package com.example.springdroolsintegration.util;

import com.example.springdroolsintegration.config.FileUploadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Utility class for securely storing uploaded files.
 * This class provides methods to store files in a secure location with proper permissions.
 */
@Component
public class SecureFileStorage {
    
    private static final Logger logger = LoggerFactory.getLogger(SecureFileStorage.class);
    
    private final FileUploadConfig fileUploadConfig;
    private final FileValidator fileValidator;
    private final FileSanitizer fileSanitizer;
    
    /**
     * Constructor for SecureFileStorage.
     * 
     * @param fileUploadConfig The file upload configuration
     * @param fileValidator The file validator
     * @param fileSanitizer The file sanitizer
     */
    public SecureFileStorage(FileUploadConfig fileUploadConfig, 
                            FileValidator fileValidator,
                            FileSanitizer fileSanitizer) {
        this.fileUploadConfig = fileUploadConfig;
        this.fileValidator = fileValidator;
        this.fileSanitizer = fileSanitizer;
    }
    
    /**
     * Stores a file securely.
     * 
     * @param file The file to store
     * @param subdirectory Optional subdirectory within the upload directory
     * @return The path to the stored file
     * @throws IOException If an I/O error occurs
     * @throws SecurityException If the file fails validation or sanitization
     */
    public Path storeFile(MultipartFile file, String subdirectory) throws IOException, SecurityException {
        // Validate the file using FileValidator
        String validationError = fileValidator.getRuleFileValidationError(file);
        if (validationError != null) {
            throw new SecurityException("File validation failed: " + validationError);
        }
        
        // Additional sanitization using FileSanitizer
        if (!fileSanitizer.isSafeFile(file)) {
            throw new SecurityException("File sanitization failed: File content may be malicious");
        }
        
        // Sanitize the original filename
        String originalFilename = file.getOriginalFilename();
        String sanitizedFilename = fileSanitizer.sanitizeFileName(originalFilename);
        if (sanitizedFilename == null) {
            throw new SecurityException("Invalid filename: " + originalFilename);
        }
        
        // Create a secure filename
        String secureFilename = createSecureFilename(file);
        
        // Create the target directory
        Path baseDir = Paths.get(fileUploadConfig.getTempUploadDir());
        Path targetDir = baseDir;
        
        // Sanitize and resolve subdirectory if provided
        if (subdirectory != null && !subdirectory.isEmpty()) {
            Path sanitizedSubdirPath = fileSanitizer.sanitizePath(baseDir, subdirectory);
            if (sanitizedSubdirPath == null) {
                throw new SecurityException("Invalid subdirectory path: " + subdirectory);
            }
            targetDir = sanitizedSubdirPath;
        }
        
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
            // Set directory permissions (owner read/write/execute only)
            File dirFile = targetDir.toFile();
            dirFile.setReadable(true, true);
            dirFile.setWritable(true, true);
            dirFile.setExecutable(true, true);
        }
        
        // Create the target file path
        Path targetPath = targetDir.resolve(secureFilename);
        
        // Store the file
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Set file permissions (owner read/write only)
        File targetFile = targetPath.toFile();
        targetFile.setReadable(true, true);
        targetFile.setWritable(true, true);
        targetFile.setExecutable(false);
        
        logger.info("Securely stored file: {} as {}", originalFilename, targetPath);
        
        return targetPath;
    }
    
    /**
     * Creates a secure filename for a file.
     * 
     * @param file The file
     * @return A secure filename
     */
    private String createSecureFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        
        // Create a unique filename using UUID and timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString();
        
        // Add a hash of the original filename for reference
        String filenameHash = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(originalFilename != null ? originalFilename.getBytes() : new byte[0]);
            filenameHash = "_" + HexFormat.of().formatHex(hashBytes).substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            logger.warn("Failed to create filename hash", e);
        }
        
        return timestamp + "_" + uniqueId + filenameHash + extension;
    }
    
    /**
     * Gets the upload directory.
     * 
     * @return The upload directory
     */
    public String getUploadDirectory() {
        return fileUploadConfig.getTempUploadDir();
    }
}