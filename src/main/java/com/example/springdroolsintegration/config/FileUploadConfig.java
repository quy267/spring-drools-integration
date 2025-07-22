package com.example.springdroolsintegration.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration class for file upload settings.
 * This class configures file upload restrictions for security.
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadConfig.class);
    
    @Value("${app.rules.upload.max-file-size:5MB}")
    private String maxFileSize;
    
    @Value("${app.rules.upload.max-request-size:10MB}")
    private String maxRequestSize;
    
    @Value("${app.rules.upload.allowed-extensions:.drl,.xls,.xlsx}")
    private String allowedExtensions;
    
    @Value("${app.rules.upload.temp-dir:${java.io.tmpdir}/rule-uploads}")
    private String tempUploadDir;
    
    /**
     * Gets the temporary upload directory.
     * 
     * @return The temporary upload directory
     */
    public String getTempUploadDir() {
        return tempUploadDir;
    }
    
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
        
        // Set temporary upload location
        try {
            Path uploadPath = Paths.get(tempUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created temporary upload directory: {}", uploadPath);
            }
            factory.setLocation(tempUploadDir);
            logger.info("Set temporary upload directory to: {}", tempUploadDir);
        } catch (IOException e) {
            logger.error("Failed to create temporary upload directory: {}", tempUploadDir, e);
        }
        
        return factory.createMultipartConfig();
    }
    
    /**
     * Creates a MultipartResolver for handling file uploads.
     * 
     * @return StandardServletMultipartResolver
     */
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    
    /**
     * Gets the allowed file extensions for rule files.
     * 
     * @return Array of allowed file extensions
     */
    public String[] getAllowedExtensions() {
        return allowedExtensions.split(",");
    }
}