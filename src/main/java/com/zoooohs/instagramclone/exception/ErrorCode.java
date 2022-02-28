package com.zoooohs.instagramclone.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 400 BAD_REQUEST
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "올바르지 않은 파일 타입 입니다."),

    // 401 UNAUTHORIZED
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰 입니다."),
    USER_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "Email 인증이 완료되지 않은 계정입니다."),

    // 404 NOT FOUND
    LOGIN_WRONG_INFO(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 찾을 수 없습니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우를 찾을 수 없습니다."),

    // 409 CONFLICT
    SIGN_UP_DUPLICATED_EMAIL_OR_NAME(HttpStatus.CONFLICT, "중복 된 email 혹은 name 입니다."),
    ALREADY_LIKED_POST(HttpStatus.CONFLICT, "이미 좋아요 한 게시글 입니다."),
    ALREADY_LIKED_COMMENT(HttpStatus.CONFLICT, "이미 좋아요 한 댓글 입니다."),
    ALREADY_FOLLOWED_USER(HttpStatus.CONFLICT, "이미 팔로우 한 사용자 입니다."),
    FOLLOWING_SELF(HttpStatus.CONFLICT, "자기 자신은 영원한 친구 입니다."),

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
