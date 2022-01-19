package com.zoooohs.instagramclone.domain.post.service;

import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    PostService postService;
    @Spy
    ModelMapper modelMapper;
    @Mock
    PostRepository postRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        postService = new PostServiceImpl(postRepository, modelMapper);
    }

    @Test
    public void createPostTest() {
        PostDto.Post postDto = createPostDto();
        PostEntity postEntity = createPostEntity(postDto);
        UserDto userDto = createUserDto();

        given(postRepository.save(any(PostEntity.class))).willReturn(postEntity);

        PostDto.Post actual = postService.create(postDto, userDto);

        assertEquals(postDto.getDescription(), actual.getDescription());
        assertArrayEquals(postDto.getPhotos().toArray(), actual.getPhotos().toArray());
    }

    @Test
    public void readPostTest() {
        List<PostEntity> posts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PostEntity post = new PostEntity();
            post.setDescription("desc"+i);
            posts.add(post);
        }

        Pageable pageable = PageRequest.of(0, 20);
        given(postRepository.findAllExceptUserId(1L, pageable)).willReturn(posts.subList(0, 20));

        UserDto userDto = createUserDto();
        PageModel pageModel = new PageModel();
        pageModel.setIndex(0);
        pageModel.setSize(20);
        List<PostDto.Post> actual = postService.findAllExceptSelf(pageModel, userDto);
        assertEquals(20, actual.size());
        assertEquals(posts.get(0).getDescription(), actual.get(0).getDescription());
    }

    private PostEntity createPostEntity(PostDto.Post postDto) {
        PostEntity postEntity = new PostEntity();
        postEntity.setDescription(postDto.getDescription());
        List<PhotoEntity> photoEntities = postDto.getPhotos().stream().map(dto -> {
            PhotoEntity photoEntity = new PhotoEntity();
            photoEntity.setPath(dto.getPath());
            return photoEntity;
        }).collect(Collectors.toList());
        postEntity.setPhotos(photoEntities);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        postEntity.setUser(userEntity);
        return postEntity;
    }

    private UserDto createUserDto() {
        UserDto user = new UserDto();
        user.setId(1L);
        return user;
    }

    private PostDto.Post createPostDto() {
        List<PhotoDto.Photo> photos = List.of("abc").stream().map(path -> {
            PhotoDto.Photo photo = new PhotoDto.Photo();
            photo.setPath(path);
            return photo;
        }).collect(Collectors.toList());
        String description = "test description";
        PostDto.Post post = new PostDto.Post();
        post.setDescription(description);
        post.setPhotos(photos);
        return post;
    }
}
