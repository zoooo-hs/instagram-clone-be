package com.zoooohs.instagramclone.domain.post.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoooohs.instagramclone.configuration.SecurityConfiguration;
import com.zoooohs.instagramclone.configure.WithAuthUser;
import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.service.PostService;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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
                .photos(new ArrayList<>())
                .description("Hello World!")
                .build();
    }


    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    public void createTest() throws Exception {
        String url = "/post";

        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(url);
        List<MultipartFile> files = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MockMultipartFile file =
                    new MockMultipartFile("files", String.format("file_%d.txt", i),
                            MediaType.TEXT_PLAIN_VALUE, String.format("some contents %d", i).getBytes());
            files.add(file);
            requestBuilder.file(file);
            post.getPhotos().add(new PhotoDto.Photo());
        }
        requestBuilder.param("description", post.getDescription());

        given(postService.create(any(PostDto.Post.class), anyList(), any(UserDto.class))).willReturn(post);

        String result = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("Hello World!")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.photos", Matchers.hasSize(files.size())))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    public void readTest() throws Exception {
        String url = "/post";

        List<PostDto.Post> postDtos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            PostDto.Post postDto = PostDto.Post.builder().id((long)i).description("a").isLiked(true).likeCount((long)10).build();
            postDtos.add(postDto);
        }

        given(postService.getFeeds(eq(user.getId()), any(SearchModel.class))).willReturn(postDtos);

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .queryParam("index", "0")
                .queryParam("size", "20"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].like_count", Matchers.instanceOf(Integer.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].liked", Matchers.instanceOf(Boolean.class)))
        ;
    }

    @DisplayName("hash tag 기반 게시글 조회")
    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    public void getFeedByHashTagTest() throws Exception {
        String url = "/post";

        List<PostDto.Post> postDtos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            PostDto.Post postDto = PostDto.Post.builder().id((long)i).description("#hello").isLiked(true).likeCount((long)10).build();
            postDtos.add(postDto);
        }

        given(postService.getFeeds(eq(user.getId()), any(SearchModel.class))).willReturn(postDtos);

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .queryParam("keyword", "#hello")
                        .queryParam("searchKey", "HASH_TAG")
                        .queryParam("index", "0")
                        .queryParam("size", "20"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.containsString("#hello")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].like_count", Matchers.instanceOf(Integer.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].liked", Matchers.instanceOf(Boolean.class)))
        ;
    }

    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    public void findAllByUserIdTest() throws Exception {
        String url = String.format("/user/%d/post", user.getId());

        int resultSize = 10;
        List<PostDto.Post> postDtos = new ArrayList<>();

        for (int i = 0; i < resultSize; i++) {
            PostDto.Post postDto = PostDto.Post.builder().id((long)i).description("a").isLiked(true).likeCount((long)10).user(UserDto.Feed.builder().id(1L).build()).build();
            postDtos.add(postDto);
        }



        given(postService.findByUserId(eq(user.getId()), any(PageModel.class), eq(1L))).willReturn(postDtos);

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .queryParam("index", "0")
                .queryParam("size", "20"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].like_count", Matchers.instanceOf(Integer.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].liked", Matchers.instanceOf(Boolean.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user.id", Matchers.is(1)))
        ;
    }

    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
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
    @WithAuthUser(email = "user1@test.test", id = 2L, name = "test")
    public void updateDescriptionFailureTest() throws Exception {
        String url = String.format("/post/%d/description", 1l);

        given(postService.updateDescription(anyLong(), any(PostDto.Post.class), any(UserDto.class))).willThrow(new ZooooException(ErrorCode.POST_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(post)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithAuthUser(email = "user1@test.test", id = 1L, name = "test")
    public void deleteByIdTest() throws Exception {
        String url = String.format("/post/%d", 1l);

        given(postService.deleteById(eq(1L), eq(1L))).willReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1"));

        given(postService.deleteById(eq(1L), eq(1L))).willThrow(new ZooooException(ErrorCode.POST_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
