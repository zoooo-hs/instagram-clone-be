package com.zoooohs.instagramclone.domain.auth.dto;

import com.zoooohs.instagramclone.util.Patterns;
import lombok.*;

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
