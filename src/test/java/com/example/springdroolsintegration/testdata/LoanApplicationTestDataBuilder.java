package com.example.springdroolsintegration.testdata;

import com.example.springdroolsintegration.model.entity.Applicant;
import com.example.springdroolsintegration.model.entity.LoanApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test data builder for LoanApplication entity following the builder pattern.
 * Provides fluent API for creating LoanApplication instances with default values for testing.
 */
public class LoanApplicationTestDataBuilder {
    
    private LoanApplication loanApplication;
    
    private LoanApplicationTestDataBuilder() {
        this.loanApplication = new LoanApplication();
        // Set default values for testing
        this.loanApplication.setId(1L);
        this.loanApplication.setApplicationNumber("LA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        this.loanApplication.setApplicant(ApplicantTestDataBuilder.anApplicant().build());
        this.loanApplication.setLoanType("MORTGAGE");
        this.loanApplication.setLoanPurpose("HOME_PURCHASE");
        this.loanApplication.setLoanAmount(250000.0);
        this.loanApplication.setLoanTermMonths(360);
        this.loanApplication.setInterestRate(4.5);
        this.loanApplication.setApplicationDate(LocalDate.now());
        this.loanApplication.setLastUpdated(LocalDateTime.now());
        this.loanApplication.setStatus("PENDING");
        this.loanApplication.setNotes("Standard loan application");
        this.loanApplication.setPropertyAddress("456 Oak Street, Anytown, CA 12345");
        this.loanApplication.setPropertyValue(300000.0);
        this.loanApplication.setLoanToValueRatio(83.33);
        this.loanApplication.setDownPayment(50000.0);
        this.loanApplication.setMonthlyPayment(1266.71);
        this.loanApplication.setDebtToIncomeRatio(24.0);
        this.loanApplication.setRiskScore(650);
        this.loanApplication.setApproved(false);
    }
    
    /**
     * Creates a new LoanApplicationTestDataBuilder instance with default values.
     * @return new LoanApplicationTestDataBuilder instance
     */
    public static LoanApplicationTestDataBuilder aLoanApplication() {
        return new LoanApplicationTestDataBuilder();
    }
    
    /**
     * Creates a LoanApplicationTestDataBuilder for a mortgage loan.
     * @return LoanApplicationTestDataBuilder with mortgage defaults
     */
    public static LoanApplicationTestDataBuilder aMortgageLoan() {
        return new LoanApplicationTestDataBuilder()
                .withLoanType("MORTGAGE")
                .withLoanPurpose("HOME_PURCHASE")
                .withLoanAmount(350000.0)
                .withLoanTermMonths(360)
                .withInterestRate(4.25)
                .withPropertyValue(400000.0)
                .withDownPayment(50000.0)
                .withApplicant(ApplicantTestDataBuilder.aGoodCreditApplicant().build());
    }
    
    /**
     * Creates a LoanApplicationTestDataBuilder for an auto loan.
     * @return LoanApplicationTestDataBuilder with auto loan defaults
     */
    public static LoanApplicationTestDataBuilder anAutoLoan() {
        return new LoanApplicationTestDataBuilder()
                .withLoanType("AUTO")
                .withLoanPurpose("VEHICLE_PURCHASE")
                .withLoanAmount(35000.0)
                .withLoanTermMonths(60)
                .withInterestRate(5.5)
                .withVehicleMake("Toyota")
                .withVehicleModel("Camry")
                .withVehicleYear(2023)
                .withVehicleValue(38000.0)
                .withApplicant(ApplicantTestDataBuilder.aFairCreditApplicant().build());
    }
    
    /**
     * Creates a LoanApplicationTestDataBuilder for a personal loan.
     * @return LoanApplicationTestDataBuilder with personal loan defaults
     */
    public static LoanApplicationTestDataBuilder aPersonalLoan() {
        return new LoanApplicationTestDataBuilder()
                .withLoanType("PERSONAL")
                .withLoanPurpose("DEBT_CONSOLIDATION")
                .withLoanAmount(25000.0)
                .withLoanTermMonths(60)
                .withInterestRate(8.5)
                .withApplicant(ApplicantTestDataBuilder.aGoodCreditApplicant().build());
    }
    
    /**
     * Creates a LoanApplicationTestDataBuilder for a business loan.
     * @return LoanApplicationTestDataBuilder with business loan defaults
     */
    public static LoanApplicationTestDataBuilder aBusinessLoan() {
        return new LoanApplicationTestDataBuilder()
                .withLoanType("BUSINESS")
                .withLoanPurpose("BUSINESS_EXPANSION")
                .withLoanAmount(100000.0)
                .withLoanTermMonths(120)
                .withInterestRate(6.75)
                .withApplicant(ApplicantTestDataBuilder.aHighIncomeApplicant().asSelfEmployed().build());
    }
    
    /**
     * Creates a LoanApplicationTestDataBuilder for a high-risk loan application.
     * @return LoanApplicationTestDataBuilder with high-risk defaults
     */
    public static LoanApplicationTestDataBuilder aHighRiskLoan() {
        return new LoanApplicationTestDataBuilder()
                .withApplicant(ApplicantTestDataBuilder.aPoorCreditApplicant().build())
                .withLoanAmount(200000.0)
                .withDebtToIncomeRatio(45.0)
                .withRiskScore(300)
                .withInterestRate(7.5);
    }
    
    /**
     * Creates a LoanApplicationTestDataBuilder for a low-risk loan application.
     * @return LoanApplicationTestDataBuilder with low-risk defaults
     */
    public static LoanApplicationTestDataBuilder aLowRiskLoan() {
        return new LoanApplicationTestDataBuilder()
                .withApplicant(ApplicantTestDataBuilder.anExcellentCreditApplicant().build())
                .withLoanAmount(150000.0)
                .withDebtToIncomeRatio(15.0)
                .withRiskScore(800)
                .withInterestRate(3.75)
                .withDownPayment(75000.0);
    }
    
    /**
     * Creates a LoanApplicationTestDataBuilder for an approved loan.
     * @return LoanApplicationTestDataBuilder with approved loan defaults
     */
    public static LoanApplicationTestDataBuilder anApprovedLoan() {
        return new LoanApplicationTestDataBuilder()
                .withStatus("APPROVED")
                .withApproved(true)
                .withDecisionReason("Excellent credit and stable income")
                .withDecisionDate(LocalDate.now())
                .withUnderwriterId("UW001")
                .withApplicant(ApplicantTestDataBuilder.anExcellentCreditApplicant().build());
    }
    
    /**
     * Creates a LoanApplicationTestDataBuilder for a rejected loan.
     * @return LoanApplicationTestDataBuilder with rejected loan defaults
     */
    public static LoanApplicationTestDataBuilder aRejectedLoan() {
        return new LoanApplicationTestDataBuilder()
                .withStatus("REJECTED")
                .withApproved(false)
                .withDecisionReason("Insufficient credit score and high debt-to-income ratio")
                .withDecisionDate(LocalDate.now())
                .withUnderwriterId("UW002")
                .withApplicant(ApplicantTestDataBuilder.aPoorCreditApplicant().build());
    }
    
    public LoanApplicationTestDataBuilder withId(Long id) {
        this.loanApplication.setId(id);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withApplicationNumber(String applicationNumber) {
        this.loanApplication.setApplicationNumber(applicationNumber);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withApplicant(Applicant applicant) {
        this.loanApplication.setApplicant(applicant);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withCoApplicant(Applicant coApplicant) {
        this.loanApplication.setCoApplicant(coApplicant);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withLoanType(String loanType) {
        this.loanApplication.setLoanType(loanType);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withLoanPurpose(String loanPurpose) {
        this.loanApplication.setLoanPurpose(loanPurpose);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withLoanAmount(double loanAmount) {
        this.loanApplication.setLoanAmount(loanAmount);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withLoanTermMonths(int loanTermMonths) {
        this.loanApplication.setLoanTermMonths(loanTermMonths);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withInterestRate(double interestRate) {
        this.loanApplication.setInterestRate(interestRate);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withApplicationDate(LocalDate applicationDate) {
        this.loanApplication.setApplicationDate(applicationDate);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withLastUpdated(LocalDateTime lastUpdated) {
        this.loanApplication.setLastUpdated(lastUpdated);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withStatus(String status) {
        this.loanApplication.setStatus(status);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withNotes(String notes) {
        this.loanApplication.setNotes(notes);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withPropertyAddress(String propertyAddress) {
        this.loanApplication.setPropertyAddress(propertyAddress);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withPropertyValue(double propertyValue) {
        this.loanApplication.setPropertyValue(propertyValue);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withLoanToValueRatio(double loanToValueRatio) {
        this.loanApplication.setLoanToValueRatio(loanToValueRatio);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withDownPayment(double downPayment) {
        this.loanApplication.setDownPayment(downPayment);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withVehicleMake(String vehicleMake) {
        this.loanApplication.setVehicleMake(vehicleMake);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withVehicleModel(String vehicleModel) {
        this.loanApplication.setVehicleModel(vehicleModel);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withVehicleYear(int vehicleYear) {
        this.loanApplication.setVehicleYear(vehicleYear);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withVehicleValue(double vehicleValue) {
        this.loanApplication.setVehicleValue(vehicleValue);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withMonthlyPayment(double monthlyPayment) {
        this.loanApplication.setMonthlyPayment(monthlyPayment);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withDebtToIncomeRatio(double debtToIncomeRatio) {
        this.loanApplication.setDebtToIncomeRatio(debtToIncomeRatio);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withRiskScore(int riskScore) {
        this.loanApplication.setRiskScore(riskScore);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withApproved(boolean approved) {
        this.loanApplication.setApproved(approved);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withDecisionReason(String decisionReason) {
        this.loanApplication.setDecisionReason(decisionReason);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withDecisionDate(LocalDate decisionDate) {
        this.loanApplication.setDecisionDate(decisionDate);
        return this;
    }
    
    public LoanApplicationTestDataBuilder withUnderwriterId(String underwriterId) {
        this.loanApplication.setUnderwriterId(underwriterId);
        return this;
    }
    
    /**
     * Adds a co-applicant using the ApplicantTestDataBuilder.
     * @return LoanApplicationTestDataBuilder with co-applicant
     */
    public LoanApplicationTestDataBuilder withCoApplicant() {
        this.loanApplication.setCoApplicant(
            ApplicantTestDataBuilder.anApplicant()
                .withFirstName("Jane")
                .withLastName("Smith")
                .withEmail("jane.smith@example.com")
                .withSsn("987-65-4321")
                .build()
        );
        return this;
    }
    
    /**
     * Sets the loan as a refinance loan.
     * @return LoanApplicationTestDataBuilder configured for refinancing
     */
    public LoanApplicationTestDataBuilder asRefinance() {
        this.loanApplication.setLoanPurpose("REFINANCE");
        this.loanApplication.setNotes("Refinancing existing mortgage for better rate");
        return this;
    }
    
    /**
     * Sets the loan as a first-time home buyer loan.
     * @return LoanApplicationTestDataBuilder configured for first-time buyer
     */
    public LoanApplicationTestDataBuilder asFirstTimeBuyer() {
        this.loanApplication.setLoanPurpose("HOME_PURCHASE");
        this.loanApplication.setApplicant(ApplicantTestDataBuilder.aFirstTimeBuyer().build());
        this.loanApplication.setNotes("First-time home buyer application");
        return this;
    }
    
    /**
     * Builds and returns the LoanApplication instance.
     * @return configured LoanApplication instance
     */
    public LoanApplication build() {
        return this.loanApplication;
    }
}