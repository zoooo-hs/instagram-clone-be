package com.zoooohs.instagramclone.domain.comment.service;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.CommentRepository;
import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
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

import java.util.List;
import java.util.Optional;

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
    PostRepository postRepository;
    private UserDto userDto;
    private Long postId;
    private CommentDto commentDto;
    private PostEntity post;
    private PageModel pageModel;

    @BeforeEach
    public void setUp() {
        commentService = new CommentServiceImpl(modelMapper, commentRepository, postRepository);

        userDto = UserDto.builder().id(1L).build();
        postId = 1L;
        commentDto = CommentDto.builder().content("content").build();
        post = PostEntity.builder().id(postId).build();

        pageModel = PageModel.builder().index(0).size(20).build();
    }


    @DisplayName("commentBody, user, postId 입력받아 comment id 포함된 comment Body 반환, db에 저장, postId가 없는 post일 경우 404, POST_NOT_FOUND")
    @Test
    public void createTest() {
        CommentEntity comment = new CommentEntity();
        comment.setId(1L);
        comment.setContent(commentDto.getContent());


        given(this.postRepository.findById(eq(postId))).willReturn(Optional.ofNullable(post));
        given(this.postRepository.findById(eq(2L))).willReturn(Optional.ofNullable(null));
        given(this.commentRepository.save(any(CommentEntity.class))).willReturn(comment);

        CommentDto actual = commentService.create(commentDto, postId, userDto);

        assertTrue(actual.getId() != null);
        assertEquals(commentDto.getContent(), actual.getContent());

        try {
            commentService.create(commentDto, 2L, userDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("Long postId 받아 comment list반환하는 service 테스트")
    @Test
    public void getPostCommentListTest() {
        Long postId = 1L;

        given(this.postRepository.findById(postId)).willReturn(Optional.ofNullable(post));

        List<CommentDto> actual = this.commentService.getPostCommentList(postId, pageModel);

        assertNotEquals(null, actual);
        assertTrue(actual.size() <= pageModel.getSize());
    }

    @DisplayName("없는 postId의 경우 POST_NOT_FOUND Exception throw 하는 service테스트")
    @Test
    public void getPostCommentList404FailureTest() {
        Long postId = 1L;

        given(this.postRepository.findById(postId)).willReturn(Optional.ofNullable(null));

        try {
            this.commentService.getPostCommentList(postId, pageModel);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

}
