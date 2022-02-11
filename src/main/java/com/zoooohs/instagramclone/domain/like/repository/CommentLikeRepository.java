package com.zoooohs.instagramclone.domain.like.repository;

import com.zoooohs.instagramclone.domain.like.entity.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {
}
