package com.zoooohs.instagramclone.domain.user.repository;

import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByName(String name);

    UserEntity findByEmailAndName(String email, String name);
}
