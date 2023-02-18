package com.authserver.common.exception;

import com.authserver.common.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<NotValidResponseDto> notValidResponseDtoList = new ArrayList<>() {{
            for (FieldError fieldError : fieldErrors) {
                NotValidResponseDto notValidResponseDto = new NotValidResponseDto(fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());
                add(notValidResponseDto);
            }
        }};

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.NOT_VALID, notValidResponseDtoList);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }
}