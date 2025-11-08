package com.zts.delivery.global.persistence.common;

import lombok.Builder;

@Builder
public record Pagination(
        int page,
        int size, // 한페이지 당 레코드 갯수
        int total, // 전체 레코드 갯수
        int totalPages // 전체 페이지 갯
) {}
