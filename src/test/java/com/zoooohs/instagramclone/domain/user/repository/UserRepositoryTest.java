package com.zoooohs.instagramclone.domain.user.repository;

import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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

    @Test
    public void findByEmailAndNameTest() {
        Optional<UserEntity> actual = this.userRepository.findByEmailAndName(userEntity.getEmail(), userEntity.getName());
        Optional<UserEntity> nullActual = this.userRepository.findByEmailAndName("no-named@e.e", "no-named");

        assertFalse(actual.isEmpty());
        assertEquals(userEntity.getEmail(), actual.get().getEmail());
        assertEquals(userEntity.getName(), actual.get().getName());
        assertTrue(nullActual.isEmpty());
    }
}
