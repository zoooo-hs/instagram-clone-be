package com.zoooohs.instagramclone.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.configure.WithAuthUser;
import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.service.CommentService;
import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
        controllers = CommentController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfiguration.class
                )
        }
)
@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }


    @DisplayName("POST /post/{postId}/comment body, jwt 입력 받아, comment json return")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void isOkTest() throws Exception {
        Long postId = 1L;
        String content = "comment content";
        String url = String.format("/post/%d/comment", postId);
        CommentDto commentDto = CommentDto.builder().content(content).build();
        commentDto.setId(1L);

        given(this.commentService.create(any(CommentDto.class), anyLong(), any(UserDto.class))).willReturn(commentDto);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.instanceOf(Integer.class)))
                .andExpect(jsonPath("$.content", Matchers.is(content)));
    }

    @DisplayName("GET /post/{postId}/comment 입력, comment 리스트 반환, 없는 postid의 경우 404")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void getPostCommentListTest() throws Exception {
        Long postId = 1L;
        String url = String.format("/post/%d/comment", postId);
        String url404 = String.format("/post/%d/comment", 2L);

        ArrayList<CommentDto> commentDtos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            CommentDto commentDto = CommentDto.builder()
                    .content("content " + i)
                    .user(UserDto.Feed.builder().name("user"+1).id((long) i).build())
                    .likeCount((long) 0)
                    .isLiked(false)
                    .build();
            commentDtos.add(commentDto);
        }

        PageModel pageModel = PageModel.builder().index(0).size(20).build();

        given(this.commentService.getPostCommentList(eq(postId), any(PageModel.class), eq(1L))).willReturn(commentDtos);
        given(this.commentService.getPostCommentList(eq(2L), any(PageModel.class), eq(1L))).willThrow(new ZooooException(ErrorCode.POST_NOT_FOUND));

        mockMvc.perform(get(url)
                        .param("index", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(commentDtos.size())))
                .andExpect(jsonPath("$[0].like_count", Matchers.instanceOf(Integer.class)))
                .andExpect(jsonPath("$[0].liked", Matchers.instanceOf(Boolean.class)))
        ;

        mockMvc.perform(get(url404))
                .andExpect(status().isNotFound());

    }

    @DisplayName("PATCH /comment/{commentId}, body, jwt 입력 받아 comment json 반환. 없는 comment의 경우 404 return")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void updateCommentTest() throws Exception {
        String url = String.format("/comment/%d", 1L);
        String url404 = String.format("/comment/%d", 2L);
        String content = "another content";
        CommentDto commentDto = CommentDto.builder().content(content).build();

        given(this.commentService.updateComment(eq(1L), eq(commentDto), any(UserDto.class))).willReturn(commentDto);
        given(this.commentService.updateComment(eq(2L), eq(commentDto), any(UserDto.class))).willThrow(new ZooooException(ErrorCode.COMMENT_NOT_FOUND));

        mockMvc.perform(patch(url).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.is(content)));


        mockMvc.perform(patch(url404).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(commentDto)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("DELETE /comment/{commentId}, jwt 입력 받아, 댓글 삭제후 댓글 id 담긴 json 반환. 없는 댓글의 경우 404 반환")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void deleteByIdTest() throws Exception {
        String url = String.format("/comment/%d", 1L);
        String url404 = String.format("/comment/%d", 2L);

        given(this.commentService.deleteById(eq(1L), any(UserDto.class))).willReturn(1L);
        given(this.commentService.deleteById(eq(2L), any(UserDto.class))).willThrow(new ZooooException(ErrorCode.COMMENT_NOT_FOUND));

        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1"));

        mockMvc.perform(delete(url404))
                .andExpect(status().isNotFound());
    }
}
