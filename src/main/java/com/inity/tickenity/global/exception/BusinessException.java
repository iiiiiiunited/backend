package com.inity.tickenity.global.exception;

import com.inity.tickenity.global.response.ResultCode;
import lombok.Getter;

public class BusinessException extends RuntimeException {

    @Getter
    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getDefaultMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

}
