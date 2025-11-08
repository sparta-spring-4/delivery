package com.zts.delivery.store.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class StoreNotEditableException extends HttpStatusCodeException {
    public StoreNotEditableException() { this("매장의 수정/삭제 권한이 없습니다."); }

    public StoreNotEditableException(String message) {super(HttpStatus.UNAUTHORIZED, message);}
}
