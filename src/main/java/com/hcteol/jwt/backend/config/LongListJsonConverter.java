package com.hcteol.jwt.backend.config;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LongListJsonConverter implements AttributeConverter<List<Long>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize List<Long> to JSON", e);
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to List<Long>", e);
        }
    }
}
