package com.zoooohs.instagramclone.domain.user.service;

import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.common.type.SearchKeyType;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Spy
    ModelMapper modelMapper;

    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void init() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        userService = new UserServiceImpl(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    public void shouldGetUserInfo() {
        Long id = 1L;
        String name = "abc";
        String bio = "some bio";
        String profilePhotoPath = "some path";

        UserEntity userEntity = makeUserEntity(id, name, bio, profilePhotoPath);

        given(userRepository.findById(anyLong())).willReturn(java.util.Optional.of(userEntity));

        UserDto.Info actual = userService.getInfo(id);

        assertEquals(id, actual.getId());
        assertEquals(name, actual.getName());
        assertEquals(bio, actual.getBio());
        assertEquals(profilePhotoPath, actual.getPhoto().getPath());
    }

    private UserEntity makeUserEntity(Long id, String name, String bio, String profilePhotoPath) {
        PhotoEntity photoEntity = new PhotoEntity();
        photoEntity.setPath(profilePhotoPath);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setName(name);
        userEntity.setBio(bio);
        userEntity.setPhoto(photoEntity);
        return userEntity;
    }

    @DisplayName("userInfo(id, bio), userDto 입력 받아 해당 유저의 bio 변경 후 user info 반환. 유저 정보 일치하지 않으면 USER_NOT_FOUND Throw")
    @Test
    public void updateBioTest() {
        Long id = 1L;
        String bio = "some bio";

        String changedBio = "another bio";

        UserDto.Info updateInfo = UserDto.Info.builder().id(id).bio(changedBio).build();
        UserDto userDto = UserDto.builder().id(id).build();
        UserEntity userEntity = UserEntity.builder().id(id).bio(bio).build();
        UserEntity changedEntity = UserEntity.builder().id(id).bio(changedBio).build();

        given(userRepository.findById(eq(id))).willReturn(Optional.of(userEntity));
        given(userRepository.save(eq(changedEntity))).willReturn(changedEntity);

        UserDto.Info actual = userService.updateBio(updateInfo, userDto);

        assertEquals(changedBio, actual.getBio());

        try {
            updateInfo.setId(2L);
            userService.updateBio(updateInfo, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("keyword, search_key=name, index, size 입력받아 keyword와 유사한 이름을 가진 user list 반환")
    @Test
    public void getUsersTest() {
        SearchModel searchModel = new SearchModel(0, 20, null, "aa", SearchKeyType.NAME);

        List<UserEntity> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            UserEntity user = UserEntity.builder().id((long)i).name("aaa").build();
            users.add(user);
        }

        given(userRepository.findByNameIgnoreCaseContaining(anyString(), any(Pageable.class))).willReturn(users);

        // 여기까지 Optional일 필요가 있나 싶긴하다
        Optional<List<UserDto.Info>> maybeUsers = Optional.ofNullable(userService.getUsers(searchModel));

        searchModel.setSearchKey(SearchKeyType.HASH_TAG);
        Optional<List<UserDto.Info>> maybeZero = Optional.ofNullable(userService.getUsers(searchModel));

        long count = maybeUsers.map(List::stream)
                .map(actuals -> actuals.filter(actual -> actual.getName().contains(searchModel.getKeyword())))
                .map(Stream::count)
                .orElse((long) 0);

        assertEquals(users.size(), count);
        assertEquals(0, maybeZero.map(List::size).orElse(0));
    }

    private UserDto.Info makeUserDto(Long id, String name, String bio, String profilePhotoPath) {
        UserDto.Info userDto = new UserDto.Info();
        userDto.setId(id);
        userDto.setBio(bio);
        userDto.setName(name);
        PhotoDto.Photo photoDto = new PhotoDto.Photo();
        photoDto.setPath(profilePhotoPath);
        userDto.setPhoto(photoDto);
        return userDto;
    }

    @DisplayName("updatePassword Dto, userId, userDto 입력받아 비밀번호 변경 후 UserDto 반환")
    @Test
    public void updatePasswordTest() {
        Long userId = 1L;
        UserDto.UpdatePassword password = UserDto.UpdatePassword.builder().oldPassword("oldPassword").newPassword("newPassword").build();
        UserDto userDto = UserDto.builder().name("test").email("test@test.test").id(userId).build();
        UserEntity userEntity = UserEntity.builder().id(userId).name(userDto.getName()).email(userDto.getEmail())
                .password(passwordEncoder.encode(password.getOldPassword()))
                .build();

        given(userRepository.findById(eq(userId))).willReturn(Optional.of(userEntity));
        given(userRepository.save(any(UserEntity.class))).willReturn(userEntity);

        UserDto.Info actual = userService.updatePassword(userId, password, userDto);

        assertEquals(userDto.getName(), actual.getName());
        assertEquals(userDto.getId(), actual.getId());
    }

    @DisplayName("userId, userDto, password 정보 일치하지 않으면 USER_NOT_FOUND Throw")
    @Test
    public void updatePassword404Test() {
        Long userId = 1L;
        UserDto.UpdatePassword password = UserDto.UpdatePassword.builder().oldPassword("oldPassword").newPassword("newPassword").build();
        UserDto userDto = UserDto.builder().name("test").email("test@test.test").id(userId).build();

        given(userRepository.findById(eq(userId))).willReturn(Optional.ofNullable(null));

        try {
            userService.updatePassword(userId, password, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("updatePassword에 동일한 비밀번호로 변경 시도 시 SAME_PASSWORD Throw")
    @Test
    public void updatePassword409Test() {
        Long userId = 1L;
        UserDto.UpdatePassword password = UserDto.UpdatePassword.builder().oldPassword("oldPassword").newPassword("oldPassword").build();
        UserDto userDto = UserDto.builder().name("test").email("test@test.test").id(userId).build();

        try {
            userService.updatePassword(userId, password, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.SAME_PASSWORD, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("user 이름으로 사용자 조회해서 info 반환")
    @Test
    void findByNameTest() {
        given(userRepository.findByName(eq("test")))
            .willReturn(Optional.of(UserEntity.builder().id(1L).name("test").build()));

        UserDto.Info actual = userService.findByName("test");

        assertEquals("test", actual.getName());
        
        try {
            userService.findByName("test-2-not-found-name");
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }
    
}
