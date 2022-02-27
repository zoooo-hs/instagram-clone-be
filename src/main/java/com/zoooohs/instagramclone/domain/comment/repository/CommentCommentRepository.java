package com.zoooohs.instagramclone.domain.comment.repository;

import com.zoooohs.instagramclone.domain.comment.entity.CommentCommentEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentCommentRepository extends JpaRepository<CommentCommentEntity, Long> {
    List<CommentCommentEntity> findByCommentId(Long commentId, Pageable pageable);

    @EntityGraph("comment-user")
    @Query("SELECT c FROM comment_comment c WHERE c.comment.id = :id ORDER BY c.likes.size DESC")
    List<CommentCommentEntity> findCommentCommentsOrderByLikesSize(@Param("id") Long id, Pageable pageable);

    @EntityGraph("comment-user")
    @Query("SELECT c FROM comment_comment c WHERE c.comment.id = :id ORDER BY c.comments.size DESC")
    List<CommentCommentEntity> findCommentCommentsOrderByCommentsSize(@Param("id") Long id, Pageable pageable);
}

