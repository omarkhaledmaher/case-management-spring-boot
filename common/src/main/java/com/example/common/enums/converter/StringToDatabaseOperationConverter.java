package com.example.common.enums.converter;

import org.springframework.core.convert.converter.Converter;
import com.example.common.enums.DatabaseOperation;

public class StringToDatabaseOperationConverter implements Converter<String, DatabaseOperation> {
    @Override
    public DatabaseOperation convert(String source) {
        try {
            return DatabaseOperation.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
