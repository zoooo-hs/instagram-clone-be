package com.zoooohs.instagramclone.domain.auth.repository;

import com.zoooohs.instagramclone.domain.auth.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    RefreshTokenEntity findByUserName(String userName);

    void deleteByToken(String token);

    RefreshTokenEntity findByToken(String token);
}
