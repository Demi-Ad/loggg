package com.example.logserver.entity;

import jakarta.persistence.AttributeConverter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TimestampConverter implements AttributeConverter<Long, LocalDateTime> {
    @Override
    public LocalDateTime convertToDatabaseColumn(Long attribute) {
        Timestamp timestamp = new Timestamp(attribute);
        return timestamp.toLocalDateTime();
    }

    @Override
    public Long convertToEntityAttribute(LocalDateTime dbData) {
        return Timestamp.valueOf(dbData).getTime();
    }
}
