package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.dto.LoanApprovalResponse;
import com.example.springdroolsintegration.model.request.LoanApprovalRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional integration tests for LoanApprovalService focusing on:
 * 1. Credit score boundaries
 * 2. Income boundaries
 * 3. Debt-to-income ratio boundaries
 * 4. Employment stability boundaries
 * 5. Loan amount boundaries
 * 6. Special loan types
 * 7. Combinations of multiple factors
 * 8. Error scenarios
 */
@SpringBootTest
@ActiveProfiles("test")
public class LoanApprovalServiceAdditionalIntegrationTest {

    @Autowired
    private LoanApprovalService loanApprovalService;

    @ParameterizedTest
    @DisplayName("Test loan approval at credit score boundaries")
    @CsvSource({
        "751, APPROVED, LOW", // Excellent credit (> 750)
        "750, APPROVED, LOW", // Excellent credit (= 750)
        "700, APPROVED, LOW", // Good credit (700-750)
        "680, APPROVED, LOW", // Good credit (= 680)
        "650, REVIEW, MEDIUM", // Fair credit (620-680)
        "620, REVIEW, MEDIUM", // Fair credit (= 620)
        "610, REVIEW, MEDIUM", // Borderline credit (600-620)
        "600, REVIEW, MEDIUM", // Borderline credit (= 600)
        "599, REJECTED, HIGH" // Poor credit (< 600)
    })
    void testCreditScoreBoundaries(int creditScore, String expectedStatus, String expectedRiskLevel) {
        // Arrange
        LoanApprovalRequest request = createBasicLoanRequest();
        request.setCreditScore(creditScore);

        // Act
        LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedRiskLevel, response.getRiskCategory());
    }

    @ParameterizedTest
    @DisplayName("Test loan approval at income boundaries")
    @CsvSource({
        "120000, APPROVED, LOW", // Very high income (> $100,000)
        "100000, APPROVED, LOW", // Very high income (= $100,000)
        "85000, APPROVED, LOW", // High income (> $80,000)
        "80000, APPROVED, LOW", // High income (= $80,000)
        "60000, APPROVED, LOW", // Moderate income ($50,000-$75,000)
        "50000, REVIEW, MEDIUM", // Moderate income (= $50,000)
        "40000, REVIEW, MEDIUM", // Between moderate and low
        "30000, REVIEW, MEDIUM", // Low income (= $30,000)
        "29999, REJECTED, HIGH" // Low income (< $30,000)
    })
    void testIncomeBoundaries(double annualIncome, String expectedStatus, String expectedRiskLevel) {
        // Arrange
        LoanApprovalRequest request = createBasicLoanRequest();
        request.setAnnualIncome(annualIncome);

        // Act
        LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedRiskLevel, response.getRiskCategory());
    }

    @Test
    @DisplayName("Test loan approval for debt-to-income ratio boundaries")
    void testDebtToIncomeBoundaries() {
        // Arrange - Low DTI (< 0.36)
        LoanApprovalRequest lowDtiRequest = createBasicLoanRequest();
        lowDtiRequest.setAnnualIncome(100000); // $8,333/month
        lowDtiRequest.setMonthlyDebtPayments(2500); // DTI = 0.30

        // Arrange - Moderate DTI (0.36-0.40)
        LoanApprovalRequest moderateDtiRequest = createBasicLoanRequest();
        moderateDtiRequest.setAnnualIncome(100000); // $8,333/month
        moderateDtiRequest.setMonthlyDebtPayments(3200); // DTI = 0.38

        // Arrange - High DTI (0.40-0.43)
        LoanApprovalRequest highDtiRequest = createBasicLoanRequest();
        highDtiRequest.setAnnualIncome(100000); // $8,333/month
        highDtiRequest.setMonthlyDebtPayments(3500); // DTI = 0.42

        // Arrange - Excessive DTI (>= 0.43)
        LoanApprovalRequest excessiveDtiRequest = createBasicLoanRequest();
        excessiveDtiRequest.setAnnualIncome(100000); // $8,333/month
        excessiveDtiRequest.setMonthlyDebtPayments(3600); // DTI = 0.43

        // Act
        LoanApprovalResponse lowDtiResponse = loanApprovalService.evaluateLoanApplication(lowDtiRequest);
        LoanApprovalResponse moderateDtiResponse = loanApprovalService.evaluateLoanApplication(moderateDtiRequest);
        LoanApprovalResponse highDtiResponse = loanApprovalService.evaluateLoanApplication(highDtiRequest);
        LoanApprovalResponse excessiveDtiResponse = loanApprovalService.evaluateLoanApplication(excessiveDtiRequest);

        // Assert
        assertNotNull(lowDtiResponse);
        assertEquals("APPROVED", lowDtiResponse.getStatus());
        assertEquals("LOW", lowDtiResponse.getRiskCategory());

        assertNotNull(moderateDtiResponse);
        assertEquals("APPROVED", moderateDtiResponse.getStatus());
        assertEquals("LOW", moderateDtiResponse.getRiskCategory());

        assertNotNull(highDtiResponse);
        assertEquals("REVIEW", highDtiResponse.getStatus());
        assertEquals("MEDIUM", highDtiResponse.getRiskCategory());

        assertNotNull(excessiveDtiResponse);
        assertEquals("REJECTED", excessiveDtiResponse.getStatus());
        assertEquals("HIGH", excessiveDtiResponse.getRiskCategory());
    }

    @ParameterizedTest
    @DisplayName("Test loan approval at employment stability boundaries")
    @CsvSource({
        "3.5, APPROVED, LOW", // Very stable employment (>= 3 years)
        "3.0, APPROVED, LOW", // Very stable employment (= 3 years)
        "2.5, APPROVED, LOW", // Stable employment (>= 2 years)
        "2.0, APPROVED, LOW", // Stable employment (= 2 years)
        "1.5, REVIEW, MEDIUM", // Between stable and unstable
        "1.0, REVIEW, MEDIUM", // Borderline stable (= 1 year)
        "0.9, REJECTED, HIGH" // Unstable employment (< 1 year)
    })
    void testEmploymentStabilityBoundaries(double yearsAtEmployer, String expectedStatus, String expectedRiskLevel) {
        // Arrange
        LoanApprovalRequest request = createBasicLoanRequest();
        request.setYearsAtEmployer(yearsAtEmployer);

        // Act
        LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedRiskLevel, response.getRiskCategory());
    }

    @ParameterizedTest
    @DisplayName("Test loan approval at loan amount boundaries")
    @CsvSource({
        "40000, APPROVED, LOW", // Small loan (< $50,000)
        "50000, APPROVED, LOW", // Small loan (= $50,000)
        "150000, APPROVED, LOW", // Medium loan ($50,000-$200,000)
        "200000, APPROVED, LOW", // Medium loan (= $200,000)
        "350000, APPROVED, LOW", // Large loan ($200,000-$500,000)
        "500000, REVIEW, MEDIUM", // Large loan (= $500,000)
        "600000, REVIEW, MEDIUM" // Jumbo loan (> $500,000)
    })
    void testLoanAmountBoundaries(double loanAmount, String expectedStatus, String expectedRiskLevel) {
        // Arrange
        LoanApprovalRequest request = createBasicLoanRequest();
        request.setLoanAmount(loanAmount);

        // Act
        LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatus());
        assertEquals(expectedRiskLevel, response.getRiskCategory());
    }

    @Test
    @DisplayName("Test special loan types")
    void testSpecialLoanTypes() {
        // Arrange - Home loan
        LoanApprovalRequest homeLoanRequest = createBasicLoanRequest();
        homeLoanRequest.setCreditScore(700);
        homeLoanRequest.setAnnualIncome(75000);
        homeLoanRequest.setLoanAmount(300000);
        homeLoanRequest.setLoanPurpose("HOME");

        // Arrange - Auto loan
        LoanApprovalRequest autoLoanRequest = createBasicLoanRequest();
        autoLoanRequest.setCreditScore(700);
        autoLoanRequest.setAnnualIncome(65000);
        autoLoanRequest.setLoanAmount(40000);
        autoLoanRequest.setLoanPurpose("AUTO");

        // Act
        LoanApprovalResponse homeLoanResponse = loanApprovalService.evaluateLoanApplication(homeLoanRequest);
        LoanApprovalResponse autoLoanResponse = loanApprovalService.evaluateLoanApplication(autoLoanRequest);

        // Assert
        assertNotNull(homeLoanResponse);
        assertEquals("APPROVED", homeLoanResponse.getStatus());
        assertEquals("LOW", homeLoanResponse.getRiskCategory());
        assertEquals(3.85, homeLoanResponse.getInterestRate());

        assertNotNull(autoLoanResponse);
        assertEquals("APPROVED", autoLoanResponse.getStatus());
        assertEquals("LOW", autoLoanResponse.getRiskCategory());
        assertEquals(3.9, autoLoanResponse.getInterestRate());
    }

    @Test
    @DisplayName("Test combinations of multiple factors")
    void testCombinationsOfMultipleFactors() {
        // Arrange - Excellent credit but low income
        LoanApprovalRequest excellentCreditLowIncomeRequest = createBasicLoanRequest();
        excellentCreditLowIncomeRequest.setCreditScore(780);
        excellentCreditLowIncomeRequest.setAnnualIncome(29000);

        // Arrange - Poor credit but high income
        LoanApprovalRequest poorCreditHighIncomeRequest = createBasicLoanRequest();
        poorCreditHighIncomeRequest.setCreditScore(580);
        poorCreditHighIncomeRequest.setAnnualIncome(150000);

        // Arrange - Good credit but unstable employment
        LoanApprovalRequest goodCreditUnstableEmploymentRequest = createBasicLoanRequest();
        goodCreditUnstableEmploymentRequest.setCreditScore(720);
        goodCreditUnstableEmploymentRequest.setYearsAtEmployer(0.5);

        // Arrange - Fair credit but jumbo loan
        LoanApprovalRequest fairCreditJumboLoanRequest = createBasicLoanRequest();
        fairCreditJumboLoanRequest.setCreditScore(650);
        fairCreditJumboLoanRequest.setLoanAmount(600000);

        // Act
        LoanApprovalResponse excellentCreditLowIncomeResponse = loanApprovalService.evaluateLoanApplication(excellentCreditLowIncomeRequest);
        LoanApprovalResponse poorCreditHighIncomeResponse = loanApprovalService.evaluateLoanApplication(poorCreditHighIncomeRequest);
        LoanApprovalResponse goodCreditUnstableEmploymentResponse = loanApprovalService.evaluateLoanApplication(goodCreditUnstableEmploymentRequest);
        LoanApprovalResponse fairCreditJumboLoanResponse = loanApprovalService.evaluateLoanApplication(fairCreditJumboLoanRequest);

        // Assert
        assertNotNull(excellentCreditLowIncomeResponse);
        assertEquals("REJECTED", excellentCreditLowIncomeResponse.getStatus()); // Low income rule has higher priority

        assertNotNull(poorCreditHighIncomeResponse);
        assertEquals("REJECTED", poorCreditHighIncomeResponse.getStatus()); // Poor credit rule has higher priority

        assertNotNull(goodCreditUnstableEmploymentResponse);
        assertEquals("REJECTED", goodCreditUnstableEmploymentResponse.getStatus()); // Unstable employment rule has higher priority

        assertNotNull(fairCreditJumboLoanResponse);
        assertEquals("REVIEW", fairCreditJumboLoanResponse.getStatus()); // Jumbo loan requires review
    }

    @Test
    @DisplayName("Test error scenarios")
    void testErrorScenarios() {
        // Test null request
        Exception nullRequestException = assertThrows(IllegalArgumentException.class, () -> {
            loanApprovalService.evaluateLoanApplication(null);
        });
        assertTrue(nullRequestException.getMessage().contains("Request cannot be null"));

        // Test negative credit score
        LoanApprovalRequest negativeCreditScoreRequest = createBasicLoanRequest();
        negativeCreditScoreRequest.setCreditScore(-100);
        Exception negativeCreditScoreException = assertThrows(IllegalArgumentException.class, () -> {
            loanApprovalService.evaluateLoanApplication(negativeCreditScoreRequest);
        });
        assertTrue(negativeCreditScoreException.getMessage().contains("Credit score must be positive"));

        // Test negative loan amount
        LoanApprovalRequest negativeLoanAmountRequest = createBasicLoanRequest();
        negativeLoanAmountRequest.setLoanAmount(-50000);
        Exception negativeLoanAmountException = assertThrows(IllegalArgumentException.class, () -> {
            loanApprovalService.evaluateLoanApplication(negativeLoanAmountRequest);
        });
        assertTrue(negativeLoanAmountException.getMessage().contains("Loan amount must be positive"));

        // Test negative annual income
        LoanApprovalRequest negativeAnnualIncomeRequest = createBasicLoanRequest();
        negativeAnnualIncomeRequest.setAnnualIncome(-75000);
        Exception negativeAnnualIncomeException = assertThrows(IllegalArgumentException.class, () -> {
            loanApprovalService.evaluateLoanApplication(negativeAnnualIncomeRequest);
        });
        assertTrue(negativeAnnualIncomeException.getMessage().contains("Annual income must be positive"));
    }

    /**
     * Helper method to create a basic loan request with default values
     */
    private LoanApprovalRequest createBasicLoanRequest() {
        LoanApprovalRequest request = new LoanApprovalRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1980, 1, 1));
        request.setSsn("123-45-6789");
        request.setAnnualIncome(75000);
        request.setCreditScore(720);
        request.setLoanType("PERSONAL");
        request.setLoanPurpose("GENERAL");
        request.setLoanAmount(150000);
        request.setLoanTermMonths(360);
        request.setYearsAtEmployer(3.0);
        request.setMonthlyDebtPayments(1500);
        return request;
    }
}