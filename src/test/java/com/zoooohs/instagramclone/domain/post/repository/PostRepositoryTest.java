package com.zoooohs.instagramclone.domain.post.repository;

import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.PostCommentRepository;
import com.zoooohs.instagramclone.domain.hashtag.entity.HashTagEntity;
import com.zoooohs.instagramclone.domain.like.entity.PostLikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.PostLikeRepository;
import com.zoooohs.instagramclone.domain.post.entity.PostEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostCommentRepository postCommentRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

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

    @Test
    public void findByIdAndUserIdTest() {
        this.postRepository.save(post1);

        PostEntity actual = this.postRepository.findByIdAndUserId(post1.getId(), user.getId());
        PostEntity nullActual = this.postRepository.findByIdAndUserId(post1.getId(), 123L);

        assertEquals(post1.getId(), actual.getId());
        assertEquals(post1.getDescription(), actual.getDescription());
        assertEquals(null, nullActual);
    }

    @Test
    public void deleteByIdTest() {
        post1 = this.postRepository.save(post1);

        this.postRepository.delete(post1);
        Optional<PostEntity> actual = this.postRepository.findById(post1.getId());

        assertTrue(actual.isEmpty());
    }

    @DisplayName("댓글이 달린 게시글 삭제시 댓글, 좋아요 같이 삭제")
    @Test
    public void deleteCascadeTest() {
        post1 = this.postRepository.save(post1);

        PostCommentEntity comment = PostCommentEntity.builder()
                .post(post1)
                .user(user)
                .content("asdf")
                .build();

        postCommentRepository.save(comment);

        PostLikeEntity like = PostLikeEntity.builder()
                .post(post1)
                .user(user)
                .build();

        postLikeRepository.save(like);

        this.postRepository.delete(post1);
        Optional<PostEntity> actual = this.postRepository.findById(post1.getId());

        assertTrue(actual.isEmpty());
    }

    @DisplayName("userId list로 post entity list select 하는 post repository")
    @Test
    public void findAllByUserIdTest() {
        Date now = new Date();
        String testEmail = "tt-sign-up-test-id"+now.getTime()+"@email.com";
        String testPassword = "passwd";
        String testName = "sign-up-test-name"+now.getTime();
        UserEntity user2 = new UserEntity();
        user2.setEmail(testEmail);
        user2.setPassword(passwordEncoder.encode(testPassword));
        user2.setName(testName);
        this.userRepository.save(user2);
        post1.setUser(user2);
        post1 = this.postRepository.save(post1);
        PostEntity post2 = PostEntity.builder().description("post2").user(user).build();
        this.postRepository.save(post2);


        List<PostEntity> actual = postRepository.findAllByUserId(List.of(user.getId(), user2.getId()), PageRequest.of(0, 20));

        assertEquals(2, actual.size());
        // order by desc
        assertEquals(post1.getId(), actual.get(1).getId());
        assertEquals(post2.getId(), actual.get(0).getId());
    }

    @DisplayName("tag로 모든 게시글 찾는 레포지토리 테스트")
    @Test
    public void findAllByTag() {
        List<String> tags = List.of("#hello", "#world", "#bye_bye_bye");
        Set<HashTagEntity> hashTagEntities = tags.stream().map(tag -> HashTagEntity.builder().post(post1).tag(tag).build()).collect(Collectors.toSet());
        post1.setHashTags(hashTagEntities);
        post1.setDescription("#world #hello #bye_bye_bye hihi");
        postRepository.save(post1);

        List<PostEntity> actual = postRepository.findAllByTag("#hello", PageRequest.of(0, 20));

        assertEquals(1, actual.size());
        assertTrue(actual.get(0).getDescription().contains("#hello"));
    }
}
