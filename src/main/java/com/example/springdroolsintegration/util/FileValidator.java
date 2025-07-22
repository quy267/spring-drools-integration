package com.example.springdroolsintegration.util;

import com.example.springdroolsintegration.config.FileUploadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for validating uploaded files.
 * This class provides methods to validate file types, sizes, and other properties.
 */
@Component
public class FileValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(FileValidator.class);
    
    // Allowed MIME types for rule files
    private static final Set<String> ALLOWED_RULE_FILE_MIME_TYPES = new HashSet<>(
            Arrays.asList(
                    "text/plain", // For .drl files
                    "application/vnd.ms-excel", // For .xls files
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // For .xlsx files
                    "application/octet-stream" // Fallback for some systems
            ));
    
    // Excel file signatures (magic numbers)
    private static final byte[][] EXCEL_SIGNATURES = {
            {(byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0}, // .xls (OLE2)
            {(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04}  // .xlsx (ZIP/OOXML)
    };
    
    // DRL file signature (first few characters should be "package" or "import" or "rule")
    private static final String[] DRL_SIGNATURES = {
            "package", "import", "rule", "function", "global"
    };
    
    private final FileUploadConfig fileUploadConfig;
    
    /**
     * Constructor for FileValidator.
     * 
     * @param fileUploadConfig The file upload configuration
     */
    public FileValidator(FileUploadConfig fileUploadConfig) {
        this.fileUploadConfig = fileUploadConfig;
    }
    
    /**
     * Validates a rule file upload.
     * Checks if the file is not null, has a valid extension, and a valid MIME type.
     *
     * @param file The file to validate
     * @return true if the file is valid, false otherwise
     */
    public boolean isValidRuleFile(MultipartFile file) {
        return getRuleFileValidationError(file) == null;
    }
    
    /**
     * Gets a validation error message for a rule file.
     * 
     * @param file The file to validate
     * @return An error message if the file is invalid, or null if the file is valid
     */
    public String getRuleFileValidationError(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "File is required and cannot be empty";
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "Original filename is required";
        }
        
        // Check file extension
        boolean hasValidExtension = false;
        String[] allowedExtensions = fileUploadConfig.getAllowedExtensions();
        for (String extension : allowedExtensions) {
            if (originalFilename.toLowerCase().endsWith(extension.toLowerCase())) {
                hasValidExtension = true;
                break;
            }
        }
        
        if (!hasValidExtension) {
            return "Invalid file extension. Allowed extensions: " + 
                    String.join(", ", allowedExtensions);
        }
        
        // Check MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_RULE_FILE_MIME_TYPES.contains(contentType)) {
            logger.warn("Suspicious content type: {} for file: {}", contentType, originalFilename);
            // Don't reject based on MIME type alone, as some systems may report different MIME types
        }
        
        // Perform content validation based on file type
        try {
            if (originalFilename.toLowerCase().endsWith(".drl")) {
                String validationError = validateDrlContent(file);
                if (validationError != null) {
                    return validationError;
                }
            } else if (originalFilename.toLowerCase().endsWith(".xls") || 
                       originalFilename.toLowerCase().endsWith(".xlsx")) {
                String validationError = validateExcelContent(file);
                if (validationError != null) {
                    return validationError;
                }
            }
        } catch (IOException e) {
            logger.error("Error validating file content: {}", e.getMessage(), e);
            return "Error validating file content: " + e.getMessage();
        }
        
        return null; // No validation error
    }
    
    /**
     * Validates the content of a DRL file.
     * 
     * @param file The DRL file to validate
     * @return An error message if the file is invalid, or null if the file is valid
     * @throws IOException If an I/O error occurs
     */
    private String validateDrlContent(MultipartFile file) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream())) {
            byte[] buffer = new byte[1024];
            int bytesRead = bis.read(buffer);
            
            if (bytesRead > 0) {
                String content = new String(buffer, 0, bytesRead).trim().toLowerCase();
                
                // Check if the file starts with any of the DRL signatures
                boolean validSignature = false;
                for (String signature : DRL_SIGNATURES) {
                    if (content.startsWith(signature) || content.contains("\n" + signature)) {
                        validSignature = true;
                        break;
                    }
                }
                
                if (!validSignature) {
                    return "Invalid DRL file content. File does not appear to be a valid Drools rule file.";
                }
            }
        }
        
        return null; // No validation error
    }
    
    /**
     * Validates the content of an Excel file.
     * 
     * @param file The Excel file to validate
     * @return An error message if the file is invalid, or null if the file is valid
     * @throws IOException If an I/O error occurs
     */
    private String validateExcelContent(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            int bytesRead = is.read(header);
            
            if (bytesRead < 4) {
                return "Invalid Excel file: File is too small";
            }
            
            boolean validSignature = false;
            for (byte[] signature : EXCEL_SIGNATURES) {
                if (Arrays.equals(Arrays.copyOf(header, signature.length), signature)) {
                    validSignature = true;
                    break;
                }
            }
            
            if (!validSignature) {
                return "Invalid Excel file: File does not have a valid Excel signature";
            }
        }
        
        return null; // No validation error
    }
}