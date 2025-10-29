package com.zts.delivery.infrastructure.exception.handler;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class TestDto {

    @NotEmpty
    private String name;

    @Min(1)
    private Integer age;

    @NotNull
    private Boolean isAdult;

    public TestDto(String name, Integer age, Boolean isAdult) {
        this.name = name;
        this.age = age;
        this.isAdult = isAdult;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isAdult() {
        return isAdult;
    }
}
