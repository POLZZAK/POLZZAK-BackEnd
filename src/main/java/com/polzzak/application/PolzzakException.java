package com.polzzak.application;

import com.polzzak.application.dto.ErrorCode;
import lombok.Getter;

@Getter
public class PolzzakException extends RuntimeException {
    private ErrorCode errorCode;

    public PolzzakException(final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return this.errorCode.getMessage();
    }
}
