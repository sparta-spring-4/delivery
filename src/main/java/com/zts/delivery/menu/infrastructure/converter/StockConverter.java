package com.zts.delivery.menu.infrastructure.converter;

import com.zts.delivery.menu.domain.Stock;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StockConverter implements AttributeConverter<Stock, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Stock attribute) {
        return attribute.getValue();
    }

    @Override
    public Stock convertToEntityAttribute(Integer value) {
        return new Stock(value == null ? 0 : value);
    }
}
