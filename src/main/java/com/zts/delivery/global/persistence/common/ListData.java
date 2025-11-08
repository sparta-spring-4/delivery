package com.zts.delivery.global.persistence.common;

import java.util.List;

public record ListData<T>(
        List<T> items,
        Pagination pagination
) {}
