package com.zoooohs.instagramclone.domain.post.repository;

import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

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
        this.userRepository.save(user).getId();

        post1 = PostEntity.builder().description("post1").user(user).build();
    }


    @Test
    public void shouldSavePost() {
        PostEntity actual = this.postRepository.save(post1);

        assertTrue(actual.getId() != null);
        assertEquals(post1.getDescription(), actual.getDescription());
        assertEquals(user.getId(), actual.getUser().getId());
    }

    @Test
    public void shouldUpdateDescription() {
        post1 = this.postRepository.save(post1);
        post1.setDescription("another desc");
        PostEntity actual = this.postRepository.save(post1);

        assertEquals(post1.getId(), actual.getId());
        assertEquals("another desc", actual.getDescription());
    }

}
