package com.zoooohs.instagramclone.domain.comment.repository;

import com.zoooohs.instagramclone.domain.comment.entity.CommentCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCommentRepository extends JpaRepository<CommentCommentEntity, Long> {}

