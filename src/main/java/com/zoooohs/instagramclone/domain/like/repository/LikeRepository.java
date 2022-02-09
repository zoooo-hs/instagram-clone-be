package com.zoooohs.instagramclone.domain.like.repository;

import com.zoooohs.instagramclone.domain.like.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    LikeEntity findByPostIdAndUserId(Long postId, Long id);
}
