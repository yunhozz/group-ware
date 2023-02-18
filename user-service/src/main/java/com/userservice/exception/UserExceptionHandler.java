package com.userservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.userservice.exception.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpClientErrorException(HttpClientErrorException e) throws JsonProcessingException {
        log.error("handleHttpClientErrorException", e);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()); // LocalDateTime 변환
        ErrorResponseDto errorResponseDto = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponseDto.class);

        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleNotAuthorizedException(NotAuthorizedException e) {
        log.error("handleNotAuthorizedException", e);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.NOT_AUTHORIZED);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }
}