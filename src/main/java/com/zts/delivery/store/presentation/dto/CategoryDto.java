package com.zts.delivery.store.presentation.dto;

import com.zts.delivery.store.domain.Category;

public record CategoryDto(
        Category category,
        boolean active
) {}
