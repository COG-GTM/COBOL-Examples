package com.example.cobolmigration.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Maps the COBOL {@code is_enabled pic x} field, stored in PostgreSQL as a
 * {@code varchar(1)} of 'Y'/'N', to a Java {@link Boolean}. This mirrors the
 * level-88 condition names {@code ws-account-enabled VALUE 'Y'} and
 * {@code ws-account-disabled VALUE 'N'} from {@code sql/sql_example.cbl}.
 */
@Converter(autoApply = false)
public class YesNoBooleanConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return Boolean.TRUE.equals(attribute) ? "Y" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return "Y".equalsIgnoreCase(dbData);
    }
}
