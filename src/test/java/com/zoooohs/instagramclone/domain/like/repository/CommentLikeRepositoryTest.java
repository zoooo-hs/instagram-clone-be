package com.zoooohs.instagramclone.domain.like.repository;

import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.CommentRepository;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
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

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentLikeRepositoryTest {

    @Autowired
    CommentLikeRepository likeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    private PasswordEncoder passwordEncoder;
    private PostEntity post;
    private UserEntity user;
    private CommentEntity comment;

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

        comment = CommentEntity.builder().post(post).user(user).content("some content").build();
        comment = commentRepository.save(comment);
    }

    @DisplayName("like entity save test")
    @Test
    public void saveTest() {
        CommentLikeEntity like = CommentLikeEntity.builder().user(user).comment(comment).build();
        CommentLikeEntity actual = likeRepository.save(like);

        assertTrue(actual.getId() != null);
        assertEquals(like.getComment().getId(), actual.getComment().getId());
    }

    @DisplayName("commentId, userid 쌍이 같은 commentLike row가 insert되어선 안된다")
    @Test
    public void commentAndUserUniqueTest() {
        CommentLikeEntity like = CommentLikeEntity.builder().user(user).comment(comment).build();
        likeRepository.save(like);
        CommentLikeEntity like2 = CommentLikeEntity.builder().user(user).comment(comment).build();

        try {
            likeRepository.save(like2);
            fail();
        } catch (DataIntegrityViolationException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("commentId, userId 입력 받아 commnet like entity 찾기")
    @Test
    public void findByCommentIdAndUserIdTest() {
        CommentLikeEntity like = CommentLikeEntity.builder().user(user).comment(comment).build();
        like = likeRepository.save(like);

        CommentLikeEntity actual = likeRepository.findByCommentIdAndUserId(comment.getId(), user.getId());

        assertEquals(like.getId(), actual.getId());
    }

}
