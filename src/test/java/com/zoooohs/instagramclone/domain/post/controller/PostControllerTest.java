package com.zoooohs.instagramclone.domain.post.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.configure.WithAuthUser;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.service.PostService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
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
    controllers = PostController.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = SecurityConfiguration.class
        )
    }
)
@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Spy
    ModelMapper modelMapper;
    ObjectMapper objectMapper;

    @MockBean
    PostService postService;

    UserDto user;
    PostDto.Post post;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        user = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@test.test")
                .build();
        post = PostDto.Post.builder()
                .id(1L)
                .user(modelMapper.map(user, UserDto.Feed.class))
                .description("Hello World!")
                .build();
    }


    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void createTest() throws Exception {
        String url = "/post";

        given(postService.create(eq(post), any(UserDto.class))).willReturn(post);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(post)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("Hello World!")));

        post.setDescription("Bye World!");
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(post)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("Bye World!")));
    }

    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void readTest() throws Exception {
        String url = String.format("/user/%d/post", user.getId());

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .queryParam("index", "0")
                .queryParam("size", "20"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void findAllByUserIdTest() throws Exception {
        String url = "/post";

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .queryParam("index", "0")
                .queryParam("size", "20"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L)
    public void updateDescriptionTest() throws Exception {
        String url = String.format("/post/%d/description", 1l);

        given(postService.updateDescription(anyLong(), eq(post), any(UserDto.class))).willReturn(post);

        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(post)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("Hello World!")));

        post.setDescription("Bye World!");
        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(post)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("Bye World!")));
    }

    @Test
    @WithAuthUser(email = "user1@test.test", id = 2L)
    public void updateDescriptionFailureTest() throws Exception {
        String url = String.format("/post/%d/description", 1l);

        given(postService.updateDescription(anyLong(), any(PostDto.Post.class), any(UserDto.class))).willThrow(new ZooooException(ErrorCode.POST_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(post)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
