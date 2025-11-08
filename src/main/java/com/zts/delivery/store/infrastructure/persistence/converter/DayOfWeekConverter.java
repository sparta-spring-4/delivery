package com.zts.delivery.store.infrastructure.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class DayOfWeekConverter implements AttributeConverter<List<DayOfWeek>, String> {
    @Override
    public String convertToDatabaseColumn(List<DayOfWeek> attribute) {
        return attribute == null ? null : attribute.stream().map(DayOfWeek::name).collect(Collectors.joining(","));
    }

    @Override
    public List<DayOfWeek> convertToEntityAttribute(String dbData) {
        return StringUtils.hasText(dbData) ? Arrays.stream(dbData.split(",")).map(DayOfWeek::valueOf).toList() : null;
    }
}
