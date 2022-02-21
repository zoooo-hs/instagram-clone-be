package com.zoooohs.instagramclone.domain.auth.dto;

import com.zoooohs.instagramclone.util.Patterns;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AuthDto {

    @Schema(name = "AuthDto.SignUp")
    @Data
    @NoArgsConstructor
    public static class SignUp {
        @NotNull
        @Email
        private String email;

        @Size(max = 30)
        @NotNull
        private String name;
        @NotNull
        private String password;

        @Builder
        public SignUp(String email, String name, String password) {
            this.email = email;
            this.name = name;
            this.password = password;
        }
    }

    @Schema(name = "AuthDto.SignIn")
    @Data
    @NoArgsConstructor
    public static class SignIn {
        @NotNull
        @Email
        private String email;
        @NotNull
        private String password;

        @Builder
        public SignIn(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    @Schema(name = "AuthDto.Token")
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Token {
        @Pattern(regexp = Patterns.JWT)
        @NotNull
        private String accessToken;

        @Pattern(regexp = Patterns.JWT)
        @NotNull
        private String refreshToken;

        @Builder
        public Token(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
