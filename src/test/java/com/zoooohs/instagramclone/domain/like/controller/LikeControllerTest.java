package com.zoooohs.instagramclone.domain.like.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.configure.WithAuthUser;
import com.zoooohs.instagramclone.domain.comment.dto.CommentDto;
import com.zoooohs.instagramclone.domain.like.dto.CommentLikeDto;
import com.zoooohs.instagramclone.domain.like.dto.PostLikeDto;
import com.zoooohs.instagramclone.domain.like.service.LikeService;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
        controllers = LikeController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfiguration.class
                )
        }
)
@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LikeService likeService;

    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @DisplayName("POST /post/{postId}/like 입력받아 Like Json 반환, ost /post/{postId}/like 입력 받아 없는 postId 의 경우 404 반환")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void likePostTest() throws Exception {
        String url = String.format("/post/%d/like", 1L);
        String url404 = String.format("/post/%d/like", 2L);

        given(likeService.likePost(eq(1L), any(UserDto.class))).willReturn(PostLikeDto.builder().id(1L).post(PostDto.Post.builder().id(1L).build()).build());
        given(likeService.likePost(eq(2L), any(UserDto.class))).willThrow(new ZooooException(ErrorCode.POST_NOT_FOUND));

        mockMvc.perform(post(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.instanceOf(Integer.class)));

        given(likeService.likePost(eq(1L), any(UserDto.class))).willThrow(new ZooooException(ErrorCode.ALREADY_LIKED_POST));

        mockMvc.perform(post(url))
                .andExpect(status().isConflict());

        mockMvc.perform(post(url404))
                .andExpect(status().isNotFound());
    }

    @DisplayName("DELETE /post/{postId}/like 입력 받아  id 담긴 json 반환")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void unlikePostTest() throws Exception {
        String url = String.format("/post/%d/like", 1L);
        String url404 = String.format("/post/%d/like", 2L);

        given(likeService.unlikePost(eq(1L), any(UserDto.class))).willReturn(1L);
        given(likeService.unlikePost(eq(2L), any(UserDto.class))).willThrow(new ZooooException(ErrorCode.LIKE_NOT_FOUND));

        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1"));

        mockMvc.perform(delete(url404))
                .andExpect(status().isNotFound());
    }

    @DisplayName("POST /comment/{commentId}/like, jwt 입력 받아, like json 반환")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void likeCommentTest() throws Exception {
        String url = String.format("/comment/%d/like", 1L);
        String url404 = String.format("/comment/%d/like", 2L);

        given(likeService.likeComment(eq(1L), any(UserDto.class))).willReturn(CommentLikeDto.builder().id(1L).comment(CommentDto.builder().content("cont").build()).build());
        given(likeService.likeComment(eq(2L), any(UserDto.class))).willThrow(new ZooooException(ErrorCode.COMMENT_NOT_FOUND));

        mockMvc.perform(post(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.instanceOf(Integer.class)));

        given(likeService.likeComment(eq(1L), any(UserDto.class))).willThrow(new ZooooException(ErrorCode.ALREADY_LIKED_COMMENT));

        mockMvc.perform(post(url))
                .andExpect(status().isConflict());

        mockMvc.perform(post(url404))
                .andExpect(status().isNotFound());
    }

    @DisplayName("DELETE /comment/{commentId}/like, jwt 입력 받아 like id  반환, comment 혹은 like가 없는 경우 404")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void unlikeCommentTest() throws Exception {
        String url = String.format("/comment/%d/like", 1L);
        String url404 = String.format("/comment/%d/like", 2L);

        given(likeService.unlikeComment(eq(1L), any(UserDto.class))).willReturn(1L);
        given(likeService.unlikeComment(eq(2L), any(UserDto.class))).willThrow(new ZooooException(ErrorCode.LIKE_NOT_FOUND));

        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1"));

        mockMvc.perform(delete(url404))
                .andExpect(status().isNotFound());
    }

}
