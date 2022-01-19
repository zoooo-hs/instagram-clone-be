package com.zoooohs.instagramclone.configuration;

import com.zoooohs.instagramclone.exception.ZooooException;
import com.zoooohs.instagramclone.exception.ZooooExceptionResponse;
import com.zoooohs.instagramclone.exception.ZooooValidationExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerConfiguration extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ZooooException.class)
    public ResponseEntity<ZooooExceptionResponse> zooooExceptionHandler(HttpServletRequest request, final ZooooException e) {
        // TODO: AOP로 로깅하기
        e.printStackTrace();
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(ZooooExceptionResponse.builder()
                .status(e.getErrorCode().getStatus())
                .message(e.getErrorCode().getMessage())
                .code(e.getErrorCode().name())
                .build());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        // TODO: AOP로 로깅하기
        e.printStackTrace();
        List<ZooooValidationExceptionResponse.Validation> validations = e.getFieldErrors().stream()
                .map(error -> ZooooValidationExceptionResponse.Validation.builder()
                        .field(error.getField())
                        .rejectedValue(error.getRejectedValue().toString())
                        .defaultMessage(error.getDefaultMessage())
                        .build()).collect(Collectors.toList());

        ZooooValidationExceptionResponse response = new ZooooValidationExceptionResponse(HttpStatus.BAD_REQUEST, "입력 값 오류", "ARGUMENT_NOT_VALID", validations);
        response.setValidations(validations);
        return handleExceptionInternal(e, response, headers, status, request);
    }

    @ExceptionHandler({
            Exception.class,
            IOException.class
    })
    public ResponseEntity<ZooooExceptionResponse> exceptionHandler(HttpServletRequest request, final Exception e) {
        // TODO: AOP로 로깅하기
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ZooooExceptionResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("알수 없는 에러")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .build());
    }
}
