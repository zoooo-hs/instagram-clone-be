package com.zoooohs.instagramclone.domain.comment.service;

import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.CommentRepository;
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

    @BeforeEach
    public void setUp() {
        commentService = new CommentServiceImpl(modelMapper, commentRepository, postRepository);

        userDto = UserDto.builder().id(1L).build();
        postId = 1L;
        commentDto = CommentDto.builder().content("content").build();
    }


    @DisplayName("commentBody, user, postId 입력받아 comment id 포함된 comment Body 반환, db에 저장, postId가 없는 post일 경우 404, POST_NOT_FOUND")
    @Test
    public void createTest() {
        CommentEntity comment = new CommentEntity();
        comment.setId(1L);
        comment.setContent(commentDto.getContent());

        PostEntity post = PostEntity.builder().id(1L).build();

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
}
