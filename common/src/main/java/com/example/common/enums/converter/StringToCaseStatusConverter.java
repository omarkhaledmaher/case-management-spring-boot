package com.example.common.enums.converter;

import org.springframework.core.convert.converter.Converter;
import com.example.common.enums.CaseStatus;

public class StringToCaseStatusConverter implements Converter<String, CaseStatus> {
    @Override
    public CaseStatus convert(String source) {
        try {
            return CaseStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
