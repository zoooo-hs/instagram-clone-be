package com.zoooohs.instagramclone.domain.auth.service;

import com.zoooohs.instagramclone.configuration.JwtTokenProvider;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

// TODO: unit test 로 바꾸기
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class AuthServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    String testEmail;
    String testPassword;
    String testName;
    Long testId;

    @BeforeAll
    public void init() {
        Date now = new Date();
        testEmail = "tt-sign-up-test-id"+now.getTime()+"@email.com";
        testPassword = "passwd";
        testName = "sign-up-test-name"+now.getTime();
        UserEntity user = new UserEntity();
        user.setEmail(testEmail);
        user.setPassword(passwordEncoder.encode(testPassword));
        user.setName(testName);
        testId = this.userRepository.save(user).getId();
    }

    @AfterAll
    public void clean() {
        this.userRepository.deleteById(testId);
    }

    @Test
    public void duplicatedSignUpTest() {
        AuthDto.SignUp signUpDto = new AuthDto.SignUp();
        signUpDto.setEmail(testEmail);
        signUpDto.setPassword(testPassword);
        signUpDto.setName(testName);
        AuthDto.Token token = this.authService.signUp(signUpDto);
        assertEquals(null, token, "sign up test failed");
    }

    @Test
    public void singUpTest() {
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
    }

    @Test
    public void signInTest() {
        // success sign in
        AuthDto.SignIn signInDto = new AuthDto.SignIn();
        signInDto.setEmail(testEmail);
        signInDto.setPassword(testPassword);
        AuthDto.Token token = this.authService.signIn(signInDto);
        String userId = jwtTokenProvider.getAccessTokenUserId(token.getAccessToken());
        assertEquals(testEmail, userId, "sign in test failed");

        // wrong password
        signInDto.setPassword("wrong-password");
        assertThrowsExactly(IllegalArgumentException.class, () -> this.authService.signIn(signInDto));

        // refresh token
        token = this.authService.refresh(token);
        userId = jwtTokenProvider.getAccessTokenUserId(token.getAccessToken());
        assertEquals(testEmail, userId, "refreshToken test failed");
    }

    @Test
    public void checkDuplicatedIdAndNameTest() {
        // id duplicated test
        assertTrue(authService.checkDuplicatedEmail(testEmail));
        // name duplicated test
        assertTrue(authService.checkDuplicatedName(testName));
    }

}
