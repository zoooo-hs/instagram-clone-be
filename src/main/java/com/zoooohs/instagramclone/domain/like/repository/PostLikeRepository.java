package com.zoooohs.instagramclone.domain.like.repository;

import com.zoooohs.instagramclone.domain.like.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {
    PostLikeEntity findByPostIdAndUserId(Long postId, Long id);
}
