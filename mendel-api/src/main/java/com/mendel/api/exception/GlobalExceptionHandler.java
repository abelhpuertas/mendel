package com.mendel.api.exception;

import com.mendel.business.exception.MendelException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MendelException.class)
    public ResponseEntity<Map<String, Object>> handleMendelException(MendelException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", ex.getExceptionCode().getCode());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, ex.getExceptionCode().getHttpStatus());
    }
}
