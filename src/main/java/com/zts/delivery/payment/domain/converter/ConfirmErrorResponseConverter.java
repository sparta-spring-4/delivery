package com.zts.delivery.payment.domain.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zts.delivery.payment.domain.ConfirmErrorResponse;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Converter
@RequiredArgsConstructor
@Component
public class ConfirmErrorResponseConverter implements AttributeConverter<List<ConfirmErrorResponse>, String> {

    private final ObjectMapper objectMapper;


    @Override
    public String convertToDatabaseColumn(List<ConfirmErrorResponse> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ConfirmErrorResponse> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize", e);
            throw new RuntimeException(e);
        }
    }
}
