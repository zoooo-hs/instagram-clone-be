package com.zoooohs.instagramclone.domain.auth.service;

import com.zoooohs.instagramclone.configuration.JwtTokenProvider;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.auth.entity.RefreshTokenEntity;
import com.zoooohs.instagramclone.domain.auth.repository.RefreshTokenRepository;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    AuthService authService;

    @Spy
    ModelMapper modelMapper;

    @Mock
    UserRepository userRepository;
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @Mock
    UserDetailsService userDetailsService;

    PasswordEncoder passwordEncoder;
    JwtTokenProvider jwtTokenProvider;

    UserEntity testUser;

    final String testUserPasswdDecode = "passwd";

    @BeforeEach
    public void setUp() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, modelMapper);
        authService = new AuthServiceImpl(userRepository, refreshTokenRepository, modelMapper, passwordEncoder, jwtTokenProvider);

        String email = "test-user@email.com";
        String password = passwordEncoder.encode(testUserPasswdDecode);
        String name = "test-user";
        testUser = UserEntity.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
    }

    @Test
    public void signUpTest() {
        String email = "sign-up-test-id@email.com";
        String password = "passwd";
        String name = "sign-up-test-name";

        AuthDto.SignUp signUpDto = AuthDto.SignUp.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();

        UserEntity user = UserEntity.builder()
                .email(email).name(name).build();
        user.setId(1L);

        given(this.userRepository.findByEmailAndName(eq(email), eq(name))).willReturn(null);
        given(this.userRepository.save(any(UserEntity.class))).willReturn(user);

        AuthDto.Token actual = this.authService.signUp(signUpDto);

        assertEquals(email, this.jwtTokenProvider.getAccessTokenUserId(actual.getAccessToken()));
        assertEquals(email, this.jwtTokenProvider.getRefreshTokenUserId(actual.getRefreshToken()));
    }

    @Test
    public void signUpFailureTest() {
        AuthDto.SignUp duplicated = this.modelMapper.map(testUser, AuthDto.SignUp.class);

        given(this.userRepository.findByEmailAndName(eq(duplicated.getEmail()), eq(duplicated.getName()))).willReturn(Optional.of(testUser));

        try {
            this.authService.signUp(duplicated);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.SIGN_UP_DUPLICATED_EMAIL_OR_NAME, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void singInTest() {
        AuthDto.SignIn signInDto = AuthDto.SignIn.builder().email(testUser.getEmail()).password(testUserPasswdDecode).build();

        given(this.userRepository.findByEmail(eq(testUser.getEmail()))).willReturn(Optional.of(testUser));

        AuthDto.Token actual = this.authService.signIn(signInDto);

        assertEquals(testUser.getEmail(), jwtTokenProvider.getAccessTokenUserId(actual.getAccessToken()));
    }

    @Test
    public void singInFailureTest() {
        AuthDto.SignIn signInDto = AuthDto.SignIn.builder().email(testUser.getEmail()).password("wrong-passwd").build();

        given(this.userRepository.findByEmail(eq(testUser.getEmail()))).willReturn(Optional.of(testUser));

        try {
            this.authService.signIn(signInDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.LOGIN_WRONG_INFO, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void refreshTest() {
        AuthDto.Token token = AuthDto.Token.builder()
                .refreshToken(this.jwtTokenProvider.createRefreshToken(testUser.getUsername())).build();

        given(this.refreshTokenRepository.findByToken(eq(token.getRefreshToken()))).willReturn(RefreshTokenEntity.builder().build());

        AuthDto.Token actual = this.authService.refresh(token);

        assertEquals(this.jwtTokenProvider.getAccessTokenUserId(token.getAccessToken()),
                this.jwtTokenProvider.getAccessTokenUserId(actual.getAccessToken()));
    }

    @Test
    public void refreshTokenDateExpiredFailureTest() {
        AuthDto.Token token = AuthDto.Token.builder()
                .refreshToken(this.jwtTokenProvider.createRefreshToken(testUser.getUsername())).build();

        Instant now = Instant.ofEpochMilli(Instant.now().toEpochMilli() + 2*24*60*60*1000);

        mockStatic(Instant.class).when(() -> Instant.now()).thenReturn(now);

        try {
            AuthDto.Token actual = this.authService.refresh(token);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.TOKEN_EXPIRED, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void refreshTokenNotFoundFailureTest() {
        AuthDto.Token token = AuthDto.Token.builder()
                .refreshToken(this.jwtTokenProvider.createRefreshToken(testUser.getUsername())).build();

        given(this.refreshTokenRepository.findByToken(eq(token.getRefreshToken()))).willReturn(null);

        try {
            AuthDto.Token actual = this.authService.refresh(token);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.TOKEN_EXPIRED, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void checkDuplicatedEmail() {
        given(this.userRepository.findByEmail(eq(testUser.getEmail()))).willReturn(Optional.of(testUser));
        given(this.userRepository.findByEmail(eq("not-duplicatee@d.d"))).willReturn(Optional.ofNullable(null));

        boolean duplicatedActual = this.authService.checkDuplicatedEmail(testUser.getEmail());
        boolean notDuplicatedActual = this.authService.checkDuplicatedEmail("not-duplicatee@d.d");
        assertTrue(duplicatedActual);
        assertFalse(notDuplicatedActual);
    }

    @Test
    public void checkDuplicatedName() {
        given(this.userRepository.findByName(eq(testUser.getName()))).willReturn(Optional.of(testUser));
        given(this.userRepository.findByName(eq("not-duplicated"))).willReturn(Optional.ofNullable(null));

        boolean duplicatedActual = this.authService.checkDuplicatedName(testUser.getName());
        boolean notDuplicatedActual = this.authService.checkDuplicatedName("not-duplicated");
        assertTrue(duplicatedActual);
        assertFalse(notDuplicatedActual);
    }
}
