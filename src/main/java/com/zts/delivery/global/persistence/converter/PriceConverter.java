package com.zts.delivery.global.persistence.converter;

import com.zts.delivery.global.persistence.Price;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Objects;

@Converter(autoApply = true)
public class PriceConverter implements AttributeConverter<Price, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Price price) {
        return price == null ? 0 : price.getValue();
    }

    @Override
    public Price convertToEntityAttribute(Integer value) {
        return new Price(Objects.requireNonNullElse(value, 0));
    }
}
