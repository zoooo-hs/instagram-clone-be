package com.zoooohs.instagramclone.exception;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class ZooooExceptionResponse {
    private HttpStatus status;
    private String message;
    private String code;

    @Builder
    public ZooooExceptionResponse(HttpStatus status, String message, String code) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
