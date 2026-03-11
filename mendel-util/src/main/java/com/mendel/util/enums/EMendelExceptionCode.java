package com.mendel.util.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum EMendelExceptionCode {
    TRANSACTION_NOT_FOUND("MENDEL-001", "Transaction not found", HttpStatus.NOT_FOUND),
    INVALID_TRANSACTION_DATA("MENDEL-002", "Invalid transaction data", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    EMendelExceptionCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
