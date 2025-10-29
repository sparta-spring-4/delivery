package com.zts.delivery.infrastructure.execption;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse<T> {

    private int code;
    private String message;
    private T details;
}
