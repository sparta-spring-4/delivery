package com.zts.delivery.infrastructure.execption.handler;

import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.infrastructure.execption.ErrorResponse;
import com.zts.delivery.infrastructure.execption.FieldError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(value = ApplicationException.class)
    public ResponseEntity<ErrorResponse<Void>> applicationException(ApplicationException e) {
        log.error("ApplicationException", e);

        return ResponseEntity
                .status(e.getStatusCode())
                .body(
                        new ErrorResponse<>(e.getCode(), e.getMessage(), null)
                );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<List<FieldError>>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors()
                .stream().map(
                        it -> new FieldError(it.getField(), it.getRejectedValue(), it.getDefaultMessage())
                ).toList();
        ErrorCode errorCode = ErrorCode.REQUEST_VALIDATION_ERROR;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorResponse<>(errorCode.getCode(), errorCode.getMessage(), fieldErrors));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse<Void>> exception(Exception e) {
        log.error("Exception", e);

        ErrorCode internalServerError = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(internalServerError.getHttpStatus())
                .body(
                        new ErrorResponse<>(internalServerError.getCode(), internalServerError.getMessage(), null));
    }
}
