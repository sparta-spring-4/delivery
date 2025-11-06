package com.zts.delivery.store.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Builder
public record StoreRequest(
        @NotBlank String storeName,
        @NotBlank String storeAddress,
        @NotBlank String storeTel,

        @DateTimeFormat(pattern = "HH:mm")
        LocalTime startHour,

        @DateTimeFormat(pattern = "HH:mm")
        LocalTime endHour,
        List<DayOfWeek> weekdays,

        List<CategoryDto> category
) {}
