package com.zts.delivery.store.domain.dto;

import com.zts.delivery.store.domain.Category;
import lombok.Builder;

import java.util.List;

@Builder
public record SearchDto(
        String storeName,
        String storeTel,
        String sido,
        String sigugun,
        String dong,
        String keyword,
        List<Category> category
) {}
