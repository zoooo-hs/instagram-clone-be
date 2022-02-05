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

import java.util.ArrayList;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void getPostCommentListTest() throws Exception {
        Long postId = 1L;
        String url = String.format("/post/%d/comment", postId);
        String url404 = String.format("/post/%d/comment", 2L);

        ArrayList<CommentDto> commentDtos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            CommentDto commentDto = CommentDto.builder()
                    .content("content " + i)
                    .user(UserDto.Feed.builder().name("user"+1).id((long) i).build()).build();
        }

        PageModel pageModel = PageModel.builder().index(0).size(20).build();

        given(this.commentService.getPostCommentList(postId, pageModel)).willReturn(commentDtos);
        given(this.commentService.getPostCommentList(eq(2L), any(PageModel.class))).willThrow(new ZooooException(ErrorCode.POST_NOT_FOUND));

        mockMvc.perform(get(url)
                        .param("index", "0")
                        .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(commentDtos.size())));

        mockMvc.perform(get(url404))
                .andExpect(status().isNotFound());

    }
}
