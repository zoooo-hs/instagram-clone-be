package com.zoooohs.instagramclone.domain.like.service;

import com.zoooohs.instagramclone.domain.like.dto.PostLikeDto;
import com.zoooohs.instagramclone.domain.like.entity.PostLikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.PostLikeRepository;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    LikeService likeService;

    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Spy
    ModelMapper modelMapper;
    private UserDto userDto;
    private Long postId;
    private PostLikeEntity postLikeEntity;
    private PostEntity postEntity;

    @BeforeEach
    public void setUp() {
        likeService = new LikeServiceImpl(postRepository, postLikeRepository, modelMapper);
        userDto = UserDto.builder().id(1L).build();
        postId = 1L;
        postEntity = PostEntity.builder().id(postId).build();
        postLikeEntity = PostLikeEntity.builder().user(UserEntity.builder().id(1L).build()).post(postEntity).build();
        postLikeEntity.setId(1L);

    }

    @DisplayName("postId, userDto 입력 받아 like Entity 저장하고 like dto 반환하는 서비스")
    @Test
    public void likeTest() {
        given(postRepository.findByIdAndUserId(eq(postId), eq(userDto.getId()))).willReturn(postEntity);
        given(postLikeRepository.save(any(PostLikeEntity.class))).willReturn(postLikeEntity);

        PostLikeDto actual = likeService.likePost(postId, userDto);

        assertNotNull(actual.getId());
        assertEquals(postId, actual.getPost().getId());
    }

    @DisplayName("postId가 없을 경우 post not found 예외 쓰로우 테스트")
    @Test
    public void likeFailure404Test() {
        try {
            likeService.likePost(2L, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }

    }

    @DisplayName("postid, userdto 받아 like entity 삭제 후 삭제된 id 반환")
    @Test
    public void unlikeTest() {
        given(postLikeRepository.findByPostIdAndUserId(eq(postId), eq(userDto.getId()))).willReturn(postLikeEntity);

        Long actual = likeService.unlikePost(postId, userDto);

        assertEquals(1L, actual);
    }

    @DisplayName("postid, userdto 에 맞는 like없을 경우 like not found 예외 쓰로우")
    @Test
    public void unlikeFailure404Test() {
        try {
            likeService.unlikePost(2L, userDto);
        } catch (ZooooException e) {
            assertEquals(ErrorCode.LIKE_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

}
