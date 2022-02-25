package com.zoooohs.instagramclone.domain.comment.repository;

import com.zoooohs.instagramclone.domain.comment.entity.PostCommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Long> {
    @EntityGraph("comment-user")
    List<PostCommentEntity> findByPostId(Long postId, Pageable pageable);

    PostCommentEntity findByIdAndUserId(Long commentId, Long userId);
}
