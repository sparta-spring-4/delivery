package com.zts.delivery.item.infrastructure.converter;

import com.zts.delivery.item.domain.Stock;
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
