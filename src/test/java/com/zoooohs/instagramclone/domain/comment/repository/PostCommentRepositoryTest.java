package com.zoooohs.instagramclone.domain.comment.repository;

import com.zoooohs.instagramclone.domain.comment.entity.CommentCommentEntity;
import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import com.zoooohs.instagramclone.domain.like.repository.CommentLikeRepository;
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
public class PostCommentRepositoryTest {

    @Autowired
    PostCommentRepository postCommentRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;

    @Autowired
    LikeRepository likeRepository;

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
        PostCommentEntity comment = new PostCommentEntity();
        comment.setContent("content");
        comment.setUser(user);
        comment.setPost(post);

        PostCommentEntity actual = this.postCommentRepository.save(comment);

        assertEquals(comment.getContent(), actual.getContent());
        assertTrue(actual.getId() != null);
    }

    @DisplayName("comment repository findByPostId, postId로 comment list 받아오기 테스트")
    @Test
    public void findByPostIdTest() {
        List<PostCommentEntity> testList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PostCommentEntity comment = new PostCommentEntity();
            comment.setContent("content");
            comment.setUser(user);
            comment.setPost(post);
            testList.add(comment);
        }
        this.postCommentRepository.saveAll(testList);

        int pageSize = 20;
        Pageable pageable = PageRequest.of(0, pageSize);

        List<PostCommentEntity> actual = this.postCommentRepository.findByPostId(post.getId(), pageable);

        assertNotEquals(null, actual);
        assertEquals(pageSize, actual.size());
    }

    @DisplayName("findByPostId comment entity list SELECT 할때 likes같이 반환")
    @Test
    public void findByPostIdWithLikesTest() {
        List<PostCommentEntity> testList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PostCommentEntity comment = new PostCommentEntity();
            comment.setContent("content");
            comment.setUser(user);
            comment.setPost(post);
            testList.add(comment);
        }

        this.postCommentRepository.saveAll(testList);

        CommentLikeEntity commentLike = new CommentLikeEntity();
        commentLike.setComment(testList.get(0));
        commentLike.setUser(user);

        commentLikeRepository.save(commentLike);

        entityManager.flush();
        entityManager.clear();


        int pageSize = 20;
        Pageable pageable = PageRequest.of(0, pageSize);

        List<PostCommentEntity> actual = this.postCommentRepository.findByPostId(post.getId(), pageable);

        assertNotEquals(null, actual);
        assertEquals(pageSize, actual.size());
        assertEquals(1, actual.get(0).getLikeCount());
        assertEquals(0, actual.get(0).getCommentCount());
        assertTrue(actual.get(0).getLikes().stream().filter(l -> l.getUser().getId().equals(user.getId())).findFirst().isPresent());
    }

    @DisplayName("commentRepository.findByIdAndUserId commentId, userId 받아와서 해당 유저의 댓글 가져오는 기능 테스트")
    @Test
    public void findByIdAndUserIdTest() {
        PostCommentEntity comment = new PostCommentEntity();
        comment.setContent("content22");
        comment.setUser(user);
        comment.setPost(post);
        comment = this.postCommentRepository.save(comment);

        PostCommentEntity actual = this.postCommentRepository.findByIdAndUserId(comment.getId(), user.getId());
        PostCommentEntity actualNull = this.postCommentRepository.findByIdAndUserId(comment.getId(), user.getId()+3L);

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

        this.postCommentRepository.delete(comment);

        assertThrows(Exception.class, () -> postCommentRepository.findById(commentId).orElseThrow(() -> new Exception()));
    }


    @DisplayName("like 개수로 정렬해 받아오기")
    @Test
    public void orderByLikeCountTest() {
        for (int i = 0; i < 2; i++) {
            PostCommentEntity comment = new PostCommentEntity();
            comment.setContent("content22");
            comment.setUser(user);
            comment.setPost(post);
            comment = this.postCommentRepository.save(comment);
            for (int j = i; j < 1; j++) {
                // 순서대로 좋아요 1개, 0개 넣기
                CommentLikeEntity like = CommentLikeEntity.builder().user(user).comment(comment).build();
                likeRepository.save(like);
            }
        }

        entityManager.flush();
        entityManager.clear();

        List<PostCommentEntity> actual = postCommentRepository.findPostCommentsOrderByLikesSize(post.getId(), PageRequest.of(0, 10));

        assertEquals(1, actual.get(0).getLikeCount());
        assertEquals(0, actual.get(1).getLikeCount());
    }

    @DisplayName("대댓글 개수로 정렬해 받아오기")
    @Test
    public void orderByCommentCountTest() {
        for (int i = 0; i < 2; i++) {
            PostCommentEntity comment = new PostCommentEntity();
            comment.setContent("content22");
            comment.setUser(user);
            comment.setPost(post);
            comment = this.postCommentRepository.save(comment);

            for (int j = i; j < 2; j++) {
                // 순서대로 대댓글 2개, 1개 넣기
                CommentCommentEntity commentComment = CommentCommentEntity.builder()
                        .content("asdfasdf")
                        .user(user)
                        .comment(comment)
                        .build();
                commentRepository.save(commentComment);
            }
        }

        entityManager.flush();
        entityManager.clear();

        List<PostCommentEntity> actual = postCommentRepository.findPostCommentsOrderByCommentsSize(post.getId(), PageRequest.of(0, 10));

        assertEquals(2, actual.get(0).getCommentCount());
        assertEquals(1, actual.get(1).getCommentCount());
    }
}
