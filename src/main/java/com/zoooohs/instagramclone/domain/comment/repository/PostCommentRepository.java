package com.zoooohs.instagramclone.domain.comment.repository;

import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Long> {
    @EntityGraph("comment-user")
    List<PostCommentEntity> findByPostId(Long postId, Pageable pageable);

    PostCommentEntity findByIdAndUserId(Long commentId, Long userId);

    @EntityGraph("comment-user")
    @Query("SELECT c FROM post_comment c WHERE c.post.id = :id ORDER BY c.likes.size DESC")
    List<PostCommentEntity> findPostCommentsOrderByLikesSize(@Param("id") Long id, Pageable pageable);

    @EntityGraph("comment-user")
    @Query("SELECT c FROM post_comment c WHERE c.post.id = :id ORDER BY c.comments.size DESC")
    List<PostCommentEntity> findPostCommentsOrderByCommentsSize(@Param("id") Long id, Pageable pageable);
}
