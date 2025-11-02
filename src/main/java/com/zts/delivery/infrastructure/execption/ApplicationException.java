package com.zts.delivery.infrastructure.execption;

import lombok.Getter;
import org.springframework.web.client.HttpStatusCodeException;

@Getter
public class ApplicationException extends HttpStatusCodeException {

    private int code;
    private String message;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus());
        this.code = errorCode.getCode();
        this.message = errorCode.getDefaultMessage();
    }

    public ApplicationException(ErrorCode errorCode, String message) {
        super(errorCode.getHttpStatus());
        this.code = errorCode.getCode();
        this.message = message;
    }
}
