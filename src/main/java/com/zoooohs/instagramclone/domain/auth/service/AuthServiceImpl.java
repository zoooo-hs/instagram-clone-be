package com.zoooohs.instagramclone.domain.auth.service;

import com.zoooohs.instagramclone.configuration.JwtTokenProvider;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.auth.entity.RefreshTokenEntity;
import com.zoooohs.instagramclone.domain.auth.repository.RefreshTokenRepository;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
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
            throw new ZooooException(ErrorCode.SIGN_UP_DUPLICATED_EMAIL);
        }
        UserEntity user = this.modelMapper.map(signUp, UserEntity.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.userRepository.save(user).getId();
        return generateNewToken(user.getUsername());
    }

    @Override
    @Transactional
    public AuthDto.Token signIn(AuthDto.SignIn signIn) {
        UserEntity user = this.userRepository.findByEmail(signIn.getEmail()).orElseThrow(() -> new ZooooException(ErrorCode.LOGIN_WRONG_INFO));
        if (!passwordEncoder.matches(signIn.getPassword(), user.getPassword())) {
            throw new ZooooException(ErrorCode.LOGIN_WRONG_INFO);
        }
        return generateNewToken(user.getUsername());
    }

    @Override
    @Transactional
    public AuthDto.Token refresh(AuthDto.Token token) {
        if (!this.jwtTokenProvider.validRefreshToken(token.getRefreshToken())) {
            this.refreshTokenRepository.deleteByToken(token.getRefreshToken());
            throw new ZooooException(ErrorCode.TOKEN_EXPIRED);
        }
        RefreshTokenEntity refreshTokenEntity = this.refreshTokenRepository.findByToken(token.getRefreshToken());
        if (refreshTokenEntity == null) {
            throw new ZooooException(ErrorCode.TOKEN_EXPIRED);
        }
        String userName = this.jwtTokenProvider.getRefreshTokenUserId(token.getRefreshToken());
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