package com.fullcycle.admin.catalogo.infrastructure.video.persistence;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import com.fullcycle.admin.catalogo.domain.video.Rating;

@Converter(autoApply = true)
public class JpaAttributeConverterImpl implements AttributeConverter<Rating, String> {

    @Override
    public String convertToDatabaseColumn(Rating attribute) {
        if (attribute == null) return null;
        return attribute.getName();
    }

    @Override
    public Rating convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Rating.of(dbData).orElse(null);
    }
}
