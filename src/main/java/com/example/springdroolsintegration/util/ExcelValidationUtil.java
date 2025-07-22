package com.example.springdroolsintegration.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for validating Excel decision table files.
 * This class provides methods to validate Excel files for use as Drools decision tables.
 */
public class ExcelValidationUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelValidationUtil.class);
    
    // Valid content types for Excel files
    private static final Set<String> VALID_EXCEL_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/excel",
            "application/x-excel",
            "application/x-msexcel"
    ));
    
    // Required column headers for decision tables
    private static final Set<String> REQUIRED_COLUMN_HEADERS = new HashSet<>(Arrays.asList(
            "RuleSet",
            "RuleTable"
    ));
    
    /**
     * Validates an Excel file for use as a Drools decision table.
     * This method checks content type, file structure, and required headers.
     *
     * @param file The Excel file to validate
     * @return A list of validation errors, empty if valid
     */
    public static List<String> validateExcelFile(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        
        // Check if file is empty
        if (file.isEmpty()) {
            errors.add("File is empty");
            return errors;
        }
        
        // Validate content type
        if (!isValidContentType(file)) {
            errors.add("Invalid content type: " + file.getContentType() + 
                    ". Expected Excel file content type.");
        }
        
        // Validate file structure and content
        try {
            validateExcelStructure(file, errors);
        } catch (IOException e) {
            logger.error("Error validating Excel structure", e);
            errors.add("Error reading Excel file: " + e.getMessage());
        }
        
        return errors;
    }
    
    /**
     * Checks if the file has a valid Excel content type.
     *
     * @param file The file to check
     * @return true if the content type is valid, false otherwise
     */
    public static boolean isValidContentType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && VALID_EXCEL_CONTENT_TYPES.contains(contentType);
    }
    
    /**
     * Validates the structure of an Excel file for use as a decision table.
     * This method checks for required headers and basic structure.
     *
     * @param file The Excel file to validate
     * @param errors List to add validation errors to
     * @throws IOException if there is an error reading the file
     */
    private static void validateExcelStructure(MultipartFile file, List<String> errors) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook;
            
            // Create workbook based on file extension
            if (file.getOriginalFilename().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else {
                workbook = new HSSFWorkbook(is);
            }
            
            // Check if workbook has at least one sheet
            if (workbook.getNumberOfSheets() == 0) {
                errors.add("Excel file does not contain any sheets");
                return;
            }
            
            // Check each sheet for decision table structure
            boolean foundDecisionTable = false;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                
                // Check if sheet has required headers
                if (hasRequiredHeaders(sheet)) {
                    foundDecisionTable = true;
                    
                    // Validate decision table structure
                    validateDecisionTableStructure(sheet, errors);
                }
            }
            
            if (!foundDecisionTable) {
                errors.add("No valid decision table found in Excel file. " +
                        "Make sure it contains RuleSet and RuleTable headers.");
            }
        }
    }
    
    /**
     * Checks if a sheet has the required headers for a decision table.
     *
     * @param sheet The sheet to check
     * @return true if the sheet has required headers, false otherwise
     */
    private static boolean hasRequiredHeaders(Sheet sheet) {
        Set<String> foundHeaders = new HashSet<>();
        
        // Check first 10 rows for headers
        for (int rowIndex = 0; rowIndex < Math.min(10, sheet.getLastRowNum() + 1); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            
            for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                if (cell == null) continue;
                
                String cellValue = cell.toString().trim();
                if (REQUIRED_COLUMN_HEADERS.contains(cellValue)) {
                    foundHeaders.add(cellValue);
                }
            }
        }
        
        return foundHeaders.containsAll(REQUIRED_COLUMN_HEADERS);
    }
    
    /**
     * Validates the structure of a decision table sheet.
     * This method checks for condition and action columns.
     *
     * @param sheet The sheet to validate
     * @param errors List to add validation errors to
     */
    private static void validateDecisionTableStructure(Sheet sheet, List<String> errors) {
        boolean foundRuleTable = false;
        boolean hasConditionColumn = false;
        boolean hasActionColumn = false;
        
        // Find RuleTable row
        for (int rowIndex = 0; rowIndex < Math.min(20, sheet.getLastRowNum() + 1); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            
            for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                if (cell == null) continue;
                
                String cellValue = cell.toString().trim();
                
                // Found RuleTable, check next row for condition and action columns
                if (cellValue.equals("RuleTable")) {
                    foundRuleTable = true;
                    
                    // Check next row for condition and action columns
                    if (rowIndex + 1 <= sheet.getLastRowNum()) {
                        Row nextRow = sheet.getRow(rowIndex + 1);
                        if (nextRow != null) {
                            for (int nextCellIndex = 0; nextCellIndex < nextRow.getLastCellNum(); nextCellIndex++) {
                                Cell nextCell = nextRow.getCell(nextCellIndex);
                                if (nextCell == null) continue;
                                
                                String nextCellValue = nextCell.toString().trim();
                                if (nextCellValue.startsWith("CONDITION")) {
                                    hasConditionColumn = true;
                                } else if (nextCellValue.startsWith("ACTION")) {
                                    hasActionColumn = true;
                                }
                            }
                        }
                    }
                    
                    break;
                }
            }
            
            if (foundRuleTable) break;
        }
        
        if (!foundRuleTable) {
            errors.add("RuleTable header not found in expected format");
        } else {
            if (!hasConditionColumn) {
                errors.add("No CONDITION columns found in decision table");
            }
            if (!hasActionColumn) {
                errors.add("No ACTION columns found in decision table");
            }
        }
    }
}