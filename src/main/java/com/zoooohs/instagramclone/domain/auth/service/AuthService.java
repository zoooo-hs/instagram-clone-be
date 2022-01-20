package com.zoooohs.instagramclone.domain.auth.service;

import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;

public interface AuthService {
    AuthDto.Token signUp(AuthDto.SignUp signUp);

    AuthDto.Token signIn(AuthDto.SignIn signIn);

    AuthDto.Token refresh(AuthDto.Token token);

    boolean checkDuplicatedEmail(String email);

    boolean checkDuplicatedName(String name);
}
