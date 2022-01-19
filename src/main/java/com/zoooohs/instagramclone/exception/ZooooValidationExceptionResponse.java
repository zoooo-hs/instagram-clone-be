package com.zoooohs.instagramclone.exception;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ZooooValidationExceptionResponse extends ZooooExceptionResponse {
    private List<Validation> validations;

    public ZooooValidationExceptionResponse(HttpStatus status, String message, String code, List<Validation> validations) {
        super(status, message, code);
        this.validations = validations;
    }

    @Data
    public static class Validation {
        private String field;
        private String rejectedValue;
        private String defaultMessage;

        @Builder
        public Validation(String field, String rejectedValue, String defaultMessage) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.defaultMessage = defaultMessage;
        }
    }

}
