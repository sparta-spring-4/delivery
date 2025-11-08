package com.zts.delivery.global.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping
    public String redirectSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
