package com.zoooohs.instagramclone.domain.auth.repository;

import com.zoooohs.instagramclone.configuration.JwtTokenProvider;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.auth.entity.RefreshTokenEntity;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RefreshTokenRepositoryTest {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    UserRepository userRepository;
    @Spy
    ModelMapper modelMapper;
    @MockBean
    UserDetailsService userDetailsService;
    JwtTokenProvider jwtTokenProvider;
    PasswordEncoder passwordEncoder;

    UserEntity userEntity;
    RefreshTokenEntity refreshTokenEntity;
    String refreshToken;

    @BeforeEach
    public void setUp() {
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, modelMapper);
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        userEntity = UserEntity.builder().name("name").email("e@e.e").password(passwordEncoder.encode("psswd")).build();
        this.userRepository.save(userEntity);
        refreshToken = jwtTokenProvider.createRefreshToken(userEntity.getUsername());

        refreshTokenEntity = RefreshTokenEntity.builder().userName(userEntity.getUsername()).token(refreshToken).build();
        this.refreshTokenRepository.save(refreshTokenEntity);
    }

    @AfterEach
    public void tearDown() {
        this.refreshTokenRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    public void findByTokenTest() {
        RefreshTokenEntity actual = this.refreshTokenRepository.findByToken(refreshToken);
        RefreshTokenEntity nullActual = this.refreshTokenRepository.findByToken("null-token");

        assertEquals(refreshToken, actual.getToken());
        assertEquals(userEntity.getUsername(), actual.getUserName());
        assertEquals(null, nullActual);
    }

    @Test
    public void findByUserNameTest() {
        RefreshTokenEntity actual = this.refreshTokenRepository.findByUserName(userEntity.getUsername());
        RefreshTokenEntity nullActual = this.refreshTokenRepository.findByUserName("null-user-name");

        assertEquals(refreshToken, actual.getToken());
        assertEquals(userEntity.getUsername(), actual.getUserName());
        assertEquals(null, nullActual);
    }

    @Test
    public void deleteByTokenTest() {
        this.refreshTokenRepository.deleteByToken(refreshToken);

        RefreshTokenEntity actual = this.refreshTokenRepository.findByToken(refreshToken);

        assertEquals(null, actual);
    }

}
