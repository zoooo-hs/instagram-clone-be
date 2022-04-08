package com.zoooohs.instagramclone.domain.follow.service;

import com.zoooohs.instagramclone.domain.follow.dto.FollowDto;
import com.zoooohs.instagramclone.domain.follow.entity.FollowEntity;
import com.zoooohs.instagramclone.domain.follow.repository.FollowRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    FollowService followService;

    @Mock
    FollowRepository followRepository;

    @Mock
    UserRepository userRepository;

    @Spy
    ModelMapper modelMapper;
    private Long followUserId;
    private Long userId;
    private UserEntity userEntity;
    private UserEntity followUserEntity;
    private FollowEntity followEntity;

    @BeforeEach
    public void setUp() {
        followService = new FollowServiceImpl(followRepository, userRepository, modelMapper);

        followUserId = 2L;
        userId = 1L;

        followUserEntity = UserEntity.builder().id(followUserId).build();
        userEntity = UserEntity.builder().id(userId).build();
        followEntity = FollowEntity.builder().followUser(followUserEntity).user(userEntity).build();
        followEntity.setId(1L);
    }


    @DisplayName("followUserId(팔로우 할 상대), userId(자기자신) 입력 받아 follow entity save 후 follow dto 반환")
    @Test
    public void followTest() {
        given(userRepository.findById(eq(followUserId))).willReturn(Optional.ofNullable(userEntity));
        given(followRepository.save(any(FollowEntity.class))).willReturn(followEntity);

        FollowDto actual = followService.follow(followUserId, userId);

        assertNotNull(actual);
        assertEquals(followUserId, actual.getFollowUser().getId());
    }

    @DisplayName("followUserId(팔로우 할 상대), userId(자기자신) 입력 받아 없는 user의 경우 USER_NOT_FOUND Throw")
    @Test
    public void followFailure404Test() {
        given(userRepository.findById(eq(followUserId))).willReturn(Optional.ofNullable(null));

        try {
            followService.follow(followUserId, userId);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("followUserId(팔로우 할 상대), userId(자기자신) 입력 받아 이미 follow 한 user의 경우 ALREADY_FOLLOWED throw")
    @Test
    public void followFailure409AlreadyTest() {
        given(userRepository.findById(eq(followUserId))).willReturn(Optional.ofNullable(userEntity));
        given(followRepository.save(any(FollowEntity.class))).willReturn(followEntity);
        followService.follow(followUserId, userId);
        try {
            given(followRepository.findByFollowUserIdAndUserId(eq(followUserId), eq(userId))).willReturn(followEntity);
            followService.follow(followUserId, userId);
            fail();
        } catch (ZooooException e) {
             assertEquals(ErrorCode.ALREADY_FOLLOWED_USER, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("followUserId(팔로우 할 상대), userId(자기자신) 입력 받아 자기 자신을 follow 하려는 경우 FOLLOWING_SELF throw")
    @Test
    public void followFailure409Self() {
        try {
            followService.follow(userId, userId);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.FOLLOWING_SELF, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("followUserId, userId 쌍의 follow 있을때 follow entity 삭제후 id 반환")
    @Test
    public void unfollowTest() {
        given(followRepository.findByFollowUserIdAndUserId(followUserId, userId)).willReturn(followEntity);

        Long actual = followService.unfollow(followUserId, userId);

        assertEquals(followEntity.getId(), actual);
    }

    @DisplayName("followUserId, userId 쌍의 follow 없을 때 FOLLOW_NOT_FOUND throw")
    @Test
    public void unfollowFailure404Test() {
        given(followRepository.findByFollowUserIdAndUserId(followUserId, userId)).willReturn(null);

        try {
            followService.unfollow(followUserId, userId);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.FOLLOW_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("followUserId == userId 일 경우 FOLLOWING_SELF throw")
    @Test
    public void unfollowFailure409Test() {
        try {
            followService.unfollow(userId, userId);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.FOLLOWING_SELF, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("user id 로 팔로잉하는 유저 리스트 반환")
    @Test
    void findByUserIdTest() {
        UserDto userDto = UserDto.builder().id(1L).build();

        List<FollowEntity> follows = new ArrayList<>();
        for (int i = 2; i <= 3; i++) {
            follows.add(FollowEntity.builder()
                    .user(userEntity)
                    .followUser(UserEntity.builder().id((long) i).build())
                    .build());
        }

        given(followRepository.findByUserId(eq(1L))).willReturn(follows);

        List<UserDto.Info> actual = followService.findByUserId(userId, userDto);

        assertEquals(2, actual.size());
        assertEquals(2L, actual.get(0).getId());
        assertEquals(3L, actual.get(1).getId());
    }

    @DisplayName("user id를 followUser로 갖는 follow들의 user 리스트 반환")
    @Test
    void findByFollowUserIdTest() {
        UserDto userDto = UserDto.builder().id(1L).build();

        List<FollowEntity> follows = new ArrayList<>();
        for (int i = 2; i <= 3; i++) {
            follows.add(FollowEntity.builder()
                    .user(UserEntity.builder().id((long) i).build())
                    .followUser(userEntity)
                    .build());
        }

        given(followRepository.findByFollowUserId(eq(1L))).willReturn(follows);

        List<UserDto.Info> actual = followService.findByFollowUserId(userId, userDto);

        assertEquals(2, actual.size());
        assertEquals(2L, actual.get(0).getId());
        assertEquals(3L, actual.get(1).getId());
    }
}
