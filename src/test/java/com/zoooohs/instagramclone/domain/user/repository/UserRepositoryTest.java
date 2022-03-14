package com.zoooohs.instagramclone.domain.user.repository;

import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    PasswordEncoder passwordEncoder;
    UserEntity userEntity;

    @BeforeEach
    public void setUp() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        userEntity = UserEntity.builder()
                .name("test").email("test@test.test").password(passwordEncoder.encode("passwd")).build();
        this.userRepository.save(userEntity);
    }

    @AfterEach
    public void tearDown() {
        this.userRepository.deleteAll();
    }

    @Test
    public void saveTest() {
        userEntity = UserEntity.builder()
                .name("test2").email("test2@test.test").password(passwordEncoder.encode("passwd")).build();

        UserEntity actual = this.userRepository.save(userEntity);

        assertTrue(actual.getId() != null);
        assertEquals("test2", actual.getName());
        assertEquals("test2@test.test", actual.getEmail());
        assertTrue(passwordEncoder.matches("passwd", actual.getPassword()));
    }

    @Test
    public void findById() {
        Optional<UserEntity> actual = this.userRepository.findById(userEntity.getId());
        Optional<UserEntity> nullActual = this.userRepository.findById(9999999L);

        assertFalse(actual.isEmpty());
        assertEquals(userEntity.getId(), actual.get().getId());
        assertTrue(nullActual.isEmpty());
    }

    @Test
    public void findByNameTest() {
        Optional<UserEntity> actual = this.userRepository.findByName(userEntity.getName());
        Optional<UserEntity> nullActual = this.userRepository.findByName("no-named");

        assertFalse(actual.isEmpty());
        assertEquals(userEntity.getName(), actual.get().getName());
        assertTrue(nullActual.isEmpty());
    }

    @Test
    public void findByEmailTest() {
        Optional<UserEntity> actual = this.userRepository.findByEmail(userEntity.getEmail());
        Optional<UserEntity> nullActual = this.userRepository.findByEmail("no-named@e.e");

        assertFalse(actual.isEmpty());
        assertEquals(userEntity.getEmail(), actual.get().getEmail());
        assertTrue(nullActual.isEmpty());
    }

    @DisplayName("이름, 이메일 모두 일치하는 계정 찾기")
    @Test
    public void findByEmailAndNameTest() {
        Optional<UserEntity> maybeUser = this.userRepository.findByEmailAndName(userEntity.getEmail(), userEntity.getName());
        Optional<UserEntity> maybeNull = this.userRepository.findByEmailAndName(userEntity.getEmail(), "no-named");

        assertFalse(maybeUser.isEmpty());
        assertEquals(userEntity.getEmail(), maybeUser.get().getEmail());
        assertEquals(userEntity.getName(), maybeUser.get().getName());
        assertTrue(maybeNull.isEmpty());
    }

    @DisplayName("이름 혹은 이메일로 계정 찾기")
    @Test
    public void findByEmailOrNameTest() {
        Optional<UserEntity> maybeUser1 = this.userRepository.findByEmailOrName(userEntity.getEmail(), "");
        Optional<UserEntity> maybeUser2 = this.userRepository.findByEmailOrName("", userEntity.getName());
        Optional<UserEntity> nullActual = this.userRepository.findByEmailOrName("no-named@e.e", "no-named");

        assertFalse(maybeUser1.isEmpty());
        assertFalse(maybeUser2.isEmpty());
        assertEquals(userEntity.getEmail(), maybeUser1.get().getEmail());
        assertEquals(userEntity.getEmail(), maybeUser2.get().getEmail());
        assertEquals(userEntity.getName(), maybeUser1.get().getName());
        assertEquals(userEntity.getName(), maybeUser2.get().getName());
        assertTrue(nullActual.isEmpty());
    }

    @DisplayName("findByNameIgnoreCaseContaining name keyword, index, size에 만족하는 user entity 반환")
    @Test
    public void findByNameIgnoreCaseContainingTest() {
        Pageable pageable = PageRequest.of(0, 20);

        for (int i = 0; i < 30; i++) {
            userEntity = UserEntity.builder()
                    .name("a"+i).email("test@test.test"+i).password(passwordEncoder.encode("passwd")).build();
            this.userRepository.save(userEntity);
        }

        Optional<List<UserEntity>> maybeUsers = Optional.ofNullable(userRepository.findByNameIgnoreCaseContaining("a1", pageable));

        long count = maybeUsers.map(List::stream)
                .map(actuals -> actuals.filter(actual -> actual.getName().contains("a1")))
                .map(Stream::count)
                .orElse((long) 0);

        assertEquals(11, count);
    }
}
