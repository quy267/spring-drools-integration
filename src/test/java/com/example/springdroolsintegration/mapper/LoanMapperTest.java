package com.example.springdroolsintegration.mapper;

import com.example.springdroolsintegration.model.dto.LoanApprovalResponse;
import com.example.springdroolsintegration.model.entity.Applicant;
import com.example.springdroolsintegration.model.entity.CreditScore;
import com.example.springdroolsintegration.model.entity.LoanApplication;
import com.example.springdroolsintegration.model.request.LoanApprovalRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for LoanMapper.
 * Tests all mapping scenarios and edge cases for loan approval mappings.
 */
@DisplayName("LoanMapper Tests")
class LoanMapperTest {

    private LoanMapper loanMapper;

    @BeforeEach
    void setUp() {
        loanMapper = Mappers.getMapper(LoanMapper.class);
    }

    @Test
    @DisplayName("Should map LoanApprovalRequest to Applicant correctly")
    void shouldMapRequestToApplicant() {
        // Given
        LoanApprovalRequest request = createSampleLoanRequest();

        // When
        Applicant applicant = loanMapper.requestToApplicant(request);

        // Then
        assertThat(applicant).isNotNull();
        assertThat(applicant.getId()).isNull(); // Should be ignored
        assertThat(applicant.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(applicant.getLastName()).isEqualTo(request.getLastName());
        assertThat(applicant.getDateOfBirth()).isEqualTo(request.getDateOfBirth());
        assertThat(applicant.getSsn()).isEqualTo(request.getSsn());
        assertThat(applicant.getEmail()).isEqualTo(request.getEmail());
        assertThat(applicant.getPhoneNumber()).isEqualTo(request.getPhoneNumber());
        assertThat(applicant.getAddress()).isEqualTo(request.getAddress());
        assertThat(applicant.getEmploymentStatus()).isEqualTo(request.getEmploymentStatus());
        assertThat(applicant.getEmployerName()).isEqualTo(request.getEmployerName());
        assertThat(applicant.getYearsAtEmployer()).isEqualTo(request.getYearsAtEmployer());
        assertThat(applicant.getAnnualIncome()).isEqualTo(request.getAnnualIncome());
        assertThat(applicant.getMonthlyIncome()).isEqualTo(request.getAnnualIncome() / 12);
        assertThat(applicant.getMonthlyDebtPayments()).isEqualTo(request.getMonthlyDebtPayments());
        assertThat(applicant.getCreditScore()).isEqualTo(request.getCreditScore());
        assertThat(applicant.getCreditInquiries()).isEqualTo(request.getCreditInquiries());
        assertThat(applicant.isBankruptcyHistory()).isEqualTo(request.isHasBankruptcy());
        assertThat(applicant.isForeclosureHistory()).isEqualTo(request.isHasForeclosure());
        
        // Verify ignored properties
        assertThat(applicant.getCity()).isNull();
        assertThat(applicant.getState()).isNull();
        assertThat(applicant.getZipCode()).isNull();
        assertThat(applicant.getCountry()).isNull();
        assertThat(applicant.getJobTitle()).isNull();
        assertThat(applicant.isFirstTimeHomeBuyer()).isFalse();
        assertThat(applicant.isUsCitizen()).isFalse();
    }

    @Test
    @DisplayName("Should map LoanApprovalRequest to CoApplicant correctly")
    void shouldMapRequestToCoApplicant() {
        // Given
        LoanApprovalRequest request = createSampleLoanRequestWithCoApplicant();

        // When
        Applicant coApplicant = loanMapper.requestToCoApplicant(request);

        // Then
        assertThat(coApplicant).isNotNull();
        assertThat(coApplicant.getId()).isNull(); // Should be ignored
        assertThat(coApplicant.getFirstName()).isEqualTo(request.getCoApplicantFirstName());
        assertThat(coApplicant.getLastName()).isEqualTo(request.getCoApplicantLastName());
        assertThat(coApplicant.getDateOfBirth()).isEqualTo(request.getCoApplicantDateOfBirth());
        assertThat(coApplicant.getSsn()).isEqualTo(request.getCoApplicantSsn());
        assertThat(coApplicant.getEmploymentStatus()).isEqualTo(request.getCoApplicantEmploymentStatus());
        assertThat(coApplicant.getAnnualIncome()).isEqualTo(request.getCoApplicantAnnualIncome());
        assertThat(coApplicant.getMonthlyIncome()).isEqualTo(request.getCoApplicantAnnualIncome() / 12);
        assertThat(coApplicant.getMonthlyDebtPayments()).isEqualTo(request.getCoApplicantMonthlyDebtPayments());
        assertThat(coApplicant.getCreditScore()).isEqualTo(request.getCoApplicantCreditScore());
        
        // Verify ignored properties
        assertThat(coApplicant.getEmail()).isNull();
        assertThat(coApplicant.getPhoneNumber()).isNull();
        assertThat(coApplicant.getAddress()).isNull();
        assertThat(coApplicant.getCity()).isNull();
        assertThat(coApplicant.getState()).isNull();
        assertThat(coApplicant.getZipCode()).isNull();
        assertThat(coApplicant.getCountry()).isNull();
        assertThat(coApplicant.getJobTitle()).isNull();
    }

    @Test
    @DisplayName("Should map LoanApprovalRequest to CreditScore correctly")
    void shouldMapRequestToCreditScore() {
        // Given
        LoanApprovalRequest request = createSampleLoanRequest();
        Applicant applicant = createSampleApplicant();

        // When
        CreditScore creditScore = loanMapper.requestToCreditScore(request, applicant);

        // Then
        assertThat(creditScore).isNotNull();
        assertThat(creditScore.getId()).isNull(); // Should be ignored
        assertThat(creditScore.getApplicant()).isEqualTo(applicant);
        assertThat(creditScore.getScore()).isEqualTo(request.getCreditScore());
        assertThat(creditScore.getBureau()).isEqualTo(request.getCreditBureau());
        assertThat(creditScore.getReportDate()).isNotNull();
        assertThat(creditScore.getScoreModel()).isEqualTo("FICO 8");
        assertThat(creditScore.getHardInquiries()).isEqualTo(request.getCreditInquiries());
        assertThat(creditScore.isHasBankruptcy()).isEqualTo(request.isHasBankruptcy());
        assertThat(creditScore.isHasForeclosure()).isEqualTo(request.isHasForeclosure());
        
        // Verify constant values
        assertThat(creditScore.getOpenAccounts()).isEqualTo(0);
        assertThat(creditScore.getTotalCreditLimit()).isEqualTo(0);
        assertThat(creditScore.getTotalBalance()).isEqualTo(0);
        assertThat(creditScore.getCreditUtilization()).isEqualTo(0);
        assertThat(creditScore.isHasTaxLiens()).isFalse();
        assertThat(creditScore.isHasJudgments()).isFalse();
    }

    @Test
    @DisplayName("Should map LoanApprovalRequest to LoanApplication correctly")
    void shouldMapRequestToLoanApplication() {
        // Given
        LoanApprovalRequest request = createSampleLoanRequest();
        Applicant applicant = createSampleApplicant();
        Applicant coApplicant = null;

        // When
        LoanApplication loanApplication = loanMapper.requestToLoanApplication(request, applicant, coApplicant);

        // Then
        assertThat(loanApplication).isNotNull();
        assertThat(loanApplication.getId()).isNull(); // Should be ignored
        assertThat(loanApplication.getApplicationNumber()).isNotNull();
        assertThat(loanApplication.getApplicant()).isEqualTo(applicant);
        assertThat(loanApplication.getCoApplicant()).isNull();
        assertThat(loanApplication.getLoanAmount()).isEqualTo(request.getLoanAmount());
        assertThat(loanApplication.getLoanTermMonths()).isEqualTo(request.getLoanTermMonths());
        assertThat(loanApplication.getInterestRate()).isEqualTo(request.getInterestRate());
        assertThat(loanApplication.getLoanType()).isEqualTo(request.getLoanType());
        assertThat(loanApplication.getLoanPurpose()).isEqualTo(request.getLoanPurpose());
        assertThat(loanApplication.getPropertyValue()).isEqualTo(request.getPropertyValue());
        assertThat(loanApplication.getDownPayment()).isEqualTo(request.getDownPayment());
        assertThat(loanApplication.getApplicationDate()).isNotNull();
    }

    @Test
    @DisplayName("Should map LoanApplication to LoanApprovalResponse correctly")
    void shouldMapLoanApplicationToResponse() {
        // Given
        LoanApplication loanApplication = createSampleLoanApplication();

        // When
        LoanApprovalResponse response = loanMapper.loanApplicationToResponse(loanApplication);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getApplicationNumber()).isEqualTo(loanApplication.getApplicationNumber());
        assertThat(response.getApplicantName()).isEqualTo(loanApplication.getApplicant().getFirstName() + " " + loanApplication.getApplicant().getLastName());
        assertThat(response.getLoanAmount()).isEqualTo(loanApplication.getLoanAmount());
        assertThat(response.getLoanTermMonths()).isEqualTo(loanApplication.getLoanTermMonths());
        assertThat(response.getInterestRate()).isEqualTo(loanApplication.getInterestRate());
        assertThat(response.getStatus()).isEqualTo(loanApplication.getStatus());
        assertThat(response.getDecisionDate()).isEqualTo(loanApplication.getDecisionDate());
        assertThat(response.getDecisionReason()).isEqualTo(loanApplication.getDecisionReason());
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void shouldHandleNullRequest() {
        // When & Then
        Applicant applicant = loanMapper.requestToApplicant(null);
        assertThat(applicant).isNull();
    }

    @Test
    @DisplayName("Should update Applicant correctly")
    void shouldUpdateApplicant() {
        // Given
        LoanApprovalRequest request = createSampleLoanRequest();
        Applicant existingApplicant = createSampleApplicant();

        // When
        Applicant updatedApplicant = loanMapper.updateApplicant(request, existingApplicant);

        // Then
        assertThat(updatedApplicant).isNotNull();
        assertThat(updatedApplicant.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(updatedApplicant.getLastName()).isEqualTo(request.getLastName());
        assertThat(updatedApplicant.getEmail()).isEqualTo(request.getEmail());
        assertThat(updatedApplicant.getAnnualIncome()).isEqualTo(request.getAnnualIncome());
    }

    @Test
    @DisplayName("Should generate application number")
    void shouldGenerateApplicationNumber() {
        // When
        String applicationNumber = loanMapper.generateApplicationNumber();

        // Then
        assertThat(applicationNumber).isNotNull();
        assertThat(applicationNumber).startsWith("LA");
        assertThat(applicationNumber).hasSize(12); // LA + 10 digits
    }

    @Test
    @DisplayName("Should calculate LTV correctly")
    void shouldCalculateLTV() {
        // Given
        LoanApprovalRequest request = createSampleLoanRequest();

        // When
        double ltv = loanMapper.calculateLTV(request);

        // Then
        double expectedLTV = (request.getLoanAmount() / request.getPropertyValue()) * 100;
        assertThat(ltv).isEqualTo(expectedLTV);
    }

    @Test
    @DisplayName("Should calculate monthly payment correctly")
    void shouldCalculateMonthlyPayment() {
        // Given
        LoanApprovalRequest request = createSampleLoanRequest();

        // When
        double monthlyPayment = loanMapper.calculateMonthlyPayment(request);

        // Then
        assertThat(monthlyPayment).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should calculate DTI correctly")
    void shouldCalculateDTI() {
        // Given
        LoanApprovalRequest request = createSampleLoanRequest();

        // When
        double dti = loanMapper.calculateDTI(request);

        // Then
        double expectedDTI = (request.getMonthlyDebtPayments() / (request.getAnnualIncome() / 12)) * 100;
        assertThat(dti).isEqualTo(expectedDTI);
    }

    private LoanApprovalRequest createSampleLoanRequest() {
        LoanApprovalRequest request = new LoanApprovalRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1985, 5, 15));
        request.setSsn("123-45-6789");
        request.setEmail("john.doe@example.com");
        request.setPhoneNumber("555-123-4567");
        request.setAddress("123 Main St");
        request.setEmploymentStatus("EMPLOYED");
        request.setEmployerName("Tech Corp");
        request.setYearsAtEmployer(5);
        request.setAnnualIncome(75000.0);
        request.setMonthlyDebtPayments(1500.0);
        request.setCreditScore(720);
        request.setCreditBureau("EXPERIAN");
        request.setCreditInquiries(2);
        request.setHasBankruptcy(false);
        request.setHasForeclosure(false);
        request.setLoanAmount(300000.0);
        request.setLoanTermMonths(30);
        request.setInterestRate(3.5);
        request.setLoanType("CONVENTIONAL");
        request.setLoanPurpose("PURCHASE");
        request.setPropertyValue(400000.0);
        request.setDownPayment(100000.0);
        return request;
    }

    private LoanApprovalRequest createSampleLoanRequestWithCoApplicant() {
        LoanApprovalRequest request = createSampleLoanRequest();
        request.setCoApplicantFirstName("Jane");
        request.setCoApplicantLastName("Doe");
        request.setCoApplicantDateOfBirth(LocalDate.of(1987, 8, 20));
        request.setCoApplicantSsn("987-65-4321");
        request.setCoApplicantEmploymentStatus("EMPLOYED");
        request.setCoApplicantAnnualIncome(65000.0);
        request.setCoApplicantMonthlyDebtPayments(800.0);
        request.setCoApplicantCreditScore(740);
        return request;
    }

    private Applicant createSampleApplicant() {
        Applicant applicant = new Applicant();
        applicant.setId(1L);
        applicant.setFirstName("John");
        applicant.setLastName("Doe");
        applicant.setEmail("john.doe@example.com");
        applicant.setAnnualIncome(75000.0);
        applicant.setCreditScore(720);
        return applicant;
    }

    private LoanApplication createSampleLoanApplication() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(1L);
        loanApplication.setApplicationNumber("LA1234567890");
        loanApplication.setApplicant(createSampleApplicant());
        loanApplication.setLoanAmount(300000.0);
        loanApplication.setLoanTermMonths(30);
        loanApplication.setInterestRate(3.5);
        loanApplication.setStatus("APPROVED");
        loanApplication.setDecisionDate(LocalDate.now());
        loanApplication.setDecisionReason("Meets all criteria");
        return loanApplication;
    }
}