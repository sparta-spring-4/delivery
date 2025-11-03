package com.zts.delivery.infrastructure.execption;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    OK(HttpStatus.OK, 200, "OK"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "Bad Request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404, "Not Found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Internal Server Error"),

    // validation
    REQUEST_VALIDATION_ERROR(HttpStatus.PRECONDITION_FAILED, 1000, "Request value is not valid"),

    // user
    DUPLICATED_USERNAME(HttpStatus.CONFLICT, 2000, "Username is already taken."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, 2001, "Email is already in use."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,2002, "User Not Found"),

    // token
    TOKEN_ISSUE_ERROR(HttpStatus.UNAUTHORIZED, 3000, "TOKEN_ISSUE_ERROR");

    private final HttpStatus httpStatus;
    private final int code;
    private final String defaultMessage;
}
