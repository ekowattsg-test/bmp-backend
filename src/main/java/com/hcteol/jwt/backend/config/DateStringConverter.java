package com.hcteol.jwt.backend.config;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts between a Java String (ISO date "yyyy-MM-dd") and a SQL DATE column.
 * This allows entity fields to remain String while the DB column stays as DATE,
 * preventing Hibernate from attempting an ALTER TABLE on existing databases.
 */
@Converter
public class DateStringConverter implements AttributeConverter<String, Date> {

    @Override
    public Date convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank()) {
            return null;
        }
        try {
            return Date.valueOf(LocalDate.parse(attribute.trim()));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String convertToEntityAttribute(Date dbData) {
        if (dbData == null) {
            return null;
        }
        return dbData.toLocalDate().toString();
    }
}
