package com.zoooohs.instagramclone.domain.like.repository;

import com.zoooohs.instagramclone.domain.like.entity.PostLikeEntity;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.post.repository.PostRepository;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostLikeRepositoryTest {

    @Autowired
    PostLikeRepository likeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager entityManager;

    private PasswordEncoder passwordEncoder;
    private PostEntity post;
    private UserEntity user;

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
        user = this.userRepository.save(user);
        post = PostEntity.builder().description("post1").user(user).build();
        post = this.postRepository.save(post);
    }

    @DisplayName("like entity save test")
    @Test
    public void saveTest() {
        PostLikeEntity like = PostLikeEntity.builder().user(user).post(post).build();
        PostLikeEntity actual = likeRepository.save(like);

        assertTrue(actual.getId() != null);
        assertEquals(like.getPost().getId(), actual.getPost().getId());
    }

    // Entity ?????? Join Patter ????????? ????????? ???????????? ?????? ????????? ???
    @DisplayName("postid, userid ?????? ?????? postLike row??? insert????????? ?????????")
    public void postAndUserUniqueTest() {
        PostLikeEntity like = PostLikeEntity.builder().user(user).post(post).build();
        likeRepository.save(like);
        PostLikeEntity like2 = PostLikeEntity.builder().user(user).post(post).build();

        try {
            likeRepository.save(like2);
            fail();
        } catch (DataIntegrityViolationException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }

    }

    @DisplayName("postId, userId???  like entity ?????? findByPostIdAndUserId ?????????")
    @Test
    public void findByPostIdAndUserIdTest() {
        PostLikeEntity like = PostLikeEntity.builder().user(user).post(post).build();
        like = likeRepository.save(like);

        PostLikeEntity actual = likeRepository.findByPostIdAndUserId(post.getId(), user.getId());

        assertNotNull(actual);
        assertEquals(like.getId(), actual.getId());
    }

    @DisplayName("PostEntity?????? likcCount query??? ?????? ??????")
    @Test
    public void likeCountTest() {
        PostLikeEntity like = PostLikeEntity.builder().user(user).post(post).build();
        likeRepository.save(like);
        entityManager.flush();
        entityManager.clear();

        PostEntity postEntity = postRepository.findByIdAndUserId(post.getId(), user.getId());

        Long actual = postEntity.getLikeCount();

        assertEquals(1L, actual);
    }

}
