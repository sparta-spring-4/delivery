package com.zts.delivery.store.presentation.dto;

import com.zts.delivery.store.domain.Category;
import lombok.Builder;

import java.util.List;

@Builder
public record SearchRequest(
    String storeName,
    String storeTel,
    String sido,
    String sigugun,
    String dong,
    String keyword,
    List<Category> category,
    Integer page, // 1, 2, 3, 4,
    Integer size
) {}