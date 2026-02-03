package com.dilaraalk.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "uploads" klasörünü "/uploads/**" URL'sine eşle
        // Docker içinde kesin yol: /app/uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/app/uploads/");
    }
}
