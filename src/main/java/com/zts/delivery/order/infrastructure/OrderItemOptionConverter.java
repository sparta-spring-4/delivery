package com.zts.delivery.order.infrastructure;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.order.domain.OrderItemOption;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

/**
 * 옵션이름_금액_수량||옵션이름_금액_수량..
 *
 */
@Converter(autoApply = true)
public class OrderItemOptionConverter implements AttributeConverter<List<OrderItemOption>, String> {
    @Override
    public String convertToDatabaseColumn(List<OrderItemOption> attribute) {
        return attribute == null ? null : attribute.stream()
            .map(o -> String.format("%s_%d", o.getName(), o.getPrice().getValue())).collect(
                Collectors.joining("||"));

    }

    @Override
    public List<OrderItemOption> convertToEntityAttribute(String dbData) {
        return StringUtils.hasText(dbData) ? Arrays.stream(dbData.split("\\|\\|"))
            .map(s -> {
                String[] values = s.split("_");
                return OrderItemOption.builder()
                    .name(values[0])
                    .price(new Price(Integer.parseInt(values[1])))
                    .build();
            }).toList() : null;
    }
}
