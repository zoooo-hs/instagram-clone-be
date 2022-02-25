package com.zoooohs.instagramclone.domain.comment.repository;

import com.zoooohs.instagramclone.domain.comment.entity.CommentCommentEntity;
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

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentCommentRepositoryTest {

    @Autowired
    PostCommentRepository postCommentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentCommentRepository commentCommentRepository;

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

    @DisplayName("comment save 테스트")
    @Test
    public void saveTest() {
        CommentCommentEntity commentCommentEntity = CommentCommentEntity.builder()
                .content("new Content")
                .comment(comment)
                .user(user)
                .build();

        CommentCommentEntity actual = commentCommentRepository.save(commentCommentEntity);

        assertNotNull(actual.getId());
        assertEquals(commentCommentEntity.getContent(), actual.getContent());
    }
}
