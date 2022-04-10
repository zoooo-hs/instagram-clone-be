package com.zoooohs.instagramclone.domain.auth.service;

import com.zoooohs.instagramclone.configuration.JwtTokenProvider;
import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.common.type.AccountStatusType;
import com.zoooohs.instagramclone.domain.user.entity.UserEntity;
import com.zoooohs.instagramclone.domain.user.repository.UserRepository;
import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public String signUp(AuthDto.SignUp signUp) {
        Optional<UserEntity> duplicated = this.userRepository.findByEmailOrName(signUp.getEmail(), signUp.getName());
        if (duplicated.isPresent()) {
            throw new ZooooException(ErrorCode.SIGN_UP_DUPLICATED_EMAIL_OR_NAME);
        }
        UserEntity user = this.modelMapper.map(signUp, UserEntity.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.userRepository.save(user);
        // TODO: verification code 만드는 방법 다시 조사하기
        return passwordEncoder.encode(user.getEmail()+user.getName());
    }

    @Override
    @Transactional
    public AuthDto.Token signIn(AuthDto.SignIn signIn) {
        UserEntity user = this.userRepository.findByEmail(signIn.getEmail())
                .filter(userEntity -> passwordEncoder.matches(signIn.getPassword(), userEntity.getPassword()))
                .orElseThrow(() -> new ZooooException(ErrorCode.LOGIN_WRONG_INFO));
        if (user.getStatus().equals(AccountStatusType.WAITING)) {
            throw new ZooooException(ErrorCode.USER_NOT_VERIFIED);
        }
        return generateNewToken(user.getId(), user.getUsername());
    }

    @Override
    @Transactional
    public AuthDto.Token refresh(AuthDto.Token token) {
        if (!this.jwtTokenProvider.validRefreshToken(token.getRefreshToken())) {
            throw new ZooooException(ErrorCode.TOKEN_EXPIRED);
        }
        String username = jwtTokenProvider.getRefreshTokenUserId(token.getRefreshToken());
        Long userId = jwtTokenProvider.getRefreshTokenValue(token.getRefreshToken(), "id", Long.class);
        return generateNewToken(userId, username);
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

    @Override
    public Boolean verification(String email, String token) {
        UserEntity user = userRepository.findByEmail(email)
                .filter(userEntity -> passwordEncoder.matches(userEntity.getEmail()+userEntity.getName(), token))
                .orElseThrow(() -> new ZooooException(ErrorCode.USER_NOT_FOUND));
        if (user.getStatus().equals(AccountStatusType.VERIFIED)) {
            throw new ZooooException(ErrorCode.ALREADY_VERIFIED);
        }
        user.setStatus(AccountStatusType.VERIFIED);
        userRepository.save(user);
        return true;
    }

    private AuthDto.Token generateNewToken(Long userId, String username) {
        return jwtTokenProvider.createToken(userId, username);
    }
}
