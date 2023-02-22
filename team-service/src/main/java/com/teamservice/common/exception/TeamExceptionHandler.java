package com.teamservice.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.teamservice.common.exception.dto.ErrorResponseDto;
import com.teamservice.common.exception.dto.NotValidResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class TeamExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        log.error("handleException", e);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.INTER_SERVER_ERROR);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }

    @ExceptionHandler(TeamException.class)
    public ResponseEntity<ErrorResponseDto> handleTeamException(TeamException e) {
        log.error("handleTeamException", e);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(e.getErrorCode());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
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

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpClientErrorException(HttpClientErrorException e) throws JsonProcessingException {
        log.error("handleHttpClientErrorException", e);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ErrorResponseDto errorResponseDto = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponseDto.class);

        return new ResponseEntity<>(errorResponseDto, HttpStatus.valueOf(errorResponseDto.getStatus()));
    }
}