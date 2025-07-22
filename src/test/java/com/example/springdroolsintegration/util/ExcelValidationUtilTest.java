package com.example.springdroolsintegration.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ExcelValidationUtil class.
 * These tests verify that the Excel validation logic correctly identifies valid and invalid decision tables.
 */
public class ExcelValidationUtilTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Test validation of empty file")
    void testValidateEmptyFile() {
        // Create an empty file
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new byte[0]
        );

        // Validate the file
        List<String> errors = ExcelValidationUtil.validateExcelFile(emptyFile);

        // Assert that validation fails with appropriate error
        assertFalse(errors.isEmpty());
        assertTrue(errors.contains("File is empty"));
    }

    @Test
    @DisplayName("Test validation of invalid content type")
    void testValidateInvalidContentType() {
        // Create a file with invalid content type
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "invalid.txt",
                "text/plain",
                "This is not an Excel file".getBytes()
        );

        // Validate the file
        List<String> errors = ExcelValidationUtil.validateExcelFile(invalidFile);

        // Assert that validation fails with appropriate error
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("Invalid content type")));
    }

    @Test
    @DisplayName("Test validation of file with no sheets")
    void testValidateFileWithNoSheets() throws IOException {
        // Create an Excel file with no sheets
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        MockMultipartFile noSheetsFile = new MockMultipartFile(
                "file",
                "no-sheets.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );

        // Validate the file
        List<String> errors = ExcelValidationUtil.validateExcelFile(noSheetsFile);

        // Assert that validation fails with appropriate error
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("does not contain any sheets")));
    }

    @Test
    @DisplayName("Test validation of file missing required headers")
    void testValidateFileMissingRequiredHeaders() throws IOException {
        // Create an Excel file with a sheet but missing required headers
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        
        // Add some content but not the required headers
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Some Header");
        row.createCell(1).setCellValue("Another Header");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        MockMultipartFile missingHeadersFile = new MockMultipartFile(
                "file",
                "missing-headers.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );

        // Validate the file
        List<String> errors = ExcelValidationUtil.validateExcelFile(missingHeadersFile);

        // Assert that validation fails with appropriate error
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("No valid decision table found")));
    }

    @Test
    @DisplayName("Test validation of file missing condition columns")
    void testValidateFileMissingConditionColumns() throws IOException {
        // Create an Excel file with required headers but missing condition columns
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        
        // Add RuleSet header
        Row ruleSetRow = sheet.createRow(0);
        ruleSetRow.createCell(0).setCellValue("RuleSet");
        ruleSetRow.createCell(1).setCellValue("CustomerDiscountRules");
        
        // Add RuleTable header
        Row ruleTableRow = sheet.createRow(2);
        ruleTableRow.createCell(0).setCellValue("RuleTable");
        ruleTableRow.createCell(1).setCellValue("Discount Rules");
        
        // Add column types row but only with ACTION columns
        Row columnTypesRow = sheet.createRow(3);
        columnTypesRow.createCell(0).setCellValue("ACTION");
        columnTypesRow.createCell(1).setCellValue("ACTION");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        MockMultipartFile missingConditionsFile = new MockMultipartFile(
                "file",
                "missing-conditions.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );

        // Validate the file
        List<String> errors = ExcelValidationUtil.validateExcelFile(missingConditionsFile);

        // Assert that validation fails with appropriate error
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("No CONDITION columns found")));
    }

    @Test
    @DisplayName("Test validation of file missing action columns")
    void testValidateFileMissingActionColumns() throws IOException {
        // Create an Excel file with required headers but missing action columns
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        
        // Add RuleSet header
        Row ruleSetRow = sheet.createRow(0);
        ruleSetRow.createCell(0).setCellValue("RuleSet");
        ruleSetRow.createCell(1).setCellValue("CustomerDiscountRules");
        
        // Add RuleTable header
        Row ruleTableRow = sheet.createRow(2);
        ruleTableRow.createCell(0).setCellValue("RuleTable");
        ruleTableRow.createCell(1).setCellValue("Discount Rules");
        
        // Add column types row but only with CONDITION columns
        Row columnTypesRow = sheet.createRow(3);
        columnTypesRow.createCell(0).setCellValue("CONDITION");
        columnTypesRow.createCell(1).setCellValue("CONDITION");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        MockMultipartFile missingActionsFile = new MockMultipartFile(
                "file",
                "missing-actions.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );

        // Validate the file
        List<String> errors = ExcelValidationUtil.validateExcelFile(missingActionsFile);

        // Assert that validation fails with appropriate error
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("No ACTION columns found")));
    }

    @Test
    @DisplayName("Test validation of valid decision table")
    void testValidateValidDecisionTable() throws IOException {
        // Create a valid Excel decision table
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CustomerDiscountRules");
        
        // Add RuleSet header
        Row ruleSetRow = sheet.createRow(0);
        ruleSetRow.createCell(0).setCellValue("RuleSet");
        ruleSetRow.createCell(1).setCellValue("CustomerDiscountRules");
        
        // Add RuleTable header
        Row ruleTableRow = sheet.createRow(2);
        ruleTableRow.createCell(0).setCellValue("RuleTable");
        ruleTableRow.createCell(1).setCellValue("Discount Rules");
        
        // Add column types row with both CONDITION and ACTION columns
        Row columnTypesRow = sheet.createRow(3);
        columnTypesRow.createCell(0).setCellValue("CONDITION");
        columnTypesRow.createCell(1).setCellValue("CONDITION");
        columnTypesRow.createCell(2).setCellValue("ACTION");
        columnTypesRow.createCell(3).setCellValue("ACTION");
        
        // Add column headers row
        Row headerRow = sheet.createRow(4);
        headerRow.createCell(0).setCellValue("Customer Age");
        headerRow.createCell(1).setCellValue("Loyalty Tier");
        headerRow.createCell(2).setCellValue("Discount Percentage");
        headerRow.createCell(3).setCellValue("Applied Rule");
        
        // Add data rows
        Row dataRow1 = sheet.createRow(5);
        dataRow1.createCell(0).setCellValue("> 60");
        dataRow1.createCell(1).setCellValue("== \"GOLD\"");
        dataRow1.createCell(2).setCellValue("20");
        dataRow1.createCell(3).setCellValue("\"Premium Customer Discount\"");
        
        Row dataRow2 = sheet.createRow(6);
        dataRow2.createCell(0).setCellValue("");
        dataRow2.createCell(1).setCellValue("== \"GOLD\"");
        dataRow2.createCell(2).setCellValue("15");
        dataRow2.createCell(3).setCellValue("\"Gold Tier Discount\"");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "valid-decision-table.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );

        // Validate the file
        List<String> errors = ExcelValidationUtil.validateExcelFile(validFile);

        // Assert that validation passes (no errors)
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("Test validation of decision table with multiple sheets")
    void testValidateDecisionTableWithMultipleSheets() throws IOException {
        // Create an Excel file with multiple sheets, one valid and one invalid
        Workbook workbook = new XSSFWorkbook();
        
        // Create a valid sheet
        Sheet validSheet = workbook.createSheet("ValidSheet");
        
        // Add RuleSet header
        Row ruleSetRow = validSheet.createRow(0);
        ruleSetRow.createCell(0).setCellValue("RuleSet");
        ruleSetRow.createCell(1).setCellValue("CustomerDiscountRules");
        
        // Add RuleTable header
        Row ruleTableRow = validSheet.createRow(2);
        ruleTableRow.createCell(0).setCellValue("RuleTable");
        ruleTableRow.createCell(1).setCellValue("Discount Rules");
        
        // Add column types row with both CONDITION and ACTION columns
        Row columnTypesRow = validSheet.createRow(3);
        columnTypesRow.createCell(0).setCellValue("CONDITION");
        columnTypesRow.createCell(1).setCellValue("ACTION");
        
        // Create an invalid sheet (missing required headers)
        Sheet invalidSheet = workbook.createSheet("InvalidSheet");
        Row invalidRow = invalidSheet.createRow(0);
        invalidRow.createCell(0).setCellValue("Some Header");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        MockMultipartFile multiSheetFile = new MockMultipartFile(
                "file",
                "multi-sheet.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );

        // Validate the file
        List<String> errors = ExcelValidationUtil.validateExcelFile(multiSheetFile);

        // Assert that validation passes (at least one valid sheet)
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("Test isValidContentType method")
    void testIsValidContentType() {
        // Test with valid content types
        MockMultipartFile xlsFile = new MockMultipartFile(
                "file", "test.xls", "application/vnd.ms-excel", new byte[1]);
        MockMultipartFile xlsxFile = new MockMultipartFile(
                "file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[1]);
        MockMultipartFile excelFile = new MockMultipartFile(
                "file", "test.xlsx", "application/excel", new byte[1]);
        
        // Test with invalid content types
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", new byte[1]);
        MockMultipartFile txtFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", new byte[1]);
        MockMultipartFile nullContentTypeFile = new MockMultipartFile(
                "file", "test.xlsx", null, new byte[1]);
        
        // Assert valid content types
        assertTrue(ExcelValidationUtil.isValidContentType(xlsFile));
        assertTrue(ExcelValidationUtil.isValidContentType(xlsxFile));
        assertTrue(ExcelValidationUtil.isValidContentType(excelFile));
        
        // Assert invalid content types
        assertFalse(ExcelValidationUtil.isValidContentType(pdfFile));
        assertFalse(ExcelValidationUtil.isValidContentType(txtFile));
        assertFalse(ExcelValidationUtil.isValidContentType(nullContentTypeFile));
    }
}