package com.mendel.business.exception;

import com.mendel.util.enums.EMendelExceptionCode;
import lombok.Getter;

@Getter
public class MendelException extends RuntimeException {
    private final EMendelExceptionCode exceptionCode;

    public MendelException(EMendelExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public MendelException(EMendelExceptionCode exceptionCode, String customMessage) {
        super(customMessage);
        this.exceptionCode = exceptionCode;
    }
}
