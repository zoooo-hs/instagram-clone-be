package com.zoooohs.instagramclone.domain.auth.service;

import com.zoooohs.instagramclone.configuration.JwtTokenProvider;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.auth.entity.RefreshTokenEntity;
import com.zoooohs.instagramclone.domain.auth.repository.RefreshTokenRepository;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthDto.Token signUp(AuthDto.SignUp signUp) {
        UserEntity duplicated = this.userRepository.findByEmailAndName(signUp.getEmail(), signUp.getName());
        if (duplicated != null) {
            // TODO: exception handling
            return null;
        }
        UserEntity user = this.modelMapper.map(signUp, UserEntity.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.userRepository.save(user).getId();
        return generateNewToken(user.getUsername());
    }

    @Override
    @Transactional
    public AuthDto.Token signIn(AuthDto.SignIn signIn) {
        // TODO: exception handling
        UserEntity user = this.userRepository.findByEmail(signIn.getEmail()).orElseThrow(() -> new IllegalArgumentException("not valid user"));
        if (!passwordEncoder.matches(signIn.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("wrong info");
        }
        return generateNewToken(user.getUsername());
    }

    @Override
    @Transactional
    public AuthDto.Token refresh(AuthDto.Token token) {
        if (!this.jwtTokenProvider.validRefreshToken(token.getRefreshToken())) {
            // TODO: Auth Exception 다시 로그인
            throw new IllegalArgumentException("expired refresh token");
        }
        String userName = this.jwtTokenProvider.getAccessTokenUserId(token.getAccessToken());
        if (userName == null || !userName.equals(this.jwtTokenProvider.getRefreshTokenUserId(token.getRefreshToken()))) {
            throw new IllegalArgumentException("expired refresh token");
        }
        RefreshTokenEntity refreshTokenEntity = this.refreshTokenRepository.findByUserNameAndToken(userName, token.getRefreshToken());
        if (refreshTokenEntity == null) {
            // TODO: 임의로 조작되거나 잘못된 토큰
            throw new IllegalArgumentException("expired refresh token");
        }

        return generateNewToken(userName);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkDuplicatedEmail(String email) {
        UserEntity user = this.userRepository.findByEmail(email).orElse(null);
        return user != null;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkDuplicatedName(String name) {
        UserEntity user = this.userRepository.findByName(name).orElse(null);
        return user != null;
    }

    @Transactional
    private AuthDto.Token generateNewToken(String userName) {
        AuthDto.Token token =  AuthDto.Token.builder()
                .accessToken(this.jwtTokenProvider.createAccessToken(userName))
                .refreshToken(this.jwtTokenProvider.createRefreshToken(userName))
                .build();
        // TODO: store refresh token in memory db
        RefreshTokenEntity refreshTokenEntity = this.refreshTokenRepository.findByUserName(userName);
        if (refreshTokenEntity == null) {
            refreshTokenEntity = new RefreshTokenEntity();
            refreshTokenEntity.setUserName(userName);
        }
        refreshTokenEntity.setToken(token.getRefreshToken());
        this.refreshTokenRepository.save(refreshTokenEntity);
        return token;
    }

}
