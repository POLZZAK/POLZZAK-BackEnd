package com.polzzak.common.model;

import lombok.Getter;

@Getter
public class PolzzakException extends RuntimeException {
    private ResultCode resultCode;

    public PolzzakException(final ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String getMessage() {
        return this.resultCode.getMessage();
    }
}
