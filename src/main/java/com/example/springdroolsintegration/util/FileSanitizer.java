package com.example.springdroolsintegration.util;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utility class for sanitizing file names, paths, and content.
 * This class provides methods to prevent security vulnerabilities like path traversal,
 * XML external entity (XXE) attacks, and other file-related security issues.
 */
@Component
public class FileSanitizer {

    private static final Logger logger = LoggerFactory.getLogger(FileSanitizer.class);
    
    // Allowed file extensions
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList(".drl", ".xls", ".xlsx"));
    
    // Pattern for safe file names (alphanumeric, underscore, hyphen, period)
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-\\.]+$");
    
    // Pattern for potentially malicious XML content
    private static final Pattern MALICIOUS_XML_PATTERN = Pattern.compile(
            "<!ENTITY|<!DOCTYPE|<!ELEMENT|<!ATTLIST|<!NOTATION|<\\?xml-stylesheet");
    
    /**
     * Sanitizes a file name to prevent path traversal and other attacks.
     * 
     * @param fileName The file name to sanitize
     * @return The sanitized file name, or null if the file name is invalid
     */
    public String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            logger.warn("Empty or null file name provided");
            return null;
        }
        
        // Get the base name without path
        String baseName = FilenameUtils.getName(fileName);
        
        // Check if the file name matches the safe pattern
        if (!SAFE_FILENAME_PATTERN.matcher(baseName).matches()) {
            logger.warn("File name contains invalid characters: {}", baseName);
            return null;
        }
        
        // Check if the extension is allowed
        String extension = FilenameUtils.getExtension(baseName);
        if (extension == null || !isAllowedExtension("." + extension.toLowerCase())) {
            logger.warn("File has disallowed extension: {}", extension);
            return null;
        }
        
        return baseName;
    }
    
    /**
     * Sanitizes a file path to prevent path traversal attacks.
     * 
     * @param basePath The base path (must be absolute)
     * @param relativePath The relative path to sanitize
     * @return The sanitized absolute path, or null if the path is invalid
     */
    public Path sanitizePath(Path basePath, String relativePath) {
        if (basePath == null || !basePath.isAbsolute()) {
            logger.error("Base path must be absolute");
            return null;
        }
        
        if (relativePath == null || relativePath.isEmpty()) {
            return basePath;
        }
        
        // Check for and reject any path traversal attempts immediately
        if (relativePath.contains("..") || relativePath.contains("./") || relativePath.contains(".\\")) {
            logger.warn("Path traversal attempt detected: {}", relativePath);
            return null;
        }
        
        // Remove any path traversal sequences (additional safety)
        String sanitized = relativePath
                .replaceAll("\\.\\./", "")
                .replaceAll("\\.\\.\\\\", "")
                .replaceAll("\\\\", "/")
                .replaceAll("\\.\\.", "");
        
        // Remove any leading or trailing slashes
        sanitized = sanitized.replaceAll("^/+", "").replaceAll("/+$", "");
        
        try {
            // Resolve the path and ensure it's within the base path
            Path resolvedPath = basePath.resolve(sanitized).normalize();
            
            if (!resolvedPath.startsWith(basePath)) {
                logger.warn("Path traversal attempt detected: {}", relativePath);
                return null;
            }
            
            return resolvedPath;
        } catch (Exception e) {
            logger.error("Error sanitizing path: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Sanitizes XML content to prevent XXE attacks.
     * 
     * @param content The XML content to sanitize
     * @return true if the content is safe, false otherwise
     */
    public boolean isSafeXmlContent(byte[] content) {
        if (content == null || content.length == 0) {
            return false;
        }
        
        // Check for potentially malicious XML patterns
        String contentStr = new String(content, StandardCharsets.UTF_8);
        if (MALICIOUS_XML_PATTERN.matcher(contentStr).find()) {
            logger.warn("Potentially malicious XML content detected");
            return false;
        }
        
        // Use SAX parser with secure settings to validate XML
        try (InputStream is = new ByteArrayInputStream(content)) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            
            // Disable external entities and DTDs
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setXIncludeAware(false);
            factory.setNamespaceAware(true);
            
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.parse(new InputSource(is));
            
            return true;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.warn("XML validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Sanitizes a MultipartFile by checking its name, content type, and content.
     * 
     * @param file The MultipartFile to sanitize
     * @return true if the file is safe, false otherwise
     */
    public boolean isSafeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Empty or null file provided");
            return false;
        }
        
        // Check file name
        String fileName = file.getOriginalFilename();
        if (sanitizeFileName(fileName) == null) {
            return false;
        }
        
        // Check file extension
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        if (!isAllowedExtension("." + extension)) {
            logger.warn("File has disallowed extension: {}", extension);
            return false;
        }
        
        // For XML-based files (like Excel files), check for XXE vulnerabilities
        if (extension.equals("xls") || extension.equals("xlsx")) {
            try {
                byte[] content = file.getBytes();
                if (!isSafeXmlContent(content)) {
                    logger.warn("Unsafe XML content detected in file: {}", fileName);
                    return false;
                }
            } catch (IOException e) {
                logger.error("Error reading file content: {}", e.getMessage(), e);
                return false;
            }
        }
        
        // For DRL files, check for basic content safety (no need for XML validation)
        if (extension.equals("drl")) {
            try {
                byte[] content = file.getBytes();
                String contentStr = new String(content, StandardCharsets.UTF_8);
                // Check for potentially malicious content patterns in DRL
                if (contentStr.contains("System.exit") || 
                    contentStr.contains("Runtime.getRuntime") ||
                    contentStr.contains("<!DOCTYPE") ||
                    contentStr.contains("<!ENTITY") ||
                    contentStr.contains("SYSTEM") ||
                    contentStr.contains("file://")) {
                    logger.warn("Potentially unsafe DRL content detected in file: {}", fileName);
                    return false;
                }
            } catch (IOException e) {
                logger.error("Error reading file content: {}", e.getMessage(), e);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if a file extension is allowed.
     * 
     * @param extension The file extension to check (including the dot)
     * @return true if the extension is allowed, false otherwise
     */
    public boolean isAllowedExtension(String extension) {
        return extension != null && ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }
}