package com.qwerty.pastebook.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> message = new HashMap<>();

        if (e.getBindingResult().getAllErrors().size() == 1) {
            message.put("message", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        }
        else {
            message.put("message", "several fields failed validation");
            e.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                message.put(fieldName, errorMessage);
            });
        }
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleAll(Exception e) {
        Map<String, String> message = new HashMap<>();
        message.put("message", "Internal server error");
        log.error("Unhandled exception. Message: {}. Class: {}", e.getMessage(), e.getClass());
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}