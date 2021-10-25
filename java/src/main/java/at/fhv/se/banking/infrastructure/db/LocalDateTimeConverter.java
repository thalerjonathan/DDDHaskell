package at.fhv.se.banking.infrastructure.db;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Date> {
    @Override
    public Date convertToDatabaseColumn(LocalDateTime timeToConvert) {
        if(timeToConvert == null){
            return null;
        }

        return java.util.Date
            .from(timeToConvert.atZone(ZoneId.systemDefault())
            .toInstant());
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Date date) {
        if(date == null){
            return null;
        }

        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}