package com.zoooohs.instagramclone.domain.post.service;

import com.zoooohs.instagramclone.domain.photo.dto.PhotoDto;
import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.user.dto.UserDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ModelMapper modelMapper;

    UserDto userDto;

    @BeforeAll
    public void init() {
        Date now = new Date();
        String testEmail = "tt-sign-up-test-id"+now.getTime()+"@email.com";
        String testPassword = "passwd";
        String testName = "sign-up-test-name"+now.getTime();
        UserEntity user = new UserEntity();
        user.setEmail(testEmail);
        user.setPassword(passwordEncoder.encode(testPassword));
        user.setName(testName);
        this.userRepository.save(user);
        userDto = modelMapper.map(user, UserDto.class);
    }
    @AfterAll
    public void clean() {
        this.userRepository.deleteById(userDto.getId());
    }

    @Test
    public void createPostTest() {
        List<PhotoDto.Photo> photos = List.of("abc").stream().map(path -> {
            PhotoDto.Photo photo = new PhotoDto.Photo();
            photo.setPath(path);
            return photo;
        }).collect(Collectors.toList());
        String description = "test description";
        PostDto.Post post = new PostDto.Post();
        post.setDescription(description);
        post.setPhotos(photos);
        PostDto.Post result = postService.create(post, userDto);
        assertTrue(result.getId() != null);
    }
}
