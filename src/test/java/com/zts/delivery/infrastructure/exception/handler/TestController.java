package com.zts.delivery.infrastructure.exception.handler;

import com.zts.delivery.global.infrastructure.execption.ApplicationException;
import com.zts.delivery.global.infrastructure.execption.ErrorCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/app-exception")
    public void applicationException() {
        throw new ApplicationException(ErrorCode.BAD_REQUEST);
    }

    @PostMapping("/test/field-exception")
    public void fieldErrorException(@RequestBody @Valid TestDto testDto) {
    }

    @GetMapping("/test/uncaught-exception")
    public void uncaughtException() throws Exception {
        throw new Exception();
    }
}
