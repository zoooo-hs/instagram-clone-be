package com.zoooohs.instagramclone.domain.like.repository;

import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
import com.zoooohs.instagramclone.domain.comment.repository.PostCommentRepository;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import com.zoooohs.instagramclone.domain.like.entity.LikeEntity;
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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LikeRepositoryTest {

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostCommentRepository postCommentRepository;

    @Autowired
    EntityManager entityManager;

    private PasswordEncoder passwordEncoder;
    private PostEntity post;
    private UserEntity user;
    private PostCommentEntity comment;

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

        comment = PostCommentEntity.builder().post(post).user(user).content("some content").build();
        comment = postCommentRepository.save(comment);
    }

    @DisplayName("LikeEntity 저장 후 CommentLikeEntity로 Casting")
    @Test
    public void saveTest() {
        CommentLikeEntity like = CommentLikeEntity.builder().user(user).comment(comment).build();
        CommentLikeEntity actual = likeRepository.save(like);

        assertTrue(actual.getId() != null);
        assertEquals(like.getComment().getId(), actual.getComment().getId());
    }

    @DisplayName("LikeEntity 저장 후 LikeRepository에서 id로 읽어와도 PostCommentEntity로 Casting")
    @Test
    public void findTest() {
        CommentLikeEntity like = CommentLikeEntity.builder().user(user).comment(comment).build();
        likeRepository.save(like);

        Long likeId = like.getId();

        Optional<LikeEntity> maybeCommentLike = likeRepository.findById(likeId);

        CommentLikeEntity actual = maybeCommentLike.filter(likeEntity -> likeEntity instanceof CommentLikeEntity).map(CommentLikeEntity.class::cast).orElse(null);

        assertNotNull(actual);
        assertTrue(actual.getId() != null);
        assertEquals(like.getComment().getId(), actual.getComment().getId());
    }

    @DisplayName("LikeEntity를 삭제하면 CommentLike도 같이 삭제된다")
    @Test
    public void deleteLikeEntityTest() {
        CommentLikeEntity like = CommentLikeEntity.builder().user(user).comment(comment).build();
        likeRepository.save(like);

        likeRepository.deleteById(like.getId());


        List<CommentLikeEntity> actual = commentLikeRepository.findAll();

        assertEquals(0, actual.size());
    }

    @DisplayName("없는 ID 삭제는 안된다.")
    @Test
    public void deleteNullTest() {
        try {
            likeRepository.deleteById(3L);
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(true);
        }
    }
}
