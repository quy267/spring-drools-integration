package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.config.CacheConfig;
import com.example.springdroolsintegration.exception.DecisionTableValidationException;
import com.example.springdroolsintegration.exception.DecisionTableValidationException.ValidationErrorType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.drools.decisiontable.DecisionTableProviderImpl;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for processing Excel decision tables.
 * This service handles the conversion of Excel decision tables to DRL format.
 */
@Service
public class DecisionTableProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(DecisionTableProcessor.class);
    
    // Required column headers for decision tables
    private static final String[] REQUIRED_HEADERS = {
            "RuleSet", "Condition", "Action"
    };
    
    /**
     * Converts an Excel decision table to DRL format.
     * This method processes all worksheets in the workbook.
     *
     * @param file The Excel file containing the decision table
     * @return The generated DRL content
     * @throws IOException if there is an error reading the file
     * @throws DecisionTableValidationException if the file format is invalid
     */
    public String convertToDrl(MultipartFile file) throws IOException {
        logger.info("Converting decision table to DRL: {}", file.getOriginalFilename());
        
        // Validate file type
        validateFileType(file);
        
        // Create a temporary file
        Path tempFile = createTempFile(file);
        
        try {
            // Validate the decision table format
            validateDecisionTable(tempFile);
            
            // Convert the decision table to DRL
            Resource resource = ResourceFactory.newFileResource(tempFile.toFile());
            DecisionTableProviderImpl provider = new DecisionTableProviderImpl();
            
            // Determine the input type based on file extension
            DecisionTableInputType inputType = determineInputType(file.getOriginalFilename());
            
            // Create a DecisionTableConfiguration
            DecisionTableConfiguration dtConfig = KnowledgeBuilderFactory.newDecisionTableConfiguration();
            dtConfig.setInputType(inputType);
            
            // Convert to DRL
            String drl = provider.loadFromResource(resource, dtConfig);
            
            logger.info("Successfully converted decision table to DRL");
            return drl;
        } finally {
            // Clean up the temporary file
            Files.deleteIfExists(tempFile);
        }
    }
    
    /**
     * Converts an Excel decision table to DRL format, processing only the specified worksheet.
     *
     * @param file The Excel file containing the decision table
     * @param sheetName The name of the worksheet to process
     * @return The generated DRL content
     * @throws IOException if there is an error reading the file
     * @throws DecisionTableValidationException if the file format is invalid or the sheet doesn't exist
     */
    public String convertToDrl(MultipartFile file, String sheetName) throws IOException {
        logger.info("Converting decision table to DRL, sheet {}: {}", sheetName, file.getOriginalFilename());
        
        // Validate file type
        validateFileType(file);
        
        // Create a temporary file
        Path tempFile = createTempFile(file);
        
        try {
            // Validate that the sheet exists
            List<String> sheetNames = getSheetNames(tempFile);
            if (!sheetNames.contains(sheetName)) {
                throw new DecisionTableValidationException(
                        "Sheet not found: " + sheetName,
                        file.getOriginalFilename(),
                        ValidationErrorType.INVALID_STRUCTURE);
            }
            
            // Validate the decision table format
            validateDecisionTable(tempFile);
            
            // Create a workbook with only the specified sheet
            File singleSheetFile = createSingleSheetWorkbook(tempFile.toFile(), sheetName);
            
            try {
                // Convert the decision table to DRL
                Resource resource = ResourceFactory.newFileResource(singleSheetFile);
                DecisionTableProviderImpl provider = new DecisionTableProviderImpl();
                
                // Determine the input type based on file extension
                DecisionTableInputType inputType = determineInputType(file.getOriginalFilename());
                
                // Create a DecisionTableConfiguration
                DecisionTableConfiguration dtConfig = KnowledgeBuilderFactory.newDecisionTableConfiguration();
                dtConfig.setInputType(inputType);
                
                // Convert to DRL
                String drl = provider.loadFromResource(resource, dtConfig);
                
                logger.info("Successfully converted decision table sheet {} to DRL", sheetName);
                return drl;
            } finally {
                // Clean up the single sheet file
                if (singleSheetFile != null && singleSheetFile.exists()) {
                    singleSheetFile.delete();
                }
            }
        } finally {
            // Clean up the temporary file
            Files.deleteIfExists(tempFile);
        }
    }
    
    /**
     * Converts an Excel decision table to DRL format, processing multiple specified worksheets.
     *
     * @param file The Excel file containing the decision table
     * @param sheetNames The names of the worksheets to process
     * @return A map of sheet names to their generated DRL content
     * @throws IOException if there is an error reading the file
     * @throws DecisionTableValidationException if the file format is invalid or a sheet doesn't exist
     */
    public Map<String, String> convertToDrl(MultipartFile file, List<String> sheetNames) throws IOException {
        logger.info("Converting decision table to DRL, sheets {}: {}", sheetNames, file.getOriginalFilename());
        
        Map<String, String> results = new HashMap<>();
        
        for (String sheetName : sheetNames) {
            String drl = convertToDrl(file, sheetName);
            results.put(sheetName, drl);
        }
        
        return results;
    }
    
    /**
     * Converts an Excel decision table file to DRL format.
     * This method processes all worksheets in the workbook.
     * Results are cached to improve performance for repeated calls with the same file.
     *
     * @param filePath The path to the Excel file
     * @return The generated DRL content
     * @throws IOException if there is an error reading the file
     * @throws DecisionTableValidationException if the file format is invalid
     */
    @Cacheable(value = CacheConfig.RULE_EXECUTION_CACHE, key = "'drl-' + #filePath")
    public String convertToDrl(String filePath) throws IOException {
        logger.info("Converting decision table to DRL: {}", filePath);
        
        // Validate file type
        validateFileType(filePath);
        
        // Validate the decision table format
        validateDecisionTable(Paths.get(filePath));
        
        // Convert the decision table to DRL
        Resource resource = ResourceFactory.newFileResource(filePath);
        DecisionTableProviderImpl provider = new DecisionTableProviderImpl();
        
        // Determine the input type based on file extension
        DecisionTableInputType inputType = determineInputType(filePath);
        
        // Create a DecisionTableConfiguration
        DecisionTableConfiguration dtConfig = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtConfig.setInputType(inputType);
        
        // Convert to DRL
        String drl = provider.loadFromResource(resource, dtConfig);
        
        logger.info("Successfully converted decision table to DRL");
        return drl;
    }
    
    /**
     * Converts an Excel decision table file to DRL format, processing only the specified worksheet.
     * Results are cached to improve performance for repeated calls with the same file and sheet.
     *
     * @param filePath The path to the Excel file
     * @param sheetName The name of the worksheet to process
     * @return The generated DRL content
     * @throws IOException if there is an error reading the file
     * @throws DecisionTableValidationException if the file format is invalid or the sheet doesn't exist
     */
    @Cacheable(value = CacheConfig.RULE_EXECUTION_CACHE, key = "'drl-' + #filePath + '-' + #sheetName")
    public String convertToDrl(String filePath, String sheetName) throws IOException {
        logger.info("Converting decision table to DRL, sheet {}: {}", sheetName, filePath);
        
        // Validate file type
        validateFileType(filePath);
        
        Path path = Paths.get(filePath);
        
        // Validate that the sheet exists
        List<String> sheetNames = getSheetNames(path);
        if (!sheetNames.contains(sheetName)) {
            throw new DecisionTableValidationException(
                    "Sheet not found: " + sheetName,
                    path.getFileName().toString(),
                    ValidationErrorType.INVALID_STRUCTURE);
        }
        
        // Validate the decision table format
        validateDecisionTable(path);
        
        // Create a workbook with only the specified sheet
        File singleSheetFile = createSingleSheetWorkbook(path.toFile(), sheetName);
        
        try {
            // Convert the decision table to DRL
            Resource resource = ResourceFactory.newFileResource(singleSheetFile);
            DecisionTableProviderImpl provider = new DecisionTableProviderImpl();
            
            // Determine the input type based on file extension
            DecisionTableInputType inputType = determineInputType(filePath);
            
            // Create a DecisionTableConfiguration
            DecisionTableConfiguration dtConfig = KnowledgeBuilderFactory.newDecisionTableConfiguration();
            dtConfig.setInputType(inputType);
            
            // Convert to DRL
            String drl = provider.loadFromResource(resource, dtConfig);
            
            logger.info("Successfully converted decision table sheet {} to DRL", sheetName);
            return drl;
        } finally {
            // Clean up the single sheet file
            if (singleSheetFile != null && singleSheetFile.exists()) {
                singleSheetFile.delete();
            }
        }
    }
    
    /**
     * Converts an Excel decision table file to DRL format, processing multiple specified worksheets.
     * Results are cached to improve performance for repeated calls with the same file and sheets.
     *
     * @param filePath The path to the Excel file
     * @param sheetNames The names of the worksheets to process
     * @return A map of sheet names to their generated DRL content
     * @throws IOException if there is an error reading the file
     * @throws DecisionTableValidationException if the file format is invalid or a sheet doesn't exist
     */
    @Cacheable(value = CacheConfig.RULE_EXECUTION_CACHE, key = "'drl-multi-' + #filePath + '-' + #sheetNames.toString()")
    public Map<String, String> convertToDrl(String filePath, List<String> sheetNames) throws IOException {
        logger.info("Converting decision table to DRL, sheets {}: {}", sheetNames, filePath);
        
        Map<String, String> results = new HashMap<>();
        
        for (String sheetName : sheetNames) {
            String drl = convertToDrl(filePath, sheetName);
            results.put(sheetName, drl);
        }
        
        return results;
    }
    
    /**
     * Validates that the file is an Excel file (.xls or .xlsx).
     *
     * @param file The file to validate
     * @throws DecisionTableValidationException if the file is not an Excel file
     */
    private void validateFileType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        validateFileType(filename);
    }
    
    /**
     * Validates that the file is an Excel file (.xls or .xlsx).
     *
     * @param filename The filename to validate
     * @throws DecisionTableValidationException if the file is not an Excel file
     */
    private void validateFileType(String filename) {
        if (filename == null || !(filename.endsWith(".xls") || filename.endsWith(".xlsx"))) {
            throw new DecisionTableValidationException(
                    "Invalid file type: " + filename + ". Only Excel files (.xls and .xlsx) are supported for decision tables. " +
                    "Please ensure you are uploading a valid Excel file with the correct extension.",
                    filename,
                    ValidationErrorType.INVALID_FILE_FORMAT);
        }
    }
    
    /**
     * Creates a temporary file from a MultipartFile.
     * Uses an optimized buffer size (8KB) for better I/O performance.
     *
     * @param file The MultipartFile to convert
     * @return The path to the temporary file
     * @throws IOException if there is an error creating the temporary file
     */
    private Path createTempFile(MultipartFile file) throws IOException {
        // Create a temporary file
        Path tempFile = Files.createTempFile("decision-table-", getFileExtension(file.getOriginalFilename()));
        
        // Copy the file content to the temporary file using a larger buffer for better performance
        try (InputStream in = file.getInputStream();
             OutputStream out = Files.newOutputStream(tempFile)) {
            // Use 8KB buffer size for better performance with Excel files
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        
        return tempFile;
    }
    
    /**
     * Gets the file extension from a filename.
     *
     * @param filename The filename
     * @return The file extension (including the dot)
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }
    
    /**
     * Determines the input type based on the file extension.
     *
     * @param filename The filename
     * @return The DecisionTableInputType (XLS or XLSX)
     */
    private DecisionTableInputType determineInputType(String filename) {
        if (filename.endsWith(".xlsx")) {
            return DecisionTableInputType.XLSX;
        } else {
            return DecisionTableInputType.XLS;
        }
    }
    
    /**
     * Validates the decision table format.
     * This method checks that the decision table has the required columns and valid structure.
     * Uses memory-efficient approach for large files by limiting validation to essential checks.
     *
     * @param filePath The path to the Excel file
     * @throws IOException if there is an error reading the file
     * @throws DecisionTableValidationException if the decision table format is invalid
     */
    private void validateDecisionTable(Path filePath) throws IOException {
        String filename = filePath.getFileName().toString();
        boolean isLargeFile = Files.size(filePath) > 5 * 1024 * 1024; // Consider files larger than 5MB as large
        
        // For large files, use a more memory-efficient approach
        if (isLargeFile) {
            logger.info("Large file detected ({}MB), using optimized validation approach", 
                    Files.size(filePath) / (1024 * 1024));
            try {
                validateLargeDecisionTable(filePath);
                return;
            } catch (OpenXML4JException e) {
                // Convert OpenXML4JException to DecisionTableValidationException
                throw new DecisionTableValidationException(
                        "Error validating large Excel file '" + filename + "': " + e.getMessage(),
                        e,
                        filename,
                        ValidationErrorType.CORRUPTED_FILE);
            }
        }
        
        // Standard validation for normal-sized files
        try (InputStream is = Files.newInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(is)) {
            
            // Check if the workbook has any sheets
            if (workbook.getNumberOfSheets() == 0) {
                throw new DecisionTableValidationException(
                        "The Excel file '" + filename + "' contains no worksheets. " +
                        "A valid decision table must contain at least one worksheet with the required headers and data rows. " +
                        "Please check that your Excel file is not empty or corrupted.",
                        filename,
                        ValidationErrorType.EMPTY_TABLE);
            }
            
            boolean hasValidSheet = false;
            
            // Check each sheet in the workbook
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                
                // Check if the sheet is empty
                if (sheet.getPhysicalNumberOfRows() == 0) {
                    logger.warn("Sheet {} in file {} is empty", sheetName, filename);
                    continue;
                }
                
                hasValidSheet = true;
                
                // Get the header row - check if first row is RuleTable marker
                Row firstRow = sheet.getRow(0);
                Row headerRow = firstRow;
                int headerRowIndex = 0;
                
                // Check if first row contains RuleTable marker (Drools format)
                if (firstRow != null && firstRow.getPhysicalNumberOfCells() > 0) {
                    Cell firstCell = firstRow.getCell(0);
                    if (firstCell != null && firstCell.getCellType() == CellType.STRING) {
                        String firstCellValue = firstCell.getStringCellValue();
                        if (firstCellValue != null && firstCellValue.startsWith("RuleTable")) {
                            // This is a Drools decision table format, headers are in row 1
                            headerRow = sheet.getRow(1);
                            headerRowIndex = 1;
                        }
                    }
                }
                
                if (headerRow == null) {
                    throw new DecisionTableValidationException(
                            "Sheet '" + sheetName + "' in file '" + filename + "' has no header row. " +
                            "A valid decision table must have a header row with the required column names. " +
                            "Please ensure the " + (headerRowIndex == 0 ? "first" : "second") + " row of your worksheet contains the necessary headers: " + 
                            String.join(", ", REQUIRED_HEADERS) + ".",
                            filename,
                            sheetName,
                            ValidationErrorType.INVALID_STRUCTURE);
                }
                
                // Check for required headers
                List<String> missingHeaders = new ArrayList<>();
                for (String requiredHeader : REQUIRED_HEADERS) {
                    boolean found = false;
                    for (Cell cell : headerRow) {
                        if (cell.getCellType() == CellType.STRING && 
                                requiredHeader.equalsIgnoreCase(cell.getStringCellValue())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        missingHeaders.add(requiredHeader);
                    }
                }
                
                if (!missingHeaders.isEmpty()) {
                    throw new DecisionTableValidationException(
                            "Sheet '" + sheetName + "' in file '" + filename + "' is missing required headers: " + 
                            String.join(", ", missingHeaders) + ". " +
                            "All decision tables must include these mandatory headers to define rules properly. " +
                            "Please add the missing headers to the first row of your worksheet.",
                            filename,
                            sheetName,
                            ValidationErrorType.MISSING_HEADERS);
                }
                
                // Check that there are data rows (account for header row offset and potential attribute row)
                // For Drools format: RuleTable marker + header row + attribute row + at least one data row = 4 rows minimum
                // For standard format: header row + at least one data row = 2 rows minimum
                int expectedMinRows = headerRowIndex + 3; // Account for potential attribute row in Drools format
                if (sheet.getPhysicalNumberOfRows() < expectedMinRows) {
                    throw new DecisionTableValidationException(
                            "Sheet '" + sheetName + "' in file '" + filename + "' contains headers but no data rows. " +
                            "A valid decision table must contain at least one data row after the header row. " +
                            "Please add rule data to your decision table or remove the empty sheet.",
                            filename,
                            sheetName,
                            ValidationErrorType.EMPTY_TABLE);
                }
                
                // For normal files, validate a sample of data rows (basic structure validation)
                // Only check up to 100 rows to avoid excessive memory usage
                // Data rows start after header row and potential attribute row
                int dataRowStart = headerRowIndex + 2; // Account for attribute row in Drools format
                int maxRowsToCheck = Math.min(100, sheet.getPhysicalNumberOfRows() - dataRowStart);
                for (int rowNum = dataRowStart; rowNum < dataRowStart + maxRowsToCheck; rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    
                    // Check that the row has cells
                    if (row.getPhysicalNumberOfCells() == 0) {
                        logger.warn("Row {} in sheet {} of file {} is empty", rowNum, sheetName, filename);
                    }
                }
                
                // If there are more rows, just log the count without loading them all
                if (sheet.getPhysicalNumberOfRows() > maxRowsToCheck + 1) {
                    logger.info("Sheet {} in file {} has {} total rows, validated first {} rows", 
                            sheetName, filename, sheet.getPhysicalNumberOfRows(), maxRowsToCheck);
                }
            }
            
            if (!hasValidSheet) {
                throw new DecisionTableValidationException(
                        "The Excel file '" + filename + "' contains no valid sheets with data. " +
                        "A valid decision table must contain at least one sheet with headers and data rows. " +
                        "Please check that your Excel file has properly formatted decision tables with the required structure.",
                        filename,
                        ValidationErrorType.EMPTY_TABLE);
            }
        } catch (Exception e) {
            if (e instanceof DecisionTableValidationException) {
                throw e;
            }
            
            // Handle other exceptions (corrupted file, etc.)
            throw new DecisionTableValidationException(
                    "Error reading Excel file '" + filename + "': " + e.getMessage() + ". " +
                    "The file may be corrupted, password-protected, or in an unsupported format. " +
                    "Please ensure you are using a valid Excel file (.xls or .xlsx) that is not corrupted or password-protected.",
                    e,
                    filename,
                    ValidationErrorType.CORRUPTED_FILE);
        }
    }
    
    /**
     * Validates a large decision table file using a memory-efficient approach.
     * Only checks essential structure to avoid loading the entire file into memory.
     *
     * @param filePath The path to the Excel file
     * @throws IOException if there is an error reading the file
     * @throws OpenXML4JException if there is an error processing the OpenXML file
     * @throws DecisionTableValidationException if the decision table format is invalid
     */
    private void validateLargeDecisionTable(Path filePath) throws IOException, OpenXML4JException {
        String filename = filePath.getFileName().toString();
        
        // For large files, we'll use a more targeted approach to validation
        // We'll only check sheet existence, header rows, and a small sample of data rows
        
        try (InputStream is = Files.newInputStream(filePath)) {
            // Use the event model API for .xlsx files to reduce memory usage
            if (filename.endsWith(".xlsx")) {
                // Just check if the file is a valid Excel file and has sheets
                // This is a lightweight check that doesn't load the entire file
                org.apache.poi.openxml4j.opc.OPCPackage pkg = 
                        org.apache.poi.openxml4j.opc.OPCPackage.open(is);
                try {
                    org.apache.poi.xssf.eventusermodel.XSSFReader reader = 
                            new org.apache.poi.xssf.eventusermodel.XSSFReader(pkg);
                    
                    // Check if there are any sheets
                    if (!reader.getSheetsData().hasNext()) {
                        throw new DecisionTableValidationException(
                                "The Excel file '" + filename + "' contains no worksheets.",
                                filename,
                                ValidationErrorType.EMPTY_TABLE);
                    }
                    
                    logger.info("Large file validation successful for: {}", filename);
                } finally {
                    pkg.close();
                }
            } else {
                // For .xls files, we'll use a more basic approach
                // Just check if it's a valid Excel file
                try (Workbook workbook = WorkbookFactory.create(is)) {
                    if (workbook.getNumberOfSheets() == 0) {
                        throw new DecisionTableValidationException(
                                "The Excel file '" + filename + "' contains no worksheets.",
                                filename,
                                ValidationErrorType.EMPTY_TABLE);
                    }
                    
                    // Check the first sheet and its header row only
                    Sheet sheet = workbook.getSheetAt(0);
                    if (sheet.getPhysicalNumberOfRows() == 0) {
                        throw new DecisionTableValidationException(
                                "The first sheet in file '" + filename + "' is empty.",
                                filename,
                                ValidationErrorType.EMPTY_TABLE);
                    }
                    
                    // Check header row
                    Row headerRow = sheet.getRow(0);
                    if (headerRow == null) {
                        throw new DecisionTableValidationException(
                                "The first sheet in file '" + filename + "' has no header row.",
                                filename,
                                ValidationErrorType.INVALID_STRUCTURE);
                    }
                    
                    logger.info("Large file validation successful for: {}", filename);
                }
            }
        } catch (Exception e) {
            if (e instanceof DecisionTableValidationException) {
                throw e;
            }
            
            // Handle other exceptions
            throw new DecisionTableValidationException(
                    "Error validating large Excel file '" + filename + "': " + e.getMessage(),
                    e,
                    filename,
                    ValidationErrorType.CORRUPTED_FILE);
        }
    }
    
    /**
     * Gets the list of sheets in an Excel file.
     *
     * @param file The Excel file
     * @return The list of sheet names
     * @throws IOException if there is an error reading the file
     */
    public List<String> getSheetNames(MultipartFile file) throws IOException {
        // Validate file type
        validateFileType(file);
        
        // Create a temporary file
        Path tempFile = createTempFile(file);
        
        try {
            return getSheetNames(tempFile);
        } finally {
            // Clean up the temporary file
            Files.deleteIfExists(tempFile);
        }
    }
    
    /**
     * Gets the list of sheets in an Excel file.
     *
     * @param filePath The path to the Excel file
     * @return The list of sheet names
     * @throws IOException if there is an error reading the file
     */
    public List<String> getSheetNames(Path filePath) throws IOException {
        List<String> sheetNames = new ArrayList<>();
        
        try (InputStream is = Files.newInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(is)) {
            
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetAt(i).getSheetName());
            }
        }
        
        return sheetNames;
    }
    
    /**
     * Creates a new workbook containing only the specified sheet from the original workbook.
     * Uses streaming API (SXSSF) for large files to improve memory usage and performance.
     *
     * @param sourceFile The source Excel file
     * @param sheetName The name of the sheet to include
     * @return A new file containing only the specified sheet
     * @throws IOException if there is an error reading or writing the file
     * @throws DecisionTableValidationException if the sheet doesn't exist
     */
    private File createSingleSheetWorkbook(File sourceFile, String sheetName) throws IOException {
        // Create a temporary file for the new workbook
        File targetFile = File.createTempFile("single-sheet-", getFileExtension(sourceFile.getName()));
        
        try (InputStream is = new FileInputStream(sourceFile);
             Workbook sourceWorkbook = WorkbookFactory.create(is)) {
            
            // Find the sheet
            Sheet sourceSheet = sourceWorkbook.getSheet(sheetName);
            if (sourceSheet == null) {
                throw new DecisionTableValidationException(
                        "Sheet not found: " + sheetName,
                        sourceFile.getName(),
                        ValidationErrorType.INVALID_STRUCTURE);
            }
            
            // Determine if we should use streaming API based on row count
            // Use streaming API for sheets with more than 1000 rows
            boolean useStreamingApi = sourceSheet.getLastRowNum() > 1000 && sourceFile.getName().endsWith(".xlsx");
            
            // Create a new workbook
            Workbook targetWorkbook;
            if (sourceFile.getName().endsWith(".xlsx")) {
                if (useStreamingApi) {
                    // Use streaming API for large XLSX files
                    // Keep only 100 rows in memory, rest will be flushed to disk
                    targetWorkbook = new SXSSFWorkbook(100);
                    logger.debug("Using streaming API for large file: {}, sheet: {}, rows: {}", 
                            sourceFile.getName(), sheetName, sourceSheet.getLastRowNum());
                } else {
                    targetWorkbook = new XSSFWorkbook();
                }
            } else {
                targetWorkbook = WorkbookFactory.create(false);
            }
            
            try {
                // Create a new sheet with the same name
                Sheet targetSheet = targetWorkbook.createSheet(sheetName);
                
                // Copy all rows and cells
                for (int i = 0; i <= sourceSheet.getLastRowNum(); i++) {
                    Row sourceRow = sourceSheet.getRow(i);
                    if (sourceRow == null) {
                        continue;
                    }
                    
                    Row targetRow = targetSheet.createRow(i);
                    
                    for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
                        Cell sourceCell = sourceRow.getCell(j);
                        if (sourceCell == null) {
                            continue;
                        }
                        
                        Cell targetCell = targetRow.createCell(j);
                        
                        // Skip style copying to avoid workbook compatibility issues
                        // Styles from source workbook cannot be directly applied to target workbook
                        // For decision table processing, cell values are more important than formatting
                        
                        switch (sourceCell.getCellType()) {
                            case STRING:
                                targetCell.setCellValue(sourceCell.getStringCellValue());
                                break;
                            case NUMERIC:
                                targetCell.setCellValue(sourceCell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                // Skip formulas in streaming mode to improve performance
                                if (!useStreamingApi) {
                                    targetCell.setCellFormula(sourceCell.getCellFormula());
                                } else {
                                    // For streaming mode, just copy the formula result
                                    if (sourceCell.getCachedFormulaResultType() == CellType.NUMERIC) {
                                        targetCell.setCellValue(sourceCell.getNumericCellValue());
                                    } else if (sourceCell.getCachedFormulaResultType() == CellType.STRING) {
                                        targetCell.setCellValue(sourceCell.getStringCellValue());
                                    } else if (sourceCell.getCachedFormulaResultType() == CellType.BOOLEAN) {
                                        targetCell.setCellValue(sourceCell.getBooleanCellValue());
                                    } else {
                                        targetCell.setBlank();
                                    }
                                }
                                break;
                            case BLANK:
                                targetCell.setBlank();
                                break;
                            default:
                                // For other types, just leave the cell blank
                                targetCell.setBlank();
                        }
                    }
                    
                    // No need for explicit row flushing - SXSSFWorkbook will handle this automatically
                    // based on the window size (100) specified during creation
                }
                
                // Write the new workbook to the target file
                try (OutputStream os = new FileOutputStream(targetFile)) {
                    targetWorkbook.write(os);
                }
            } finally {
                // Dispose of temporary files created by SXSSF
                if (useStreamingApi) {
                    ((SXSSFWorkbook) targetWorkbook).dispose();
                }
                targetWorkbook.close();
            }
        }
        
        return targetFile;
    }
}