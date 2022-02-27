package com.zoooohs.instagramclone.domain.comment.repository;

import com.zoooohs.instagramclone.domain.comment.entity.CommentCommentEntity;
import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.LikeRepository;
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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    LikeRepository likeRepository;
    @Autowired
    CommentCommentRepository commentCommentRepository;

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

    @DisplayName("commentId 받아와 대댓글 리스트 반환하는 Repository 테스트")
    @Test
    public void findByCommentIdTest() {
        List<CommentCommentEntity> commentCommentEntities = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            CommentCommentEntity commentComment = CommentCommentEntity.builder()
                    .content("new Content")
                    .comment(comment)
                    .user(user)
                    .build();

            commentCommentEntities.add(commentComment);
        }
        commentCommentRepository.saveAll(commentCommentEntities);

        List<CommentCommentEntity> actual = commentCommentRepository.findByCommentId(comment.getId(), PageRequest.of(0, 10));

        assertEquals(10, actual.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(comment.getId(), actual.get(i).getComment().getId());
        }
    }

    @DisplayName("like 개수로 정렬해 받아오기")
    @Test
    public void orderByLikeCountTest() {
        for (int i = 0; i < 2; i++) {
            CommentCommentEntity commentComment = new CommentCommentEntity();
            commentComment.setContent("content22");
            commentComment.setUser(user);
            commentComment.setComment(comment);
            commentComment = this.commentCommentRepository.save(commentComment);
            for (int j = i; j < 1; j++) {
                // 순서대로 좋아요 1개, 0개 집어 넣기
                CommentLikeEntity like = CommentLikeEntity.builder().user(user).comment(commentComment).build();
                likeRepository.save(like);
            }
        }

        entityManager.flush();
        entityManager.clear();

        List<CommentCommentEntity> actual = commentCommentRepository.findCommentCommentsOrderByLikesSize(comment.getId(), PageRequest.of(0, 10));

        assertEquals(1, actual.get(0).getLikeCount());
        assertEquals(0, actual.get(1).getLikeCount());
    }

    @DisplayName("대댓글 개수로 정렬해 받아오기")
    @Test
    public void orderByCommentCountTest() {
        for (int i = 0; i < 2; i++) {
            CommentCommentEntity commentComment = new CommentCommentEntity();
            commentComment.setContent("content22");
            commentComment.setUser(user);
            commentComment.setComment(comment);
            commentCommentRepository.save(commentComment);

            for (int j = i; j < 2; j++) {
                // 순서대로 대댓글 2개, 1개 넣기
                CommentCommentEntity commentComment2 = CommentCommentEntity.builder()
                        .content("asdfasdf")
                        .user(user)
                        .comment(commentComment)
                        .build();
                commentCommentRepository.save(commentComment2);
            }
        }

        entityManager.flush();
        entityManager.clear();

        List<CommentCommentEntity> actual = commentCommentRepository.findCommentCommentsOrderByCommentsSize(comment.getId(), PageRequest.of(0, 10));

        assertEquals(2, actual.get(0).getCommentCount());
        assertEquals(1, actual.get(1).getCommentCount());
    }
}
