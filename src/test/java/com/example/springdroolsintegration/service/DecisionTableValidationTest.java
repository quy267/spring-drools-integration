package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.exception.DecisionTableValidationException;
import com.example.springdroolsintegration.util.ExcelValidationUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for validating the project's decision tables based on their specifications.
 * These tests create Excel files that match the specifications in the .md files
 * and verify that they pass validation.
 */
public class DecisionTableValidationTest {

    @TempDir
    Path tempDir;

    private final DecisionTableProcessor processor = new DecisionTableProcessor();

    @Test
    @DisplayName("Test validation of CustomerDiscountRules decision table")
    void testValidateCustomerDiscountRules() throws IOException {
        // Create a CustomerDiscountRules decision table based on the specification
        Workbook workbook = createCustomerDiscountRulesTable();
        
        // Convert to MultipartFile
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        MultipartFile file = new MockMultipartFile(
                "CustomerDiscountRules.xlsx",
                "CustomerDiscountRules.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );
        
        // Validate using ExcelValidationUtil
        List<String> errors = ExcelValidationUtil.validateExcelFile(file);
        
        // Assert that validation passes (no errors)
        assertTrue(errors.isEmpty(), "CustomerDiscountRules validation failed with errors: " + errors);
        
        // Also test that the DecisionTableProcessor can convert it to DRL
        try {
            String drl = processor.convertToDrl(file);
            assertNotNull(drl);
            assertTrue(drl.contains("package CustomerDiscountRules"));
            assertTrue(drl.contains("rule \"Premium Customer\""));
        } catch (DecisionTableValidationException e) {
            fail("DecisionTableProcessor failed to convert CustomerDiscountRules: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test validation of LoanApprovalRules decision table")
    void testValidateLoanApprovalRules() throws IOException {
        // Create a LoanApprovalRules decision table based on the specification
        Workbook workbook = createLoanApprovalRulesTable();
        
        // Convert to MultipartFile
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        MultipartFile file = new MockMultipartFile(
                "LoanApprovalRules.xlsx",
                "LoanApprovalRules.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );
        
        // Validate using ExcelValidationUtil
        List<String> errors = ExcelValidationUtil.validateExcelFile(file);
        
        // Assert that validation passes (no errors)
        assertTrue(errors.isEmpty(), "LoanApprovalRules validation failed with errors: " + errors);
        
        // Also test that the DecisionTableProcessor can convert it to DRL
        try {
            String drl = processor.convertToDrl(file);
            assertNotNull(drl);
            assertTrue(drl.contains("package LoanApprovalRules"));
            assertTrue(drl.contains("rule \"Excellent Credit Large Income\""));
        } catch (DecisionTableValidationException e) {
            fail("DecisionTableProcessor failed to convert LoanApprovalRules: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test validation of ProductRecommendationRules decision table")
    void testValidateProductRecommendationRules() throws IOException {
        // Create a ProductRecommendationRules decision table based on the specification
        Workbook workbook = createProductRecommendationRulesTable();
        
        // Convert to MultipartFile
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        MultipartFile file = new MockMultipartFile(
                "ProductRecommendationRules.xlsx",
                "ProductRecommendationRules.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );
        
        // Validate using ExcelValidationUtil
        List<String> errors = ExcelValidationUtil.validateExcelFile(file);
        
        // Assert that validation passes (no errors)
        assertTrue(errors.isEmpty(), "ProductRecommendationRules validation failed with errors: " + errors);
        
        // Also test that the DecisionTableProcessor can convert it to DRL
        try {
            String drl = processor.convertToDrl(file);
            assertNotNull(drl);
            assertTrue(drl.contains("package ProductRecommendationRules"));
            assertTrue(drl.contains("rule \"New Young Male Tech\""));
        } catch (DecisionTableValidationException e) {
            fail("DecisionTableProcessor failed to convert ProductRecommendationRules: " + e.getMessage());
        }
    }
    
    /**
     * Creates a CustomerDiscountRules decision table based on the specification.
     */
    private Workbook createCustomerDiscountRulesTable() {
        Workbook workbook = new XSSFWorkbook();
        
        // Create Documentation sheet
        Sheet docSheet = workbook.createSheet("Documentation");
        
        // Add title and description
        Row titleRow = docSheet.createRow(0);
        titleRow.createCell(0).setCellValue("Title");
        titleRow.createCell(1).setCellValue("Customer Discount Rules");
        
        Row descRow = docSheet.createRow(1);
        descRow.createCell(0).setCellValue("Description");
        descRow.createCell(1).setCellValue("This decision table determines discount percentages for customers based on various factors including age, loyalty tier, order amount, and order quantity.");
        
        Row dateRow = docSheet.createRow(2);
        dateRow.createCell(0).setCellValue("Last Updated");
        dateRow.createCell(1).setCellValue("2025-07-22");
        
        Row versionRow = docSheet.createRow(3);
        versionRow.createCell(0).setCellValue("Version");
        versionRow.createCell(1).setCellValue("1.0");
        
        // Create Rules sheet
        Sheet rulesSheet = workbook.createSheet("Rules");
        
        // Add RuleSet header
        Row ruleSetRow = rulesSheet.createRow(0);
        ruleSetRow.createCell(0).setCellValue("RuleSet");
        ruleSetRow.createCell(1).setCellValue("CustomerDiscountRules");
        
        // Add RuleTable header
        Row ruleTableRow = rulesSheet.createRow(2);
        ruleTableRow.createCell(0).setCellValue("RuleTable");
        ruleTableRow.createCell(1).setCellValue("Discount Rules");
        
        // Add column types row
        Row columnTypesRow = rulesSheet.createRow(3);
        columnTypesRow.createCell(0).setCellValue("CONDITION");
        columnTypesRow.createCell(1).setCellValue("CONDITION");
        columnTypesRow.createCell(2).setCellValue("CONDITION");
        columnTypesRow.createCell(3).setCellValue("CONDITION");
        columnTypesRow.createCell(4).setCellValue("ACTION");
        columnTypesRow.createCell(5).setCellValue("ACTION");
        
        // Add column headers row
        Row headerRow = rulesSheet.createRow(4);
        headerRow.createCell(0).setCellValue("Customer Age");
        headerRow.createCell(1).setCellValue("Loyalty Tier");
        headerRow.createCell(2).setCellValue("Order Amount");
        headerRow.createCell(3).setCellValue("Order Quantity");
        headerRow.createCell(4).setCellValue("Discount Percentage");
        headerRow.createCell(5).setCellValue("Applied Rule");
        
        // Add data rows based on the specification
        addCustomerDiscountRule(rulesSheet, 5, "> 60", "== \"GOLD\"", "", "", "20", "\"Premium Customer Discount\"");
        addCustomerDiscountRule(rulesSheet, 6, "", "== \"GOLD\"", "", "", "15", "\"Gold Tier Discount\"");
        addCustomerDiscountRule(rulesSheet, 7, "> 60", "", "", "", "10", "\"Senior Discount\"");
        addCustomerDiscountRule(rulesSheet, 8, "", "== \"SILVER\"", "", "", "10", "\"Silver Tier Discount\"");
        addCustomerDiscountRule(rulesSheet, 9, "", "== \"BRONZE\"", "", "", "5", "\"Bronze Tier Discount\"");
        addCustomerDiscountRule(rulesSheet, 10, "", "", ">= 200", "", "5", "\"Large Order Discount\"");
        addCustomerDiscountRule(rulesSheet, 11, "", "", "", ">= 20", "8", "\"Bulk Order Discount\"");
        addCustomerDiscountRule(rulesSheet, 12, "between(18, 25)", "", "", "", "5", "\"Student Discount\"");
        addCustomerDiscountRule(rulesSheet, 13, "< 18", "", "", "", "5", "\"Child Discount\"");
        addCustomerDiscountRule(rulesSheet, 14, "", "", "between(100, 199.99)", "", "3", "\"Medium Order Discount\"");
        addCustomerDiscountRule(rulesSheet, 15, "", "", "", "between(10, 19)", "5", "\"Medium Quantity Discount\"");
        
        return workbook;
    }
    
    /**
     * Helper method to add a customer discount rule row.
     */
    private void addCustomerDiscountRule(Sheet sheet, int rowIndex, String age, String tier, String amount, String quantity, String discount, String rule) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(age);
        row.createCell(1).setCellValue(tier);
        row.createCell(2).setCellValue(amount);
        row.createCell(3).setCellValue(quantity);
        row.createCell(4).setCellValue(discount);
        row.createCell(5).setCellValue(rule);
    }
    
    /**
     * Creates a LoanApprovalRules decision table based on the specification.
     */
    private Workbook createLoanApprovalRulesTable() {
        Workbook workbook = new XSSFWorkbook();
        
        // Create Documentation sheet
        Sheet docSheet = workbook.createSheet("Documentation");
        
        // Add title and description
        Row titleRow = docSheet.createRow(0);
        titleRow.createCell(0).setCellValue("Title");
        titleRow.createCell(1).setCellValue("Loan Approval Rules");
        
        Row descRow = docSheet.createRow(1);
        descRow.createCell(0).setCellValue("Description");
        descRow.createCell(1).setCellValue("This decision table determines loan approval decisions based on various factors including credit score, income, loan amount, debt-to-income ratio, and employment history.");
        
        Row dateRow = docSheet.createRow(2);
        dateRow.createCell(0).setCellValue("Last Updated");
        dateRow.createCell(1).setCellValue("2025-07-22");
        
        Row versionRow = docSheet.createRow(3);
        versionRow.createCell(0).setCellValue("Version");
        versionRow.createCell(1).setCellValue("1.0");
        
        // Create Rules sheet
        Sheet rulesSheet = workbook.createSheet("Rules");
        
        // Add RuleSet header
        Row ruleSetRow = rulesSheet.createRow(0);
        ruleSetRow.createCell(0).setCellValue("RuleSet");
        ruleSetRow.createCell(1).setCellValue("LoanApprovalRules");
        
        // Add RuleTable header
        Row ruleTableRow = rulesSheet.createRow(2);
        ruleTableRow.createCell(0).setCellValue("RuleTable");
        ruleTableRow.createCell(1).setCellValue("Loan Approval Rules");
        
        // Add column types row
        Row columnTypesRow = rulesSheet.createRow(3);
        columnTypesRow.createCell(0).setCellValue("CONDITION");
        columnTypesRow.createCell(1).setCellValue("CONDITION");
        columnTypesRow.createCell(2).setCellValue("CONDITION");
        columnTypesRow.createCell(3).setCellValue("CONDITION");
        columnTypesRow.createCell(4).setCellValue("CONDITION");
        columnTypesRow.createCell(5).setCellValue("CONDITION");
        columnTypesRow.createCell(6).setCellValue("ACTION");
        columnTypesRow.createCell(7).setCellValue("ACTION");
        columnTypesRow.createCell(8).setCellValue("ACTION");
        
        // Add column headers row
        Row headerRow = rulesSheet.createRow(4);
        headerRow.createCell(0).setCellValue("Credit Score");
        headerRow.createCell(1).setCellValue("Annual Income");
        headerRow.createCell(2).setCellValue("Loan Amount");
        headerRow.createCell(3).setCellValue("Debt-to-Income Ratio");
        headerRow.createCell(4).setCellValue("Employment Years");
        headerRow.createCell(5).setCellValue("Loan Purpose");
        headerRow.createCell(6).setCellValue("Approval Status");
        headerRow.createCell(7).setCellValue("Interest Rate");
        headerRow.createCell(8).setCellValue("Risk Level");
        
        // Add data rows based on the specification
        addLoanApprovalRule(rulesSheet, 5, "> 750", "> 100000", "< 500000", "< 0.36", ">= 2", "", "\"APPROVED\"", "3.25", "\"LOW\"");
        addLoanApprovalRule(rulesSheet, 6, "> 750", "> 75000", "< 300000", "< 0.36", ">= 2", "", "\"APPROVED\"", "3.5", "\"LOW\"");
        addLoanApprovalRule(rulesSheet, 7, "between(700, 750)", "> 80000", "< 300000", "< 0.38", ">= 2", "", "\"APPROVED\"", "3.75", "\"LOW\"");
        addLoanApprovalRule(rulesSheet, 8, "between(680, 750)", "> 60000", "< 250000", "< 0.40", ">= 2", "", "\"APPROVED\"", "4.0", "\"LOW\"");
        addLoanApprovalRule(rulesSheet, 9, "between(620, 680)", "> 60000", "< 200000", "< 0.42", ">= 3", "", "\"APPROVED\"", "4.25", "\"MEDIUM\"");
        addLoanApprovalRule(rulesSheet, 10, "between(620, 680)", "> 50000", "< 200000", "< 0.43", ">= 1", "", "\"REVIEW\"", "4.5", "\"MEDIUM\"");
        addLoanApprovalRule(rulesSheet, 11, "between(600, 620)", "> 60000", "< 150000", "< 0.40", ">= 2", "", "\"REVIEW\"", "4.75", "\"MEDIUM\"");
        addLoanApprovalRule(rulesSheet, 12, "> 680", "> 70000", "< 350000", "< 0.40", ">= 2", "== \"HOME\"", "\"APPROVED\"", "3.85", "\"LOW\"");
        addLoanApprovalRule(rulesSheet, 13, "> 680", "> 60000", "< 50000", "< 0.42", ">= 1", "== \"AUTO\"", "\"APPROVED\"", "3.9", "\"LOW\"");
        addLoanApprovalRule(rulesSheet, 14, "< 600", "", "", "", "", "", "\"REJECTED\"", "", "\"HIGH\"");
        addLoanApprovalRule(rulesSheet, 15, "", "", "", ">= 0.43", "", "", "\"REJECTED\"", "", "\"HIGH\"");
        addLoanApprovalRule(rulesSheet, 16, "", "< 30000", "", "", "", "", "\"REJECTED\"", "", "\"HIGH\"");
        addLoanApprovalRule(rulesSheet, 17, "", "", "", "", "< 1", "", "\"REJECTED\"", "", "\"HIGH\"");
        addLoanApprovalRule(rulesSheet, 18, "", "", ">= 500000", "", "", "", "\"REVIEW\"", "", "\"MEDIUM\"");
        addLoanApprovalRule(rulesSheet, 19, "", "", "", "between(0.40, 0.43)", "", "", "\"REVIEW\"", "", "\"MEDIUM\"");
        
        return workbook;
    }
    
    /**
     * Helper method to add a loan approval rule row.
     */
    private void addLoanApprovalRule(Sheet sheet, int rowIndex, String creditScore, String income, String loanAmount, 
                                    String dti, String employmentYears, String purpose, String status, String rate, String risk) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(creditScore);
        row.createCell(1).setCellValue(income);
        row.createCell(2).setCellValue(loanAmount);
        row.createCell(3).setCellValue(dti);
        row.createCell(4).setCellValue(employmentYears);
        row.createCell(5).setCellValue(purpose);
        row.createCell(6).setCellValue(status);
        row.createCell(7).setCellValue(rate);
        row.createCell(8).setCellValue(risk);
    }
    
    /**
     * Creates a ProductRecommendationRules decision table based on the specification.
     */
    private Workbook createProductRecommendationRulesTable() {
        Workbook workbook = new XSSFWorkbook();
        
        // Create Documentation sheet
        Sheet docSheet = workbook.createSheet("Documentation");
        
        // Add title and description
        Row titleRow = docSheet.createRow(0);
        titleRow.createCell(0).setCellValue("Title");
        titleRow.createCell(1).setCellValue("Product Recommendation Rules");
        
        Row descRow = docSheet.createRow(1);
        descRow.createCell(0).setCellValue("Description");
        descRow.createCell(1).setCellValue("This decision table determines product recommendations based on various factors including customer demographics, purchase history, product attributes, and seasonal considerations.");
        
        Row dateRow = docSheet.createRow(2);
        dateRow.createCell(0).setCellValue("Last Updated");
        dateRow.createCell(1).setCellValue("2025-07-22");
        
        Row versionRow = docSheet.createRow(3);
        versionRow.createCell(0).setCellValue("Version");
        versionRow.createCell(1).setCellValue("1.0");
        
        // Create Rules sheet
        Sheet rulesSheet = workbook.createSheet("Rules");
        
        // Add RuleSet header
        Row ruleSetRow = rulesSheet.createRow(0);
        ruleSetRow.createCell(0).setCellValue("RuleSet");
        ruleSetRow.createCell(1).setCellValue("ProductRecommendationRules");
        
        // Add RuleTable header
        Row ruleTableRow = rulesSheet.createRow(2);
        ruleTableRow.createCell(0).setCellValue("RuleTable");
        ruleTableRow.createCell(1).setCellValue("Product Recommendation Rules");
        
        // Add column types row
        Row columnTypesRow = rulesSheet.createRow(3);
        columnTypesRow.createCell(0).setCellValue("CONDITION");
        columnTypesRow.createCell(1).setCellValue("CONDITION");
        columnTypesRow.createCell(2).setCellValue("CONDITION");
        columnTypesRow.createCell(3).setCellValue("CONDITION");
        columnTypesRow.createCell(4).setCellValue("CONDITION");
        columnTypesRow.createCell(5).setCellValue("CONDITION");
        columnTypesRow.createCell(6).setCellValue("CONDITION");
        columnTypesRow.createCell(7).setCellValue("ACTION");
        columnTypesRow.createCell(8).setCellValue("ACTION");
        columnTypesRow.createCell(9).setCellValue("ACTION");
        columnTypesRow.createCell(10).setCellValue("ACTION");
        columnTypesRow.createCell(11).setCellValue("ACTION");
        
        // Add column headers row
        Row headerRow = rulesSheet.createRow(4);
        headerRow.createCell(0).setCellValue("Customer Age");
        headerRow.createCell(1).setCellValue("Customer Gender");
        headerRow.createCell(2).setCellValue("Customer Category");
        headerRow.createCell(3).setCellValue("Previous Purchases");
        headerRow.createCell(4).setCellValue("Last Purchase Category");
        headerRow.createCell(5).setCellValue("Season");
        headerRow.createCell(6).setCellValue("Inventory Level");
        headerRow.createCell(7).setCellValue("Recommended Category");
        headerRow.createCell(8).setCellValue("Recommended Product");
        headerRow.createCell(9).setCellValue("Discount Offer");
        headerRow.createCell(10).setCellValue("Recommendation Priority");
        headerRow.createCell(11).setCellValue("Recommendation Reason");
        
        // Add data rows based on the specification
        addProductRecommendationRule(rulesSheet, 5, "between(18, 30)", "== \"M\"", "== \"NEW\"", "", "", "", "", 
                "\"ELECTRONICS\"", "\"Gaming Console\"", "true", "100", "\"Welcome offer for new young male customers\"");
        addProductRecommendationRule(rulesSheet, 6, "between(18, 30)", "== \"F\"", "== \"NEW\"", "", "", "", "", 
                "\"ELECTRONICS\"", "\"Smartphone\"", "true", "100", "\"Welcome offer for new young female customers\"");
        addProductRecommendationRule(rulesSheet, 7, "", "", "== \"NEW\"", "", "", "", "", 
                "\"ELECTRONICS\"", "\"Smart Speaker\"", "true", "90", "\"Welcome offer for new customers\"");
        addProductRecommendationRule(rulesSheet, 8, "", "", "== \"RETURNING\"", "> 0", "== \"ELECTRONICS\"", "", "", 
                "\"ELECTRONICS\"", "\"Laptop\"", "false", "85", "\"Based on purchase history\"");
        addProductRecommendationRule(rulesSheet, 9, "", "", "== \"RETURNING\"", "> 0", "== \"CLOTHING\"", "", "", 
                "\"CLOTHING\"", "\"Premium Collection\"", "false", "85", "\"Based on purchase history\"");
        addProductRecommendationRule(rulesSheet, 10, "", "", "== \"RETURNING\"", "> 0", "== \"HOME_GOODS\"", "", "", 
                "\"HOME_GOODS\"", "\"Kitchen Appliances\"", "false", "85", "\"Based on purchase history\"");
        addProductRecommendationRule(rulesSheet, 11, "", "", "", "> 10", "", "", "", 
                "\"PREMIUM\"", "\"Loyalty Rewards\"", "true", "80", "\"Reward for frequent customers\"");
        addProductRecommendationRule(rulesSheet, 12, "", "== \"M\"", "", "", "", "== \"SUMMER\"", "> 50", 
                "\"CLOTHING\"", "\"Men's Summer Collection\"", "false", "75", "\"Seasonal recommendation\"");
        addProductRecommendationRule(rulesSheet, 13, "", "== \"F\"", "", "", "", "== \"SUMMER\"", "> 50", 
                "\"CLOTHING\"", "\"Women's Summer Collection\"", "false", "75", "\"Seasonal recommendation\"");
        addProductRecommendationRule(rulesSheet, 14, "", "== \"M\"", "", "", "", "== \"WINTER\"", "> 50", 
                "\"CLOTHING\"", "\"Men's Winter Collection\"", "false", "75", "\"Seasonal recommendation\"");
        addProductRecommendationRule(rulesSheet, 15, "", "== \"F\"", "", "", "", "== \"WINTER\"", "> 50", 
                "\"CLOTHING\"", "\"Women's Winter Collection\"", "false", "75", "\"Seasonal recommendation\"");
        addProductRecommendationRule(rulesSheet, 16, "between(18, 25)", "", "", "", "", "", "", 
                "\"TRENDING\"", "\"Trending Items\"", "false", "70", "\"Age-based recommendation\"");
        addProductRecommendationRule(rulesSheet, 17, "> 65", "", "", "", "", "", "", 
                "\"SPECIAL_OFFERS\"", "\"Senior Discount Items\"", "true", "70", "\"Age-based recommendation\"");
        addProductRecommendationRule(rulesSheet, 18, "", "", "", "", "", "", "> 200", 
                "\"CLEARANCE\"", "\"Discounted Items\"", "true", "65", "\"Inventory reduction\"");
        addProductRecommendationRule(rulesSheet, 19, "", "", "", "", "", "", "< 20", 
                "\"PREMIUM\"", "\"Limited Stock Items\"", "false", "60", "\"Scarcity-based recommendation\"");
        
        return workbook;
    }
    
    /**
     * Helper method to add a product recommendation rule row.
     */
    private void addProductRecommendationRule(Sheet sheet, int rowIndex, String age, String gender, String category, 
                                             String purchases, String lastPurchase, String season, String inventory,
                                             String recCategory, String recProduct, String discount, String priority, String reason) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(age);
        row.createCell(1).setCellValue(gender);
        row.createCell(2).setCellValue(category);
        row.createCell(3).setCellValue(purchases);
        row.createCell(4).setCellValue(lastPurchase);
        row.createCell(5).setCellValue(season);
        row.createCell(6).setCellValue(inventory);
        row.createCell(7).setCellValue(recCategory);
        row.createCell(8).setCellValue(recProduct);
        row.createCell(9).setCellValue(discount);
        row.createCell(10).setCellValue(priority);
        row.createCell(11).setCellValue(reason);
    }
}