package com.zoooohs.instagramclone.domain.follow.repository;

import com.zoooohs.instagramclone.domain.follow.entity.FollowEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FollowRepositoryTest {
    @Autowired
    FollowRepository followRepository;

    @Autowired
    UserRepository userRepository;

    PasswordEncoder passwordEncoder;
    UserEntity userEntity;
    UserEntity followUserEntity;

    @BeforeEach
    public void setUp() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        userEntity = UserEntity.builder()
                .name("test").email("test@test.test").password(passwordEncoder.encode("passwd")).build();
        followUserEntity = UserEntity.builder()
                .name("test2").email("test2@test.test").password(passwordEncoder.encode("passwd")).build();
        this.userRepository.save(userEntity);
        this.userRepository.save(followUserEntity);
    }

    @DisplayName("follow entity save 테스트")
    @Test
    public void saveTest() {
        FollowEntity followEntity = FollowEntity.builder().followUser(followUserEntity).user(userEntity).build();

        FollowEntity actual = followRepository.save(followEntity);

        assertNotNull(actual.getId());
        assertEquals(followEntity.getFollowUser().getId(), actual.getFollowUser().getId());
        assertEquals(followEntity.getUser().getId(), actual.getUser().getId());
    }

    @DisplayName("followUserId, userId 로 followEntity select 테스트")
    @Test
    public void findByFollowUserIdAndUserIdTest() {
        FollowEntity followEntity = FollowEntity.builder().followUser(followUserEntity).user(userEntity).build();
        followEntity = followRepository.save(followEntity);

        FollowEntity actual = followRepository.findByFollowUserIdAndUserId(followUserEntity.getId(), userEntity.getId());

        assertNotNull(actual);
        assertEquals(followEntity.getId(), actual.getId());
    }

    @DisplayName("follow entity delete test")
    @Test
    public void deleteTest() {
        FollowEntity followEntity = FollowEntity.builder().followUser(followUserEntity).user(userEntity).build();
        followEntity = followRepository.save(followEntity);
        followRepository.delete(followEntity);

        FollowEntity actual = followRepository.findByFollowUserIdAndUserId(followUserEntity.getId(), userEntity.getId());
        Optional<FollowEntity> actual2 = followRepository.findById(followEntity.getId());

        assertNull(actual);
        assertTrue(actual2.isEmpty());
    }

    @DisplayName("userId 로 follower entity list select 하는 follow repository")
    @Test
    public void findByUserIdTest() {
        FollowEntity followEntity = FollowEntity.builder().followUser(followUserEntity).user(userEntity).build();
        followEntity = followRepository.save(followEntity);

        List<FollowEntity> actual = followRepository.findByUserId(userEntity.getId());

        assertEquals(1, actual.size());
        assertEquals(followEntity.getId(), actual.get(0).getId());
    }

}