package com.zoooohs.instagramclone.domain.follow.repository;

import com.zoooohs.instagramclone.domain.follow.entity.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    FollowEntity findByFollowUserIdAndUserId(Long followUserId, Long userId);

    List<FollowEntity> findByUserId(Long userId);

    List<FollowEntity> findByFollowUserId(Long id);
}
