package com.zts.delivery.payment.infrastructure.client;

import com.zts.delivery.infrastructure.execption.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class TossClientErrorException extends ApplicationException {

    public TossClientErrorException(HttpStatusCode statusCode, String code, String message) {
        super(statusCode, code, message);
    }
}
