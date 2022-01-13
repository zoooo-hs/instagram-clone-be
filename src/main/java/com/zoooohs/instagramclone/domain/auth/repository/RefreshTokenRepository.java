package com.zoooohs.instagramclone.domain.auth.repository;

import com.zoooohs.instagramclone.domain.auth.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    RefreshTokenEntity findByUserNameAndToken(String userName, String refreshToken);

    RefreshTokenEntity findByUserName(String userName);
}
