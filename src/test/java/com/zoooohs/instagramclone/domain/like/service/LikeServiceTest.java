package com.zoooohs.instagramclone.domain.like.service;

import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.CommentRepository;
import com.zoooohs.instagramclone.domain.like.dto.CommentLikeDto;
import com.zoooohs.instagramclone.domain.like.dto.PostLikeDto;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import com.zoooohs.instagramclone.domain.like.entity.PostLikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.CommentLikeRepository;
import com.zoooohs.instagramclone.domain.like.repository.LikeRepository;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    LikeService likeService;

    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentLikeRepository commentLikeRepository;
    @Mock
    LikeRepository likeRepository;
    @Spy
    ModelMapper modelMapper;
    private UserDto userDto;
    private Long postId;
    private PostLikeEntity postLikeEntity;
    private PostEntity postEntity;

    private Long commentId;
    private PostCommentEntity commentEntity;
    private CommentLikeEntity commentLikeEntity;
    private UserEntity userEntity;

    @BeforeEach
    public void setUp() {
        likeService = new LikeServiceImpl(postRepository, postLikeRepository, commentRepository, commentLikeRepository, modelMapper, likeRepository);
        userDto = UserDto.builder().id(1L).build();
        postId = 1L;
        postEntity = PostEntity.builder().id(postId).build();
        userEntity = UserEntity.builder().id(1L).build();
        postLikeEntity = PostLikeEntity.builder().user(userEntity).post(postEntity).build();
        postLikeEntity.setId(1L);

        commentId = 1L;
        commentEntity = PostCommentEntity.builder().post(postEntity).build();
        commentEntity.setId(commentId);
        commentLikeEntity = CommentLikeEntity.builder().comment(commentEntity).user(userEntity).build();
        commentLikeEntity.setId(1L);
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("postId, userDto ?????? ?????? like Entity ???????????? like dto ???????????? ????????? + postid, userId??? ?????? like??? ??????????????? ???????????? like dto??? ??????, ?????? ???????????? save ??? ??????")
    @Test
    public void likePostTest() {
        given(postRepository.findById(eq(postId))).willReturn(Optional.ofNullable(postEntity));
        given(postLikeRepository.save(any(PostLikeEntity.class))).willReturn(postLikeEntity);

        PostLikeDto actual = likeService.likePost(postId, userDto);

        assertNotNull(actual.getId());
        assertEquals(postId, actual.getPost().getId());

        // unique key ?????????
        given(postLikeRepository.findByPostIdAndUserId(eq(postId), eq(userDto.getId()))).willReturn(postLikeEntity);

        try {
            likeService.likePost(postId, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.ALREADY_LIKED_POST, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("postId??? ?????? ?????? post not found ?????? ????????? ?????????")
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

    @DisplayName("postid, userdto ?????? like entity ?????? ??? ????????? id ??????")
    @Test
    public void unlikePostTest() {
        given(postLikeRepository.findByPostIdAndUserId(eq(postId), eq(userDto.getId()))).willReturn(postLikeEntity);

        Long actual = likeService.unlikePost(postId, userDto);

        assertEquals(1L, actual);
    }

    @DisplayName("postid, userdto ??? ?????? like?????? ?????? like not found ?????? ?????????")
    @Test
    public void unlikePostFailure404Test() {
        try {
            likeService.unlikePost(2L, userDto);
        } catch (ZooooException e) {
            assertEquals(ErrorCode.LIKE_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("commentId, userDto ?????? ?????? like entity save??? like dto ?????? + commentId, userId??? ?????? like??? ??????????????? ???????????? like dto??? ??????, ?????? ???????????? save ??? ??????")
    @Test
    public void likeCommentTest() {
        given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(commentEntity));
        given(commentLikeRepository.save(any(CommentLikeEntity.class))).willReturn(commentLikeEntity);

        CommentLikeDto actual = likeService.likeComment(commentId, userDto);

        assertNotNull(actual);
        assertEquals(commentId, actual.getComment().getId());

        given(commentLikeRepository.findByCommentIdAndUserId(eq(commentId), eq(userDto.getId()))).willReturn(commentLikeEntity);

        try {
            likeService.likeComment(commentId, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.ALREADY_LIKED_COMMENT, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("commentId ???????????? comment entity?????? ?????? COMMENT_NOT_FOUND throw")
    @Test
    public void likeCommentFailure404Test() {
        given(commentRepository.findById(eq(commentId))).willReturn(Optional.ofNullable(null));

        try {
            likeService.likeComment(commentId, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.COMMENT_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("commentId, userDto ?????? ?????? commentId, userDto ???????????? comment like ?????? ??? like id ??????.")
    @Test
    public void unlikeCommentTest() {
        given(commentLikeRepository.findByCommentIdAndUserId(eq(commentId), eq(userDto.getId()))).willReturn(commentLikeEntity);

        Long actual = likeService.unlikeComment(commentId, userDto);

        assertEquals(commentLikeEntity.getId(), actual);
    }

    @DisplayName("commentId, userDto ?????? ?????? commentId, userDto ???????????? commetn like ?????? ??? like not found throw")
    @Test
    public void unlikeCommentFailure404Test() {
        given(commentLikeRepository.findByCommentIdAndUserId(eq(commentId), eq(userDto.getId()))).willReturn(null);

        try {
            likeService.unlikeComment(commentId, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.LIKE_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("likeId, userDto ?????? ?????? likeId??? ????????? ???????????? ????????? ?????? ??? id ??????")
    @Test
    public void unlikeTest() {
        Long likeId = 1L;

        given(likeRepository.findByIdAndUserId(eq(likeId), eq(userDto.getId()))).willReturn(Optional.of(new CommentLikeEntity()));

        Long actual = likeService.unlike(likeId, userDto);

        assertEquals(likeId, actual);
    }

    @DisplayName("likeId, userDto ?????? ?????? likeId??? ????????? ???????????? ????????? LIKE NOT FOUND Throw")
    @Test
    public void unlikeFailure404Test() {
        Long likeId = 1L;
        given(likeRepository.findByIdAndUserId(eq(likeId), eq(userDto.getId()))).willThrow(new ZooooException(ErrorCode.LIKE_NOT_FOUND));

        try {
            likeService.unlike(likeId, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.LIKE_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }
}
