package com.zts.delivery.order.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CartRequest(
    @Schema(title = "", example = "", format = "string")
    UUID itemId,

    @Schema(title = "옵션인덱스 리스트", example = "1,2,3", format = "int32")
    List<Integer> options
) {

}
