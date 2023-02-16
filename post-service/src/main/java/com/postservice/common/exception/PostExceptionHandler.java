package com.postservice.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.postservice.common.exception.dto.ErrorResponseDto;
import com.postservice.common.exception.dto.NotValidResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class PostExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(Exception e) {
        log.error("handleException", e);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.INTER_SERVER_ERROR);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorResponseDto> handlePostException(PostException e) {
        log.error("handlePostException", e);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(e.getErrorCode());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> handleBindException(BindException e) {
        log.error("handleBindException", e);
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

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpClientErrorException(HttpClientErrorException e) throws JsonProcessingException {
        log.error("handleHttpClientErrorException", e);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()); // LocalDateTime 변환
        ErrorResponseDto errorResponseDto = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponseDto.class);

        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }
}