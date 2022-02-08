package com.zoooohs.instagramclone.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 401 UNAUTHORIZED
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰 입니다."),

    // 404 NOT FOUND
    LOGIN_WRONG_INFO(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    // 409 CONFLICT
    SIGN_UP_DUPLICATED_EMAIL_OR_NAME(HttpStatus.CONFLICT, "중복 된 email 혹은 name 입니다."),

    // 500 INTERNAL
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류"),

    ;

    private HttpStatus status;
    private String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
