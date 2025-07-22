package com.example.springdroolsintegration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

/**
 * Configuration class for file upload settings.
 * This class configures file upload restrictions for security.
 */
@Configuration
public class FileUploadConfig {
    
    @Value("${app.rules.upload.max-file-size:5MB}")
    private String maxFileSize;
    
    @Value("${app.rules.upload.max-request-size:10MB}")
    private String maxRequestSize;
    
    /**
     * Configures multipart file upload settings.
     * This method sets maximum file size and request size for security.
     *
     * @return MultipartConfigElement with configured size limits
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Set maximum file size
        factory.setMaxFileSize(DataSize.parse(maxFileSize));
        
        // Set maximum request size (for multiple files)
        factory.setMaxRequestSize(DataSize.parse(maxRequestSize));
        
        return factory.createMultipartConfig();
    }
}