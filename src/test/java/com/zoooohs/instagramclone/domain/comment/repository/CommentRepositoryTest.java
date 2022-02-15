package com.zoooohs.instagramclone.domain.comment.repository;

import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.CommentLikeRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;

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

    @DisplayName("comment save 테스트")
    @Test
    public void saveTest() {
        CommentEntity comment = new CommentEntity();
        comment.setContent("content");
        comment.setUser(user);
        comment.setPost(post);

        CommentEntity actual = this.commentRepository.save(comment);

        assertEquals(comment.getContent(), actual.getContent());
        assertTrue(actual.getId() != null);
    }

    @DisplayName("comment repository findByPostId, postId로 comment list 받아오기 테스트")
    @Test
    public void findByPostIdTest() {
        List<CommentEntity> testList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            CommentEntity comment = new CommentEntity();
            comment.setContent("content");
            comment.setUser(user);
            comment.setPost(post);
            testList.add(comment);
        }
        this.commentRepository.saveAll(testList);

        int pageSize = 20;
        Pageable pageable = PageRequest.of(0, pageSize);

        List<CommentEntity> actual = this.commentRepository.findByPostId(post.getId(), pageable);

        assertNotEquals(null, actual);
        assertEquals(pageSize, actual.size());
    }

    @DisplayName("findByPostId comment entity list SELECT 할때 likes같이 반환")
    @Test
    public void findByPostIdWithLikesTest() {
        List<CommentEntity> testList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            CommentEntity comment = new CommentEntity();
            comment.setContent("content");
            comment.setUser(user);
            comment.setPost(post);
            testList.add(comment);
        }

        this.commentRepository.saveAll(testList);

        CommentLikeEntity commentLike = new CommentLikeEntity();
        commentLike.setComment(testList.get(0));
        commentLike.setUser(user);

        commentLikeRepository.save(commentLike);

        entityManager.flush();
        entityManager.clear();


        int pageSize = 20;
        Pageable pageable = PageRequest.of(0, pageSize);

        List<CommentEntity> actual = this.commentRepository.findByPostId(post.getId(), pageable);

        assertNotEquals(null, actual);
        assertEquals(pageSize, actual.size());
        assertEquals(1, actual.get(0).getLikeCount());
        assertTrue(actual.get(0).getLikes().stream().filter(l -> l.getUser().getId().equals(user.getId())).findFirst().isPresent());
    }

    @DisplayName("commentRepository.findByIdAndUserId commentId, userId 받아와서 해당 유저의 댓글 가져오는 기능 테스트")
    @Test
    public void findByIdAndUserIdTest() {
        CommentEntity comment = new CommentEntity();
        comment.setContent("content22");
        comment.setUser(user);
        comment.setPost(post);
        comment = this.commentRepository.save(comment);

        CommentEntity actual = this.commentRepository.findByIdAndUserId(comment.getId(), user.getId());
        CommentEntity actualNull = this.commentRepository.findByIdAndUserId(comment.getId(), user.getId()+3L);

        assertNotNull(comment);
        assertEquals(comment.getId(), actual.getId());
        assertEquals(comment.getContent(), actual.getContent());
        assertNull(actualNull);
    }

    @DisplayName("comment Repository delete(commentEntity)로 댓글 삭제 테스트")
    @Test
    public void deleteTest() {
        CommentEntity comment = new CommentEntity();
        comment.setContent("content22");
        comment.setUser(user);
        comment.setPost(post);
        comment = this.commentRepository.save(comment);

        Long commentId = comment.getId();

        this.commentRepository.delete(comment);

        assertThrows(Exception.class, () -> commentRepository.findById(commentId).orElseThrow(() -> new Exception()));
    }
}
