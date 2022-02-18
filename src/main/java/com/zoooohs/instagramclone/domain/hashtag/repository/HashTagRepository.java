package com.zoooohs.instagramclone.domain.hashtag.repository;

import com.zoooohs.instagramclone.domain.hashtag.entity.HashTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashTagRepository extends JpaRepository<HashTagEntity, Long> {
    List<HashTagEntity> findByPostId(Long postId);
}
