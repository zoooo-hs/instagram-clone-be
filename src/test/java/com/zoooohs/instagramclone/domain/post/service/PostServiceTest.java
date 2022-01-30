package com.zoooohs.instagramclone.domain.post.service;

import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    PostService postService;

    @Spy
    ModelMapper modelMapper;
    @Mock
    PostRepository postRepository;

    PostDto.Post post;
    UserDto user;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        postService = new PostServiceImpl(postRepository, modelMapper);
        user = UserDto.builder().id(1L).build();
        UserDto.Feed userFeed = this.modelMapper.map(user, UserDto.Feed.class);
        post = PostDto.Post.builder().description("some desc").user(userFeed).build();
    }

    @Test
    public void shouldUpdatePostDescription() {
        UserDto.Feed userFeed = this.modelMapper.map(user, UserDto.Feed.class);
        PostDto.Post post2 = PostDto.Post.builder().user(userFeed).description("another desc").build();

        PostEntity postEntity1 = this.modelMapper.map(post, PostEntity.class);
        postEntity1.setId(1L);
        PostEntity postEntity2 = this.modelMapper.map(post2, PostEntity.class);
        postEntity2.setId(2L);

        given(this.postRepository.findById(1L)).willReturn(Optional.of(postEntity1));
        given(this.postRepository.findById(2L)).willReturn(Optional.of(postEntity2));
        given(this.postRepository.save(eq(postEntity1))).willReturn(postEntity1);
        given(this.postRepository.save(eq(postEntity2))).willReturn(postEntity2);

        PostDto.Post actual1 = this.postService.updateDescription(1L, post , user);
        PostDto.Post actual2 = this.postService.updateDescription(2L, post2 , user);

        assertEquals(post.getDescription(), actual1.getDescription());
        assertEquals(1L, actual1.getId());
        assertEquals(post2.getDescription(), actual2.getDescription());
        assertEquals(2L, actual2.getId());
    }

    @Test
    public void failedUpdateDescription404() {
        user.setId(2L);
        UserDto user2 = UserDto.builder().id(3L).build();

        PostDto.Post post2 = PostDto.Post.builder().id(2L).description("another desc").build();

        given(this.postRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        assertThrows(ZooooException.class, () -> this.postService.updateDescription(2L, post2, user));
        assertThrows(ZooooException.class, () -> this.postService.updateDescription(1L, post, user));
        assertThrows(ZooooException.class, () -> this.postService.updateDescription(1L, post, user2));
    }

}
