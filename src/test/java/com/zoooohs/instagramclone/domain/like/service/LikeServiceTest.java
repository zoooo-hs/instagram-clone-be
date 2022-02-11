package com.zoooohs.instagramclone.domain.like.service;

import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.CommentRepository;
import com.zoooohs.instagramclone.domain.like.dto.CommentLikeDto;
import com.zoooohs.instagramclone.domain.like.dto.PostLikeDto;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import com.zoooohs.instagramclone.domain.like.entity.PostLikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.CommentLikeRepository;
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
import org.springframework.dao.DataIntegrityViolationException;

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
    @Spy
    ModelMapper modelMapper;
    private UserDto userDto;
    private Long postId;
    private PostLikeEntity postLikeEntity;
    private PostEntity postEntity;

    private Long commentId;
    private CommentEntity commentEntity;
    private CommentLikeEntity commentLikeEntity;
    private UserEntity userEntity;

    @BeforeEach
    public void setUp() {
        likeService = new LikeServiceImpl(postRepository, postLikeRepository, commentRepository, commentLikeRepository, modelMapper);
        userDto = UserDto.builder().id(1L).build();
        postId = 1L;
        postEntity = PostEntity.builder().id(postId).build();
        userEntity = UserEntity.builder().id(1L).build();
        postLikeEntity = PostLikeEntity.builder().user(userEntity).post(postEntity).build();
        postLikeEntity.setId(1L);

        commentId = 1L;
        commentEntity = CommentEntity.builder().post(postEntity).build();
        commentEntity.setId(commentId);
        commentLikeEntity = CommentLikeEntity.builder().comment(commentEntity).user(userEntity).build();
        commentLikeEntity.setId(1L);
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("postId, userDto 입력 받아 like Entity 저장하고 like dto 반환하는 서비스 + postid, userId로 이미 like가 존재한다면 존재하는 like dto를 반환, 없을 경우에만 save 후 반환")
    @Test
    public void likePostTest() {
        given(postRepository.findByIdAndUserId(eq(postId), eq(userDto.getId()))).willReturn(postEntity);
        given(postLikeRepository.save(any(PostLikeEntity.class))).willReturn(postLikeEntity);

        PostLikeDto actual = likeService.likePost(postId, userDto);

        assertNotNull(actual.getId());
        assertEquals(postId, actual.getPost().getId());

        // unique key 테스트
        given(postLikeRepository.findByPostIdAndUserId(eq(postId), eq(userDto.getId()))).willReturn(postLikeEntity);
        given(postLikeRepository.save(any(PostLikeEntity.class))).willThrow(new DataIntegrityViolationException("")); // 구현 이후엔 의미없는 stubbing 그러나 테스트를 처음 만들 당시 필요했음

        PostLikeDto actual2 = likeService.likePost(postId, userDto);

        assertEquals(actual.getId(), actual2.getId());
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

    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("commentId, userDto 입력 받아 like entity save후 like dto 반환 + commentId, userId로 이미 like가 존재한다면 존재하는 like dto를 반환, 없을 경우에만 save 후 반환")
    @Test
    public void likeCommentTest() {
        given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(commentEntity));
        given(commentLikeRepository.save(any(CommentLikeEntity.class))).willReturn(commentLikeEntity);

        CommentLikeDto actual = likeService.likeComment(commentId, userDto);

        assertNotNull(actual);
        assertEquals(commentId, actual.getComment().getId());

        given(commentLikeRepository.save(any(CommentLikeEntity.class))).willThrow(new DataIntegrityViolationException(""));

        CommentLikeDto actual2 = likeService.likeComment(commentId, userDto);
        assertEquals(actual.getId(), actual2.getId());
    }

    @DisplayName("commentId 만족하는 comment entity없을 경우 COMMENT_NOT_FOUND throw")
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

    @DisplayName("commentId, userDto 입력 받아 commentId, userDto 일치하는 comment like 삭제 후 like id 반환.")
    @Test
    public void unlikeCommentTest() {
        given(commentLikeRepository.findByCommentIdAndUserId(eq(commentId), eq(userDto.getId()))).willReturn(commentLikeEntity);

        Long actual = likeService.unlikeComment(commentId, userDto);

        assertEquals(commentLikeEntity.getId(), actual);
    }

    @DisplayName("commentId, userDto 입력 받아 commentId, userDto 일치하는 commetn like 없을 시 like not found throw")
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
}
