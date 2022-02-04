package com.zoooohs.instagramclone.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.configure.WithAuthUser;
import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.comment.service.CommentService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.*;


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

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.instanceOf(Integer.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.is(content)));
    }
}
