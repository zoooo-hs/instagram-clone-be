package com.zoooohs.instagramclone.domain.auth.controller;

import com.zoooohs.instagramclone.domain.auth.dto.AuthDto;
import com.zoooohs.instagramclone.domain.auth.service.AuthService;
import com.zoooohs.instagramclone.domain.service.MailService;
import com.zoooohs.instagramclone.exception.ZooooExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// TODO: Auth Server 를 따로 두어서 MSA 구성?
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;
    private final MailService mailService;

    @Operation(summary = "회원 가입", description = "email, name, password 를 입력 받아 새로운 회원 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "회원 가입 완료 및 토큰 발급",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthDto.Token.class)) }
            ),
            @ApiResponse(
                    responseCode = "409", description = "email 혹은 name 중복",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PostMapping("/auth/sign-up")
    @Transactional
    public String signUp(@RequestBody @Valid AuthDto.SignUp signUp, @RequestHeader("origin") String origin) {
        String verificationCode = this.authService.signUp(signUp);

        final String subject = "[ZOOOO-HS INSTAGRAM CLONE] Sign-Up Email Verification";
        final String email = URLEncoder.encode(signUp.getEmail(), StandardCharsets.UTF_8);
        final String token = URLEncoder.encode(verificationCode, StandardCharsets.UTF_8);
        final String text = String.format("verification link : %s/auth/verification?email=%s&token=%s", origin, email, token);
        mailService.send(signUp.getEmail(), subject, text);
        return "OK";
    }

    @Operation(summary = "로그인", description = "email, password 입력 받아 로그인")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "로그인 완료 및 토큰 발급",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthDto.Token.class)) }
            ),
            @ApiResponse(
                    responseCode = "401", description = "미인증 회원",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "로그인 정보 오류",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PostMapping("/auth/sign-in")
    public AuthDto.Token signIn(@RequestBody @Valid AuthDto.SignIn signIn) {
        return this.authService.signIn(signIn);
    }

    @Operation(summary = "토큰 재발급", description = "유효한 refresh token 으로 새로운 jwt 발급")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "토큰 재발급 완료",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthDto.Token.class)) }
            ),
            @ApiResponse(
                    responseCode = "401", description = "토큰 만료",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @PostMapping("/auth/refresh")
    public AuthDto.Token refresh(@RequestBody @Valid AuthDto.Token token) {
        return this.authService.refresh(token);
    }

    @Operation(summary = "name 중복 찾기", description = "회원 가입 시 name 중복 확인 용 api")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "중복 유무 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)) }
            ),
    })
    @GetMapping("/auth/name")
    public Boolean checkName(@RequestParam("keyword") String name) {
        return this.authService.checkDuplicatedName(name);
    }

    @Operation(summary = "email 중복 찾기", description = "회원 가입 시 email 중복 확인 용 api")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "중복 유무 반환",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)) }
            ),
    })
    @GetMapping("/auth/email")
    public Boolean checkEmail(@RequestParam("keyword") String email) {
        return this.authService.checkDuplicatedEmail(email);
    }

    @Operation(summary = "email 인증 요청", description = "메일로 전달된 인증 링크 핸들 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "인증 완료",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)) }
            ),
            @ApiResponse(
                    responseCode = "404", description = "요청한 인증 정보 관련된 회원 찾을 수 없음",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ZooooExceptionResponse.class)) }
            ),
    })
    @GetMapping("/auth/verification")
    public Boolean verification(@RequestParam("email") String email, @RequestParam("token") String token) {
        return this.authService.verification(email, token);
    }
}
