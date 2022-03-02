package com.zoooohs.instagramclone.domain.auth.service;

import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;

public interface AuthService {
    String signUp(AuthDto.SignUp signUp);

    AuthDto.Token signIn(AuthDto.SignIn signIn);

    AuthDto.Token refresh(AuthDto.Token token);

    boolean checkDuplicatedEmail(String email);

    boolean checkDuplicatedName(String name);

    Boolean verification(String email, String token);
}
