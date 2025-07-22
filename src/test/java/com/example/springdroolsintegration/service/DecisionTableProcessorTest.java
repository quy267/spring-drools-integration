package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.exception.DecisionTableValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DecisionTableProcessor class.
 * These tests verify that the decision table processor correctly handles various input scenarios.
 */
class DecisionTableProcessorTest {

    private DecisionTableProcessor processor;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        processor = new DecisionTableProcessor();
    }
    
    /**
     * Test that a valid decision table is processed correctly.
     */
    @Test
    void testValidDecisionTable() throws IOException {
        // Create a valid decision table
        Path filePath = createValidDecisionTable("valid-table.xlsx");
        
        // Convert to DRL
        String drl = processor.convertToDrl(filePath.toString());
        
        // Verify that the DRL was generated
        assertNotNull(drl);
        assertTrue(drl.contains("package"));
        assertTrue(drl.contains("rule"));
    }
    
    /**
     * Test that a decision table with multiple sheets is processed correctly.
     */
    @Test
    void testMultipleSheets() throws IOException {
        // Create a decision table with multiple sheets
        Path filePath = createMultiSheetDecisionTable("multi-sheet.xlsx");
        
        // Get sheet names
        List<String> sheetNames = processor.getSheetNames(filePath);
        
        // Verify that both sheets are detected
        assertEquals(2, sheetNames.size());
        assertTrue(sheetNames.contains("DiscountRules"));
        assertTrue(sheetNames.contains("LoanRules"));
        
        // Convert specific sheet to DRL
        String drl = processor.convertToDrl(filePath.toString(), "DiscountRules");
        
        // Verify that the DRL was generated
        assertNotNull(drl);
        assertTrue(drl.contains("package"));
        assertTrue(drl.contains("rule"));
        
        // Convert multiple sheets to DRL
        Map<String, String> drlMap = processor.convertToDrl(filePath.toString(), Arrays.asList("DiscountRules", "LoanRules"));
        
        // Verify that both sheets were converted
        assertEquals(2, drlMap.size());
        assertTrue(drlMap.containsKey("DiscountRules"));
        assertTrue(drlMap.containsKey("LoanRules"));
    }
    
    /**
     * Test that an invalid file type is rejected.
     */
    @Test
    void testInvalidFileType() {
        // Create a text file
        Path filePath = tempDir.resolve("invalid.txt");
        try {
            Files.write(filePath, "This is not an Excel file".getBytes());
            
            // Try to convert to DRL
            Exception exception = assertThrows(DecisionTableValidationException.class, () -> {
                processor.convertToDrl(filePath.toString());
            });
            
            // Verify the exception message
            assertTrue(exception.getMessage().contains("Invalid file type"));
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }
    
    /**
     * Test that a decision table with missing headers is rejected.
     */
    @Test
    void testMissingHeaders() throws IOException {
        // Create a decision table with missing headers
        Path filePath = createInvalidDecisionTable("missing-headers.xlsx", true, false);
        
        // Try to convert to DRL
        Exception exception = assertThrows(DecisionTableValidationException.class, () -> {
            processor.convertToDrl(filePath.toString());
        });
        
        // Verify the exception message
        assertTrue(exception.getMessage().contains("missing required headers"));
    }
    
    /**
     * Test that an empty decision table is rejected.
     */
    @Test
    void testEmptyTable() throws IOException {
        // Create an empty decision table
        Path filePath = createInvalidDecisionTable("empty-table.xlsx", false, true);
        
        // Try to convert to DRL
        Exception exception = assertThrows(DecisionTableValidationException.class, () -> {
            processor.convertToDrl(filePath.toString());
        });
        
        // Verify the exception message
        assertTrue(exception.getMessage().contains("contains no valid sheets with data"));
    }
    
    /**
     * Test that a MultipartFile is processed correctly.
     */
    @Test
    void testMultipartFile() throws IOException {
        // Create a valid decision table
        Path filePath = createValidDecisionTable("valid-table.xlsx");
        
        // Create a MultipartFile from the file
        byte[] content = Files.readAllBytes(filePath);
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "valid-table.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                content);
        
        // Convert to DRL
        String drl = processor.convertToDrl(multipartFile);
        
        // Verify that the DRL was generated
        assertNotNull(drl);
        assertTrue(drl.contains("package"));
        assertTrue(drl.contains("rule"));
    }
    
    /**
     * Test that a non-existent sheet is rejected.
     */
    @Test
    void testNonExistentSheet() throws IOException {
        // Create a valid decision table
        Path filePath = createValidDecisionTable("valid-table.xlsx");
        
        // Try to convert a non-existent sheet to DRL
        Exception exception = assertThrows(DecisionTableValidationException.class, () -> {
            processor.convertToDrl(filePath.toString(), "NonExistentSheet");
        });
        
        // Verify the exception message
        assertTrue(exception.getMessage().contains("Sheet not found"));
    }
    
    /**
     * Creates a valid decision table for testing.
     *
     * @param filename The name of the file to create
     * @return The path to the created file
     * @throws IOException if there is an error creating the file
     */
    private Path createValidDecisionTable(String filename) throws IOException {
        Path filePath = tempDir.resolve(filename);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("DiscountRules");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("RuleSet");
            headerRow.createCell(1).setCellValue("RuleId");
            headerRow.createCell(2).setCellValue("Condition");
            headerRow.createCell(3).setCellValue("Action");
            headerRow.createCell(4).setCellValue("Customer Age");
            headerRow.createCell(5).setCellValue("Loyalty Tier");
            headerRow.createCell(6).setCellValue("Discount Percentage");
            
            // Create data row
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("CustomerDiscountRules");
            dataRow.createCell(1).setCellValue("Senior Discount");
            dataRow.createCell(4).setCellValue("> 60");
            dataRow.createCell(6).setCellValue("10");
            
            // Write the workbook to a file
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                workbook.write(fos);
            }
        }
        
        return filePath;
    }
    
    /**
     * Creates a decision table with multiple sheets for testing.
     *
     * @param filename The name of the file to create
     * @return The path to the created file
     * @throws IOException if there is an error creating the file
     */
    private Path createMultiSheetDecisionTable(String filename) throws IOException {
        Path filePath = tempDir.resolve(filename);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create first sheet
            Sheet sheet1 = workbook.createSheet("DiscountRules");
            
            // Create header row
            Row headerRow1 = sheet1.createRow(0);
            headerRow1.createCell(0).setCellValue("RuleSet");
            headerRow1.createCell(1).setCellValue("RuleId");
            headerRow1.createCell(2).setCellValue("Condition");
            headerRow1.createCell(3).setCellValue("Action");
            headerRow1.createCell(4).setCellValue("Customer Age");
            headerRow1.createCell(5).setCellValue("Discount Percentage");
            
            // Create data row
            Row dataRow1 = sheet1.createRow(1);
            dataRow1.createCell(0).setCellValue("CustomerDiscountRules");
            dataRow1.createCell(1).setCellValue("Senior Discount");
            dataRow1.createCell(4).setCellValue("> 60");
            dataRow1.createCell(5).setCellValue("10");
            
            // Create second sheet
            Sheet sheet2 = workbook.createSheet("LoanRules");
            
            // Create header row
            Row headerRow2 = sheet2.createRow(0);
            headerRow2.createCell(0).setCellValue("RuleSet");
            headerRow2.createCell(1).setCellValue("RuleId");
            headerRow2.createCell(2).setCellValue("Condition");
            headerRow2.createCell(3).setCellValue("Action");
            headerRow2.createCell(4).setCellValue("Credit Score");
            headerRow2.createCell(5).setCellValue("Approval Status");
            
            // Create data row
            Row dataRow2 = sheet2.createRow(1);
            dataRow2.createCell(0).setCellValue("LoanApprovalRules");
            dataRow2.createCell(1).setCellValue("Good Credit Approval");
            dataRow2.createCell(4).setCellValue("> 700");
            dataRow2.createCell(5).setCellValue("\"APPROVED\"");
            
            // Write the workbook to a file
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                workbook.write(fos);
            }
        }
        
        return filePath;
    }
    
    /**
     * Creates an invalid decision table for testing.
     *
     * @param filename The name of the file to create
     * @param missingHeaders Whether to create a table with missing headers
     * @param emptyTable Whether to create an empty table
     * @return The path to the created file
     * @throws IOException if there is an error creating the file
     */
    private Path createInvalidDecisionTable(String filename, boolean missingHeaders, boolean emptyTable) throws IOException {
        Path filePath = tempDir.resolve(filename);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("InvalidRules");
            
            if (!emptyTable) {
                // Create header row
                Row headerRow = sheet.createRow(0);
                
                if (missingHeaders) {
                    // Missing required headers
                    headerRow.createCell(0).setCellValue("SomeHeader");
                    headerRow.createCell(1).setCellValue("AnotherHeader");
                } else {
                    // Valid headers
                    headerRow.createCell(0).setCellValue("RuleSet");
                    headerRow.createCell(1).setCellValue("RuleId");
                    headerRow.createCell(2).setCellValue("Condition");
                    headerRow.createCell(3).setCellValue("Action");
                }
                
                // Create data row if not testing empty table
                if (!emptyTable) {
                    Row dataRow = sheet.createRow(1);
                    dataRow.createCell(0).setCellValue("SomeValue");
                    dataRow.createCell(1).setCellValue("AnotherValue");
                }
            }
            
            // Write the workbook to a file
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                workbook.write(fos);
            }
        }
        
        return filePath;
    }
}