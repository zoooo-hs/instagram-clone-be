package com.zoooohs.instagramclone.domain.auth.controller;

import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.common.model.SearchModel;
import com.zoooohs.instagramclone.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

// TODO: Auth Server 를 따로 두어서 MSA 구성?
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/sign-up")
    public AuthDto.Token signUp(@RequestBody @Valid AuthDto.SignUp signUp) {
        return this.authService.signUp(signUp);
    }

    @PostMapping("/auth/sign-in")
    public AuthDto.Token signIn(@RequestBody @Valid AuthDto.SignIn signIn) {
        return this.authService.signIn(signIn);
    }

    @PostMapping("/auth/refresh")
    public AuthDto.Token refresh(@RequestBody @Valid AuthDto.Token token) {
        return this.authService.refresh(token);
    }

    @GetMapping("/auth/name")
    public Boolean checkName(@ModelAttribute SearchModel searchModel) {
        return this.authService.checkDuplicatedName(searchModel.getKeyword());
    }

    @GetMapping("/auth/email")
    public Boolean checkEmail(@ModelAttribute SearchModel searchModel) {
        return this.authService.checkDuplicatedEmail(searchModel.getKeyword());
    }
}
