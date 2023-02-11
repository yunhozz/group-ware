package com.authserver.common.exception;

import com.authserver.common.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException e) {
        log.error("handleRuntimeException", e);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.INTER_SERVER_ERROR);
        return new ResponseEntity<>(errorResponseDto, HttpStatusCode.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponseDto> handleUserException(AuthException e) {
        log.error("handleUserException", e);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(e.getErrorCode());
        return new ResponseEntity<>(errorResponseDto, HttpStatusCode.valueOf(errorResponseDto.getStatus()));
    }
}