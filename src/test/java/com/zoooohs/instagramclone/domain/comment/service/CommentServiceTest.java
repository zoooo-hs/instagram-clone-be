package com.zoooohs.instagramclone.domain.comment.service;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.entity.CommentCommentEntity;
import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.CommentCommentRepository;
import com.zoooohs.instagramclone.domain.comment.repository.CommentRepository;
import com.zoooohs.instagramclone.domain.comment.repository.PostCommentRepository;
import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    CommentService commentService;

    @Spy
    ModelMapper modelMapper;
    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentCommentRepository commentCommentRepository;
    @Mock
    PostCommentRepository postCommentRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;

    private UserDto userDto;
    private Long postId;
    private CommentDto commentDto;
    private PostEntity post;
    private PageModel pageModel;
    private PostCommentEntity postCommentEntity;

    @BeforeEach
    public void setUp() {
        commentService = new CommentServiceImpl(modelMapper, commentRepository, postCommentRepository, commentCommentRepository, postRepository, userRepository);

        userDto = UserDto.builder().id(1L).build();
        postId = 1L;
        commentDto = CommentDto.builder().content("content").build();
        post = PostEntity.builder().id(postId).build();

        pageModel = new PageModel(0, 20, null);

        postCommentEntity = new PostCommentEntity();
        postCommentEntity.setId(1L);
        postCommentEntity.setContent(commentDto.getContent());
    }


    @DisplayName("commentBody, user, postId ???????????? comment id ????????? comment Body ??????, db??? ??????, postId??? ?????? post??? ?????? 404, POST_NOT_FOUND")
    @Test
    public void createPostCommentTest() {
        given(userRepository.getById(eq(userDto.getId()))).willReturn(UserEntity.builder().id(userDto.getId()).build());
        given(this.postRepository.findById(eq(postId))).willReturn(Optional.ofNullable(post));
        given(this.postRepository.findById(eq(2L))).willReturn(Optional.ofNullable(null));
        given(this.postCommentRepository.save(any(PostCommentEntity.class))).willReturn(postCommentEntity);

        CommentDto actual = commentService.createPostComment(commentDto, postId, userDto);

        assertTrue(actual.getId() != null);
        assertEquals(commentDto.getContent(), actual.getContent());

        try {
            commentService.createPostComment(commentDto, 2L, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("commentBody, user, commentId ???????????? comment id ????????? comment Body ??????, db??? ??????, commentId??? ?????? comment??? ?????? 404, COMMENT_NOT_FOUND")
    @Test
    public void createCommentCommentTest() {
        Long commentId = 1L;
        CommentEntity comment = PostCommentEntity.builder().build();
        comment.setId(commentId);
        CommentCommentEntity commentComment = CommentCommentEntity.builder()
                .comment(comment)
                .user(UserEntity.builder().id(userDto.getId()).build())
                .content(commentDto.getContent()).build();
        commentComment.setId(2L);

        given(userRepository.getById(eq(userDto.getId()))).willReturn(UserEntity.builder().id(userDto.getId()).build());
        given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(comment));
        given(commentCommentRepository.save(any())).willReturn(commentComment);

        CommentDto actual = commentService.createCommentComment(commentDto, commentId, userDto);

        assertNotNull(actual.getId());
        assertEquals(commentDto.getContent(), actual.getContent());

        try {
            commentService.createCommentComment(commentDto, 2L, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.COMMENT_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("Long postId ?????? comment list???????????? service ?????????")
    @Test
    public void getPostCommentListTest() {
        Long postId = 1L;

        ArrayList<PostCommentEntity> commentEntities = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PostCommentEntity commentEntity = PostCommentEntity.builder()
                    .content("content " + i)
                    .user(UserEntity.builder().name("user"+1).id((long) i).build())
                    .build();
            commentEntity.setId((long) i*i);
            CommentLikeEntity commentLike = new CommentLikeEntity();
            commentLike.setId((long) i);
            commentLike.setComment(commentEntity);
            commentLike.setUser(UserEntity.builder().id(userDto.getId()).build());
            commentEntities.add(commentEntity);
            Set<CommentLikeEntity>  likes = new HashSet<>();
            likes.add(commentLike);
            commentEntity.setLikes(likes);
        }

        given(this.postRepository.findById(postId)).willReturn(Optional.ofNullable(post));
        given(postCommentRepository.findByPostIdOrderByIdDesc(eq(postId), any(Pageable.class))).willReturn(commentEntities);

        List<CommentDto> actual = this.commentService.getPostCommentList(postId, pageModel, userDto.getId());

        assertNotEquals(null, actual);
        assertTrue(actual.size() <= pageModel.getSize());
        assertNotNull(actual.get(0).getLikeCount());
        assertTrue(actual.get(0).isLiked());
        assertNotNull(actual.get(0).getLikedId());
    }

    @DisplayName("commentId ?????? ?????? comment comment list ???????????? service ?????????")
    @Test
    public void getCommentCommentListTest() {
        Long commentId = 1L;

        CommentEntity comment = PostCommentEntity.builder()
                .build();
        comment.setId(commentId);

        ArrayList<CommentCommentEntity> commentEntities = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            CommentCommentEntity commentEntity = CommentCommentEntity.builder()
                    .content("content " + i)
                    .user(UserEntity.builder().name("user"+1).id((long) i).build())
                    .build();
            commentEntity.setId((long)i*i);
            CommentLikeEntity commentLike = new CommentLikeEntity();
            commentLike.setId((long) i);
            commentLike.setComment(commentEntity);
            commentLike.setUser(UserEntity.builder().id(userDto.getId()).build());
            commentEntities.add(commentEntity);
            Set<CommentLikeEntity>  likes = new HashSet<>();
            likes.add(commentLike);
            commentEntity.setLikes(likes);
        }

        given(this.commentRepository.findById(commentId)).willReturn(Optional.ofNullable(comment));
        given(commentCommentRepository.findByCommentId(eq(commentId), any(Pageable.class))).willReturn(commentEntities);

        List<CommentDto> actual = this.commentService.getCommentCommentList(commentId, pageModel, userDto.getId());

        assertNotEquals(null, actual);
        assertTrue(actual.size() <= pageModel.getSize());
        assertNotNull(actual.get(0).getLikeCount());
        assertTrue(actual.get(0).isLiked());
        assertNotNull(actual.get(0).getLikedId());
    }

    @DisplayName("?????? postId??? ?????? POST_NOT_FOUND Exception throw ?????? service?????????")
    @Test
    public void getPostCommentList404FailureTest() {
        Long postId = 1L;

        given(this.postRepository.findById(postId)).willReturn(Optional.ofNullable(null));

        try {
            this.commentService.getPostCommentList(postId, pageModel, 1L);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("comment service  updateComment(postId, commentDto, userDto) ????????? db??? comment content ???????????? comment dto return ????????? ?????????.")
    @Test
    public void updateCommentTest() {
        String newContent = "aaa";
        commentDto.setContent(newContent);
        PostCommentEntity newComment = new PostCommentEntity();
        newComment.setId(postCommentEntity.getId());
        newComment.setContent(newContent);
        given(commentRepository.findByIdAndUserId(eq(postCommentEntity.getId()), eq(userDto.getId()))).willReturn(postCommentEntity);
        given(commentRepository.save(any(PostCommentEntity.class))).willReturn(newComment);

        CommentDto actual = commentService.updateComment(postCommentEntity.getId(), commentDto, userDto);

        assertEquals(newContent, actual.getContent());
        assertEquals(postCommentEntity.getId(), actual.getId());
    }

    @DisplayName("CommentCommentEntity ???????????? CommentDto ??????")
    @Test
    public void updateCommentCommentTest() {
        String newContent = "aaa";
        commentDto.setContent(newContent);
        CommentCommentEntity newComment = new CommentCommentEntity();
        newComment.setId(1L);
        newComment.setContent(newContent);

        given(commentRepository.findByIdAndUserId(eq(postCommentEntity.getId()), eq(userDto.getId()))).willReturn(postCommentEntity);
        given(commentRepository.save(any(CommentEntity.class))).willReturn(newComment);

        CommentDto actual = commentService.updateComment(postCommentEntity.getId(), commentDto, userDto);

        assertEquals(newContent, actual.getContent());
    }

    @DisplayName("comment service  updateComment(postId, commentDto, userDto) ????????? db??? comment ????????? COMMENT_NOT_FOUND ?????? ????????? ?????????")
    @Test
    public void updateCommentFailure404Test() {
        given(commentRepository.findByIdAndUserId(eq(postCommentEntity.getId()), eq(userDto.getId()))).willReturn(null);
        try {
            commentService.updateComment(postCommentEntity.getId(), commentDto, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.COMMENT_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("comment Id, userDto ?????? ?????? comment ????????? ????????? ????????? ?????????")
    @Test
    public void deleteByIdTest() {
        Long commentId = 1L;
        given(commentRepository.findByIdAndUserId(eq(1L), eq(userDto.getId()))).willReturn(postCommentEntity);
        Long actual = commentService.deleteById(commentId, userDto);

        assertEquals(commentId, actual);
    }

    @DisplayName("comment Id, userDto ?????? ?????? comment ????????? ????????? ??????????????? comment ????????? COMMENT_NOT_FOUND ?????? ????????? ?????????")
    @Test
    public void deleteByIdFailure404Test() {
        Long commentId = 1L;
        given(commentRepository.findByIdAndUserId(eq(1L), eq(userDto.getId()))).willReturn(null);
        try {
            commentService.deleteById(commentId, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.COMMENT_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }


}
