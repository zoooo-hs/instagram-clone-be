package com.zoooohs.instagramclone.domain.auth.service;

import com.zoooohs.instagramclone.configuration.JwtTokenProvider;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.auth.entity.RefreshTokenEntity;
import com.zoooohs.instagramclone.domain.auth.repository.RefreshTokenRepository;
import com.zoooohs.instagramclone.domain.common.type.AccountStatusType;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
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
import static org.mockito.Mockito.*;

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

        given(this.userRepository.findByEmailOrName(eq(email), eq(name))).willReturn(Optional.ofNullable(null));
        given(this.userRepository.save(any(UserEntity.class))).willReturn(user);

        String actual = this.authService.signUp(signUpDto);

        assertTrue(passwordEncoder.matches(user.getEmail()+user.getName(), actual));
    }

    @Test
    public void signUpFailureTest() {
        AuthDto.SignUp duplicated = this.modelMapper.map(testUser, AuthDto.SignUp.class);

        given(this.userRepository.findByEmailOrName(eq(duplicated.getEmail()), eq(duplicated.getName()))).willReturn(Optional.of(testUser));

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

        testUser.setStatus(AccountStatusType.VERIFIED);
        given(this.userRepository.findByEmail(eq(testUser.getEmail()))).willReturn(Optional.of(testUser));

        AuthDto.Token actual = this.authService.signIn(signInDto);

        assertEquals(testUser.getEmail(), jwtTokenProvider.getAccessTokenUserId(actual.getAccessToken()));
    }

    @DisplayName("인증 받지 않은 계정 DTO -> USER_NOT_VERIFIED throw")
    @Test
    public void singInFailure401Test() {
        AuthDto.SignIn signInDto = AuthDto.SignIn.builder().email(testUser.getEmail()).password("passwd").build();

        testUser.setStatus(AccountStatusType.WAITING);
        given(this.userRepository.findByEmail(eq(testUser.getEmail()))).willReturn(Optional.of(testUser));

        try {
            authService.signIn(signInDto);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.USER_NOT_VERIFIED, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }

    @DisplayName("올바르지 못한 계정 정보 로그인 -> LOGIN_WRONG_INFO throw")
    @Test
    public void singInFailure404Test() {
        AuthDto.SignIn signInDto = AuthDto.SignIn.builder().email(testUser.getEmail()).password("wrong-passwd").build();

        given(this.userRepository.findByEmail(eq(testUser.getEmail()))).willReturn(Optional.of(testUser));

        try {
            authService.signIn(signInDto);
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
                .accessToken(this.jwtTokenProvider.createAccessToken(testUser.getUsername()))
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

        MockedStatic<Instant> instantMockedStatic = mockStatic(Instant.class);
        instantMockedStatic.when(() -> Instant.now()).thenReturn(now);

        try {
            AuthDto.Token actual = this.authService.refresh(token);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.TOKEN_EXPIRED, e.getErrorCode());
        } catch (Exception e) {
            fail();
        } finally {
            instantMockedStatic.close();
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

    @DisplayName("email, token 받아서 유효한 토큰이면 true 아니면 404 throw")
    @Test
    public void verificationTest() {
        final String email = "test@test.com";
        final String token = passwordEncoder.encode(email+"test");

        given(userRepository.findByEmail(eq(email))).willAnswer(new Answer<Optional<UserEntity>>() {
            private int count = 0;
            @Override
            public Optional<UserEntity> answer(InvocationOnMock invocation) throws Throwable {
                UserEntity user = UserEntity.builder().email(email).name("test").build();
                if (count == 0) {
                    user.setStatus(AccountStatusType.WAITING);
                } else {
                    user.setStatus(AccountStatusType.VERIFIED);
                }
                count++;
                return Optional.of(user);
            }
        });

        // 통과
        boolean actual = authService.verification(email, token);

        assertTrue(actual);
        verify(userRepository, times(1)).save(any(UserEntity.class));

        // 404
        try {
            authService.verification("1"+email, token);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }

        // 409
        try {
            authService.verification(email, token);
            fail();
        } catch (ZooooException e) {
            assertEquals(ErrorCode.ALREADY_VERIFIED, e.getErrorCode());
        } catch (Exception e) {
            fail();
        }

    }

}
