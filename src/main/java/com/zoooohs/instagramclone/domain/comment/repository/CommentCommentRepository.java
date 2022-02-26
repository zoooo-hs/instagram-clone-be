package com.zoooohs.instagramclone.domain.comment.repository;

import com.zoooohs.instagramclone.domain.comment.entity.CommentCommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentCommentRepository extends JpaRepository<CommentCommentEntity, Long> {
    List<CommentCommentEntity> findByCommentId(Long commentId, Pageable pageable);
}

