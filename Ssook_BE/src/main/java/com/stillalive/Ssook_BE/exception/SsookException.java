package com.stillalive.Ssook_BE.exception;

import lombok.Getter;

@Getter
public class SsookException extends RuntimeException {

    private final ErrorCode errorCode;

    public SsookException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
