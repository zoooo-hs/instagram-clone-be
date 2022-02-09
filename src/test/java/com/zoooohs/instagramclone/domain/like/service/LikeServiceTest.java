package com.zoooohs.instagramclone.domain.like.service;

import com.zoooohs.instagramclone.domain.like.dto.LikeDto;
import com.zoooohs.instagramclone.domain.like.entity.LikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.LikeRepository;
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
    LikeRepository likeRepository;
    @Spy
    ModelMapper modelMapper;
    private UserDto userDto;
    private Long postId;
    private LikeEntity like;
    private PostEntity post;

    @BeforeEach
    public void setUp() {
        likeService = new LikeServiceImpl(postRepository, likeRepository, modelMapper);
        userDto = UserDto.builder().id(1L).build();
        postId = 1L;
        post = PostEntity.builder().id(postId).build();
        like = LikeEntity.builder().user(UserEntity.builder().id(1L).build()).post(post).build();
        like.setId(1L);

    }

    @DisplayName("postId, userDto 입력 받아 like Entity 저장하고 like dto 반환하는 서비스")
    @Test
    public void likeTest() {
        given(postRepository.findByIdAndUserId(eq(postId), eq(userDto.getId()))).willReturn(post);
        given(likeRepository.save(any(LikeEntity.class))).willReturn(like);

        LikeDto actual = likeService.like(postId, userDto);

        assertNotNull(actual.getId());
        assertEquals(postId, actual.getPost().getId());
    }

    @DisplayName("postId가 없을 경우 post not found 예외 쓰로우 테스트")
    @Test
    public void likeFailure404Test() {
        try {
            likeService.like(2L, userDto);
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
        given(likeRepository.findByPostIdAndUserId(eq(postId), eq(userDto.getId()))).willReturn(like);

        Long actual = likeService.unlike(postId, userDto);

        assertEquals(1L, actual);
    }

    @DisplayName("postid, userdto 에 맞는 like없을 경우 like not found 예외 쓰로우")
    @Test
    public void unlikeFailure404Test() {
        try {
            likeService.unlike(2L, userDto);
        } catch (ZooooException e) {
            assertEquals(ErrorCode.LIKE_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

}
