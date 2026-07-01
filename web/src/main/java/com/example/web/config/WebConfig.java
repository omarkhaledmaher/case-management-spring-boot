package com.example.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.example.common.enums.converter.StringToCaseStatusConverter;
import com.example.common.enums.converter.StringToCaseTypeConverter;
import com.example.common.enums.converter.StringToDatabaseOperationConverter;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToCaseStatusConverter());
        registry.addConverter(new StringToCaseTypeConverter());
        registry.addConverter(new StringToDatabaseOperationConverter());
    }
}
