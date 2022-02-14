package com.zoooohs.instagramclone.domain.user.service;

import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.common.type.SearchKeyType;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Spy
    ModelMapper modelMapper;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, modelMapper);
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
        assertEquals(profilePhotoPath, actual.getProfilePhoto().getPath());
    }

    private UserEntity makeUserEntity(Long id, String name, String bio, String profilePhotoPath) {
        PhotoEntity photoEntity = new PhotoEntity();
        photoEntity.setPath(profilePhotoPath);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setName(name);
        userEntity.setBio(bio);
        userEntity.setProfilePhoto(photoEntity);
        return userEntity;
    }

    @Test
    public void shouldUpdateBio() {
        Long id = 1L;
        String bio = "some bio";

        String changedBio = "another bio";

        UserDto userDto = new UserDto();
        userDto.setId(id);
        UserEntity userEntity = makeUserEntity(id, null, bio, null);
        UserEntity changedEntity = makeUserEntity(id, null, changedBio, null);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(userEntity));
        given(userRepository.save(any(UserEntity.class))).willReturn(changedEntity);

        UserDto.Info actual = userService.updateBio(changedBio, userDto);

        assertEquals(changedBio, actual.getBio());
    }

    @DisplayName("keyword, search_key=name, index, size 입력받아 keyword와 유사한 이름을 가진 user list 반환")
    @Test
    public void getUsersTest() {
        SearchModel searchModel = new SearchModel(0, 20, "aa", SearchKeyType.NAME);

        List<UserEntity> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            UserEntity user = UserEntity.builder().id((long)i).name("aaa").build();
            users.add(user);
        }

        given(userRepository.findByNameIgnoreCaseContaining("aa", PageRequest.of(0, 20))).willReturn(users);

        Optional<List<UserDto.Info>> maybeUsers = Optional.ofNullable(userService.getUsers(searchModel));

        long count = maybeUsers.map(List::stream)
                .map(actuals -> actuals.filter(actual -> actual.getName().contains(searchModel.getKeyword())))
                .map(Stream::count)
                .orElse((long) 0);

        assertEquals(users.size(), count);
    }

    private UserDto.Info makeUserDto(Long id, String name, String bio, String profilePhotoPath) {
        UserDto.Info userDto = new UserDto.Info();
        userDto.setId(id);
        userDto.setBio(bio);
        userDto.setName(name);
        PhotoDto.Photo photoDto = new PhotoDto.Photo();
        photoDto.setPath(profilePhotoPath);
        userDto.setProfilePhoto(photoDto);
        return userDto;
    }
}
