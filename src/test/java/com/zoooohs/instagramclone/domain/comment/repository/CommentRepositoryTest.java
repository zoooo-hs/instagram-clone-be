package com.zoooohs.instagramclone.domain.comment.repository;

import com.zoooohs.instagramclone.domain.comment.entity.CommentCommentEntity;
import com.zoooohs.instagramclone.domain.comment.entity.CommentEntity;
import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
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

import static org.junit.jupiter.api.Assertions.*;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Autowired
    PostCommentRepository postCommentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentCommentRepository commentCommentRepository;

    @Autowired
    CommentRepository commentRepository;

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
        comment = new PostCommentEntity();
        comment.setContent("content");
        comment.setUser(user);
        comment.setPost(post);
        postCommentRepository.save(comment);
    }

    @DisplayName("id로 comment 찾기 테스트")
    @Test
    public void findByIdTest() {
        CommentCommentEntity commentCommentEntity = CommentCommentEntity.builder()
                .content("new Content")
                .comment(comment)
                .user(user)
                .build();


        commentCommentRepository.save(commentCommentEntity);
        Long commentId = commentCommentEntity.getId();

        entityManager.flush();
        entityManager.clear();


        CommentEntity actual = commentRepository.findById(commentId).orElse(null);

        assertNotNull(actual);
        assertEquals(commentCommentEntity.getContent(), actual.getContent());
    }

    @DisplayName("commentRepository.findByIdAndUserId commentId, userId 받아와서 해당 유저의 댓글 가져오는 기능 테스트")
    @Test
    public void findByIdAndUserIdTest() {
        PostCommentEntity comment = new PostCommentEntity();
        comment.setContent("content22");
        comment.setUser(user);
        comment.setPost(post);
        comment = this.postCommentRepository.save(comment);

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
        PostCommentEntity comment = new PostCommentEntity();
        comment.setContent("content22");
        comment.setUser(user);
        comment.setPost(post);
        comment = this.postCommentRepository.save(comment);

        Long commentId = comment.getId();

        this.commentRepository.delete(comment);

        assertThrows(Exception.class, () -> commentRepository.findById(commentId).orElseThrow(() -> new Exception()));
    }

    @DisplayName("대댓글의 원글 삭제시, 대댓글도 같이 삭제")
    @Test
    public void deleteCascadeTest() {
        PostCommentEntity comment = new PostCommentEntity();
        comment.setContent("content22");
        comment.setUser(user);
        comment.setPost(post);
        comment = this.postCommentRepository.save(comment);

        CommentCommentEntity commentComment = CommentCommentEntity.builder()
                .comment(comment)
                .user(user)
                .content("asdf")
                .build();


        this.commentRepository.delete(comment);

        assertTrue(true);
    }
}
