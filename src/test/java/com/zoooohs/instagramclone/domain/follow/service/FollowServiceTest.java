package com.zoooohs.instagramclone.domain.follow.service;

import com.zoooohs.instagramclone.domain.follow.dto.FollowDto;
import com.zoooohs.instagramclone.domain.follow.entity.FollowEntity;
import com.zoooohs.instagramclone.domain.follow.repository.FollowRepository;
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
}
