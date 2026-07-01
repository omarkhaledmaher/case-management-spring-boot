package com.example.common.enums.converter;

import org.springframework.core.convert.converter.Converter;
import com.example.common.enums.CaseType;

public class StringToCaseTypeConverter implements Converter<String, CaseType> {
    @Override
    public CaseType convert(String source) {
        try {
            return CaseType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
