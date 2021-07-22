package com.antra.evaluation.reporting_system.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDateTime;

public class LocalDateTimeToStringTypeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {
    @Override
    public String convert(LocalDateTime localDateTime) {
        return localDateTime.toString();
    }

    @Override
    public LocalDateTime unconvert(String s) {
        return LocalDateTime.parse(s);
    }
}
