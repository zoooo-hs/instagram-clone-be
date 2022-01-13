package com.zoooohs.instagramclone.domain.auth.service;

import com.zoooohs.instagramclone.configuration.JwtTokenProvider;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

@Transactional
@SpringBootTest
public class AuthServiceTest {

    @PersistenceUnit
    private EntityManagerFactory factory;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // TODO: 테스트를 쪼갤 순 없을지
    @Test
    public void AuthTest() {
        Date now = new Date();
        String email = "sign-up-test-id"+now.getTime()+"@email.com";
        String password = "passwd";
        String name = "sign-up-test-name"+now.getTime();
        AuthDto.SignUp signUpDto = new AuthDto.SignUp();
        signUpDto.setEmail(email);
        signUpDto.setPassword(password);
        signUpDto.setName(name);
        AuthDto.Token token = this.authService.signUp(signUpDto);
        String userId = jwtTokenProvider.getAccessTokenUserId(token.getAccessToken());
        assertEquals(email, userId, "sign up test failed");

        AuthDto.SignIn signInDto = new AuthDto.SignIn();
        signInDto.setEmail(email);
        signInDto.setPassword(password);
        token = this.authService.signIn(signInDto);
        userId = jwtTokenProvider.getAccessTokenUserId(token.getAccessToken());
        assertEquals(email, userId, "sign in test failed");

        signInDto.setPassword("wrong-password");
        assertThrowsExactly(IllegalArgumentException.class, () -> this.authService.signIn(signInDto));

        token = this.authService.refresh(token);
        userId = jwtTokenProvider.getAccessTokenUserId(token.getAccessToken());
        assertEquals(email, userId, "refreshToken test failed");
    }

}
