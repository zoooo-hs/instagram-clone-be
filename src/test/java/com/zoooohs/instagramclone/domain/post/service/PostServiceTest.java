package com.zoooohs.instagramclone.domain.post.service;

import com.zoooohs.instagramclone.domain.common.model.PageModel;
import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.common.type.SearchKeyType;
import com.zoooohs.instagramclone.domain.file.service.StorageService;
import com.zoooohs.instagramclone.domain.follow.entity.FollowEntity;
import com.zoooohs.instagramclone.domain.follow.repository.FollowRepository;
import com.zoooohs.instagramclone.domain.hashtag.service.HashTagService;
import com.zoooohs.instagramclone.domain.like.entity.PostLikeEntity;
import com.zoooohs.instagramclone.domain.photo.entity.PhotoEntity;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    PostService postService;

    @Spy
    ModelMapper modelMapper;
    @Mock
    PostRepository postRepository;
    @Mock
    FollowRepository followRepository;
    @Mock
    StorageService storageService;
    @Mock
    HashTagService hashTagService;

    PostDto.Post post;
    UserDto user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        postService = new PostServiceImpl(postRepository, followRepository, modelMapper, storageService, hashTagService);
        user = UserDto.builder().id(1L).build();
        UserDto.Feed userFeed = this.modelMapper.map(user, UserDto.Feed.class);
        post = PostDto.Post.builder().description("some desc").user(userFeed).photos(new ArrayList<>()).build();
    }

    @Test
    public void createPostTest() {
        PostEntity postEntity = this.modelMapper.map(post, PostEntity.class);
        postEntity.setId(1L);

        List<MultipartFile> files = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MockMultipartFile file =
                    new MockMultipartFile("files", String.format("file_%d.txt", i),
                            MediaType.TEXT_PLAIN_VALUE, String.format("some contents %d", i).getBytes());
            files.add(file);
        }
        List<String> photoPaths = files.stream().map(file -> UUID.randomUUID().toString()).collect(Collectors.toList());
        Set<PhotoEntity> photos = photoPaths.stream().map(path -> PhotoEntity.builder().path(path).build()).collect(Collectors.toSet());
        postEntity.setPhotos(photos);

        given(storageService.store(eq(files))).willReturn(photoPaths);
        given(postRepository.save(any(PostEntity.class))).willReturn(postEntity);

        PostDto.Post actual = this.postService.create(post, files, user);

        assertTrue(actual.getId() != null);
        assertEquals(post.getDescription(), actual.getDescription());
        assertEquals(files.size(), actual.getPhotos().size());
    }

    @DisplayName("userId 자신과 팔로워들의 게시글 dto list 반환")
    @Test
    public void getFeedsTest() {
        List<PostEntity> posts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PostEntity post = new PostEntity();
            post.setDescription("desc"+i);
            post.setUser(UserEntity.builder().id(user.getId()).build());
            posts.add(post);
        }

        List<FollowEntity> followEntities = new ArrayList<>();

        SearchModel searchModel = new SearchModel();
        searchModel.setIndex(0);
        searchModel.setSize(20);


        given(followRepository.findByUserId(eq(user.getId()))).willReturn(followEntities);
        given(this.postRepository.findAllByUserId(anyList(), eq(PageRequest.of(0, 20)))).willReturn(posts.subList(0, 20));

        List<PostDto.Post> actual = postService.getFeeds(user.getId(), searchModel);


        assertTrue(20 >= actual.size());
        assertTrue(0 < actual.size());
    }

    @DisplayName("userId 자신과 팔로워들의 게시글 dto list 반환")
    @Test
    public void getFeedsByHashTagTest() {
        List<PostEntity> posts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PostEntity post = new PostEntity();
            post.setDescription("#hello desc"+i);
            post.setUser(UserEntity.builder().id(user.getId()).build());
            posts.add(post);
        }

        SearchModel searchModel = new SearchModel();
        searchModel.setKeyword("#hello");
        searchModel.setSearchKey(SearchKeyType.HASH_TAG);
        searchModel.setIndex(0);
        searchModel.setSize(20);

        given(postRepository.findAllByTag(eq("#hello"), eq(PageRequest.of(0, 20)))).willReturn(posts.subList(0, 20));

        List<PostDto.Post> actual = postService.getFeeds(user.getId(), searchModel);

        searchModel.setSearchKey(SearchKeyType.NAME);
        List<PostDto.Post> actualZeroSize = postService.getFeeds(user.getId(), searchModel);


        assertTrue(20 >= actual.size());
        assertTrue(0 < actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertTrue(actual.get(i).getDescription().contains("#hello"));
        }
        assertEquals(0, actualZeroSize.size());
    }

    @Test
    public void findAllExceptSelfTest() {
        UserEntity anotherUser = UserEntity.builder().build();
        anotherUser.setId(2L);
        List<PostEntity> posts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PostEntity post = new PostEntity();
            post.setDescription("desc"+i);
            post.setUser(anotherUser);
            posts.add(post);
        }

        given(this.postRepository.findAllExceptUserId(eq(user.getId()), eq(PageRequest.of(0, 20)))).willReturn(posts.subList(0, 20));

        List<PostDto.Post> actual = this.postService.findAllExceptSelf(user.getId(), PageModel.builder().index(0).size(20).build());

        assertTrue(20 >= actual.size());
        assertTrue(0 < actual.size());
        for (PostDto.Post p: actual) {
            assertTrue(user.getId() != p.getUser().getId());
        }
    }

    @Test
    public void findByUserIdTest() {
        UserEntity userEntity = this.modelMapper.map(user, UserEntity.class);
        List<PostEntity> posts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PostEntity post = new PostEntity();
            post.setDescription("desc"+i);
            post.setUser(userEntity);
            PostLikeEntity like = PostLikeEntity.builder().user(userEntity).post(post).build();
            Set<PostLikeEntity> likes = new HashSet();
            likes.add(like);
            post.setLikes(likes);

            posts.add(post);
        }

        given(this.postRepository.findByUserId(eq(user.getId()), eq(PageRequest.of(0, 20)))).willReturn(posts.subList(0, 20));

        List<PostDto.Post> actual = this.postService.findByUserId(user.getId(), PageModel.builder().index(0).size(20).build(), user.getId());

        assertTrue(20 >= actual.size());
        assertTrue(0 < actual.size());
        for (PostDto.Post p: actual) {
            assertTrue(user.getId() == p.getUser().getId());
            assertNotNull(p.getLikeCount());
            assertNotNull(p.isLiked());
        }
    }

    @Test
    public void updateDescriptionTest() {
        UserDto.Feed userFeed = this.modelMapper.map(user, UserDto.Feed.class);
        PostDto.Post post2 = PostDto.Post.builder().user(userFeed).description("another desc").photos(new ArrayList<>()).build();

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
    public void updateDescriptionFailureTest() {
        user.setId(2L);
        UserDto user2 = UserDto.builder().id(3L).build();

        PostDto.Post post2 = PostDto.Post.builder().id(2L).description("another desc").build();

        given(this.postRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        try {
            this.postService.updateDescription(2L, post2, user);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }

        try {
            this.postService.updateDescription(1L, post, user);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }

        try {
            this.postService.updateDescription(1L, post, user2);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void deleteByIdTest() {
        Long postId = 1L;
        Long userId = 1L;
        PostEntity postEntity = this.modelMapper.map(post, PostEntity.class);
        postEntity.setId(postId);

        given(postRepository.findByIdAndUserId(eq(postId), eq(userId))).willReturn(postEntity);
        doNothing().when(postRepository).delete(postEntity);

        Long actual = this.postService.deleteById(postId, userId);
        assertEquals(postId, actual);

        given(postRepository.findByIdAndUserId(eq(postId), eq(userId))).willReturn(null);
        try {
            this.postService.deleteById(postId, userId);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }
}
