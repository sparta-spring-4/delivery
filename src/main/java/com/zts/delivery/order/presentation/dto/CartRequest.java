package com.zts.delivery.order.presentation.dto;

import com.zts.delivery.menu.domain.ItemId;
import com.zts.delivery.order.domain.cart.CartId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CartRequest(

    @Schema(title = "아이템ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", format = "uuid")
    ItemId itemId,

    @Schema(title = "옵션인덱스 리스트", example = "1,2,3", format = "int32")
    List<Integer> options
) {

}
