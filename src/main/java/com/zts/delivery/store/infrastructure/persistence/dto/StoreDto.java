package com.zts.delivery.store.infrastructure.persistence.dto;

import com.zts.delivery.store.domain.Category;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Builder
public record StoreDto(
    UUID storeId,
    UUID ownerId,
    String ownerName,
    String storeName,
    String storeTel,
    String address,
    double latitude,
    double longitude,
    LocalTime startHour,
    LocalTime endHour,
    List<DayOfWeek> weekdays,
    List<Category> category,
    LocalDateTime createdAt,
    int reviewCount,
    BigDecimal averageReviewScore
) {}
