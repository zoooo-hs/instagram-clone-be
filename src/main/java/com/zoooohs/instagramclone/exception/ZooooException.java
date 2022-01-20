package com.zoooohs.instagramclone.exception;

import lombok.Getter;

@Getter
public class ZooooException extends RuntimeException {
    private final ErrorCode errorCode;

    public ZooooException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
