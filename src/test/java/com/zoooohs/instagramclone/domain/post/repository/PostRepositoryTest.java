package com.zoooohs.instagramclone.domain.post.repository;

import com.zoooohs.instagramclone.domain.post.dto.PostDto;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    UserEntity user;

    PostEntity post1;

    @BeforeEach
    public void setUp() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        Date now = new Date();
        String testEmail = "tt-sign-up-test-id"+now.getTime()+"@email.com";
        String testPassword = "passwd";
        String testName = "sign-up-test-name"+now.getTime();
        user = new UserEntity();
        user.setEmail(testEmail);
        user.setPassword(passwordEncoder.encode(testPassword));
        user.setName(testName);
        this.userRepository.save(user);

        post1 = PostEntity.builder().description("post1").user(user).build();
    }

    @AfterEach
    public void tearDown() {
        this.postRepository.deleteAll();
        this.userRepository.deleteAll();
    }


    @Test
    public void saveTest() {
        PostEntity actual = this.postRepository.save(post1);

        assertTrue(actual.getId() != null);
        assertEquals(post1.getDescription(), actual.getDescription());
        assertEquals(user.getId(), actual.getUser().getId());
    }

    @Test
    public void updateDescriptionTest() {
        post1 = this.postRepository.save(post1);
        post1.setDescription("another desc");
        PostEntity actual = this.postRepository.save(post1);

        assertEquals(post1.getId(), actual.getId());
        assertEquals("another desc", actual.getDescription());
    }

    @Test
    public void findAllExceptUserIdTest() {
        List<PostEntity> posts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PostEntity post = new PostEntity();
            post.setDescription("desc" + i);
            post.setUser(user);
            posts.add(post);
        }
        this.postRepository.saveAll(posts);

        List<PostEntity> actual = this.postRepository.findAllExceptUserId(2L, PageRequest.of(0, 20));

        assertTrue(20 >= actual.size());
        assertTrue(0 < actual.size());
        for (PostEntity p : actual) {
            assertTrue(2L != p.getUser().getId());
        }
    }

    @Test
    public void findByUserIdTest() {
        List<PostEntity> posts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PostEntity post = new PostEntity();
            post.setDescription("desc" + i);
            post.setUser(user);
            posts.add(post);
        }
        this.postRepository.saveAll(posts);

        List<PostEntity> actual = this.postRepository.findByUserId(user.getId(), PageRequest.of(0, 20));

        assertTrue(20 >= actual.size());
        assertTrue(0 < actual.size());
        for (PostEntity p : actual) {
            assertTrue(user.getId() == p.getUser().getId());
        }
    }
}
