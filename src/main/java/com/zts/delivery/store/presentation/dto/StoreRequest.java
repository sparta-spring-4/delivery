package com.zts.delivery.store.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Builder
public record StoreRequest(
        @NotBlank(message = "매장명을 입력하세요.") String storeName,
        @NotBlank(message = "매장 주소를 입력하세요.") String storeAddress,
        @NotBlank(message = "매장 전화번호를 입력하세요.") String storeTel,

        @DateTimeFormat(pattern = "HH:mm")
        LocalTime startHour,

        @DateTimeFormat(pattern = "HH:mm")
        LocalTime endHour,
        List<DayOfWeek> weekdays,

        List<CategoryDto> category
) {}
