package com.hcteol.jwt.backend.config;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcteol.jwt.backend.entities.BundleMember;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BundleMemberListJsonConverter implements AttributeConverter<List<BundleMember>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<BundleMember> attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize List<BundleMember> to JSON", e);
        }
    }

    @Override
    public List<BundleMember> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<BundleMember>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to List<BundleMember>", e);
        }
    }
}
