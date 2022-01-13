package com.zoooohs.instagramclone.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AuthDto {

    @Data
    public static class SignUp {
        @NotNull
        @Email
        private String email;

        @Size(max = 30)
        @NotNull
        private String name;
        @NotNull
        private String password;
    }

    @Data
    public static class SignIn {
        @NotNull
        @Email
        private String email;
        @NotNull
        private String password;
    }

    @Builder
    @Data
    public static class Token {
        // TODO: pattern 관리 util class 만들기
        @Pattern(regexp = "[\\w\\d]*\\.[\\w\\d]*\\.[\\w\\d]*")
        @NotNull
        private String accessToken;
        @Pattern(regexp = "[\\w\\d]*\\.[\\w\\d]*\\.[\\w\\d]*")
        @NotNull
        private String refreshToken;
    }
}
