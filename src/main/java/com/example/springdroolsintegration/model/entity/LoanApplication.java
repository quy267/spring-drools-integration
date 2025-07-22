package com.example.springdroolsintegration.model.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * LoanApplication entity for the loan approval rules use case.
 * This entity represents a loan application in the system and contains all relevant
 * information needed for loan approval decision-making.
 */
public class LoanApplication {
    
    /**
     * Unique identifier for the loan application
     */
    private Long id;
    
    /**
     * Application reference number (unique identifier for external systems)
     */
    @NotBlank(message = "Application number is required")
    @Pattern(regexp = "^[A-Z0-9]{8,12}$", message = "Application number must be 8-12 alphanumeric characters")
    private String applicationNumber;
    
    /**
     * The applicant applying for the loan
     */
    @NotNull(message = "Applicant is required")
    private Applicant applicant;
    
    /**
     * Co-applicant for the loan (optional)
     */
    private Applicant coApplicant;
    
    /**
     * Type of loan (e.g., MORTGAGE, AUTO, PERSONAL, STUDENT, BUSINESS)
     */
    @NotBlank(message = "Loan type is required")
    private String loanType;
    
    /**
     * Purpose of the loan (e.g., HOME_PURCHASE, REFINANCE, EDUCATION, DEBT_CONSOLIDATION)
     */
    @NotBlank(message = "Loan purpose is required")
    private String loanPurpose;
    
    /**
     * Requested loan amount in base currency
     */
    @DecimalMin(value = "1.0", message = "Loan amount must be positive")
    private double loanAmount;
    
    /**
     * Requested loan term in months
     */
    @Min(value = 1, message = "Loan term must be at least 1 month")
    private int loanTermMonths;
    
    /**
     * Interest rate for the loan (percentage)
     */
    @DecimalMin(value = "0.0", message = "Interest rate cannot be negative")
    private double interestRate;
    
    /**
     * Date when the application was submitted
     */
    @NotNull(message = "Application date is required")
    @PastOrPresent(message = "Application date cannot be in the future")
    private LocalDate applicationDate;
    
    /**
     * Date and time when the application was last updated
     */
    private LocalDateTime lastUpdated;
    
    /**
     * Current status of the application (e.g., PENDING, APPROVED, DENIED, WITHDRAWN)
     */
    @NotBlank(message = "Status is required")
    private String status;
    
    /**
     * Underwriter's notes or comments on the application
     */
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    /**
     * For mortgage loans: property address
     */
    private String propertyAddress;
    
    /**
     * For mortgage loans: property value
     */
    private double propertyValue;
    
    /**
     * For mortgage loans: loan-to-value ratio (LTV)
     */
    private double loanToValueRatio;
    
    /**
     * For mortgage loans: down payment amount
     */
    private double downPayment;
    
    /**
     * For auto loans: vehicle make
     */
    private String vehicleMake;
    
    /**
     * For auto loans: vehicle model
     */
    private String vehicleModel;
    
    /**
     * For auto loans: vehicle year
     */
    private int vehicleYear;
    
    /**
     * For auto loans: vehicle value
     */
    private double vehicleValue;
    
    /**
     * Calculated monthly payment amount
     */
    private double monthlyPayment;
    
    /**
     * Calculated debt-to-income ratio after this loan
     */
    private double debtToIncomeRatio;
    
    /**
     * Risk score calculated for this application (internal scoring)
     */
    private int riskScore;
    
    /**
     * Whether the application has been approved
     */
    private boolean approved;
    
    /**
     * Reason for approval or denial
     */
    private String decisionReason;
    
    /**
     * Date when the decision was made
     */
    private LocalDate decisionDate;
    
    /**
     * User ID of the underwriter who made the decision
     */
    private String underwriterId;
    
    /**
     * Default constructor
     */
    public LoanApplication() {
        this.applicationDate = LocalDate.now();
        this.lastUpdated = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    /**
     * Constructor with essential fields
     *
     * @param applicationNumber The application reference number
     * @param applicant The primary applicant
     * @param loanType The type of loan
     * @param loanPurpose The purpose of the loan
     * @param loanAmount The requested loan amount
     * @param loanTermMonths The requested loan term in months
     */
    public LoanApplication(String applicationNumber, Applicant applicant, String loanType, 
                          String loanPurpose, double loanAmount, int loanTermMonths) {
        this.applicationNumber = applicationNumber;
        this.applicant = applicant;
        this.loanType = loanType;
        this.loanPurpose = loanPurpose;
        this.loanAmount = loanAmount;
        this.loanTermMonths = loanTermMonths;
        this.applicationDate = LocalDate.now();
        this.lastUpdated = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    /**
     * Full constructor with all fields
     *
     * @param id The application ID
     * @param applicationNumber The application reference number
     * @param applicant The primary applicant
     * @param coApplicant The co-applicant (if any)
     * @param loanType The type of loan
     * @param loanPurpose The purpose of the loan
     * @param loanAmount The requested loan amount
     * @param loanTermMonths The requested loan term in months
     * @param interestRate The interest rate
     * @param applicationDate The application submission date
     * @param lastUpdated The last update date/time
     * @param status The current status
     * @param notes Underwriter notes
     * @param propertyAddress Property address (for mortgages)
     * @param propertyValue Property value (for mortgages)
     * @param loanToValueRatio Loan-to-value ratio (for mortgages)
     * @param downPayment Down payment amount (for mortgages)
     * @param vehicleMake Vehicle make (for auto loans)
     * @param vehicleModel Vehicle model (for auto loans)
     * @param vehicleYear Vehicle year (for auto loans)
     * @param vehicleValue Vehicle value (for auto loans)
     * @param monthlyPayment Calculated monthly payment
     * @param debtToIncomeRatio Calculated debt-to-income ratio
     * @param riskScore Internal risk score
     * @param approved Whether the application is approved
     * @param decisionReason Reason for the decision
     * @param decisionDate Date of the decision
     * @param underwriterId ID of the underwriter
     */
    public LoanApplication(Long id, String applicationNumber, Applicant applicant, Applicant coApplicant,
                          String loanType, String loanPurpose, double loanAmount, int loanTermMonths,
                          double interestRate, LocalDate applicationDate, LocalDateTime lastUpdated,
                          String status, String notes, String propertyAddress, double propertyValue,
                          double loanToValueRatio, double downPayment, String vehicleMake,
                          String vehicleModel, int vehicleYear, double vehicleValue,
                          double monthlyPayment, double debtToIncomeRatio, int riskScore,
                          boolean approved, String decisionReason, LocalDate decisionDate,
                          String underwriterId) {
        this.id = id;
        this.applicationNumber = applicationNumber;
        this.applicant = applicant;
        this.coApplicant = coApplicant;
        this.loanType = loanType;
        this.loanPurpose = loanPurpose;
        this.loanAmount = loanAmount;
        this.loanTermMonths = loanTermMonths;
        this.interestRate = interestRate;
        this.applicationDate = applicationDate;
        this.lastUpdated = lastUpdated;
        this.status = status;
        this.notes = notes;
        this.propertyAddress = propertyAddress;
        this.propertyValue = propertyValue;
        this.loanToValueRatio = loanToValueRatio;
        this.downPayment = downPayment;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleYear = vehicleYear;
        this.vehicleValue = vehicleValue;
        this.monthlyPayment = monthlyPayment;
        this.debtToIncomeRatio = debtToIncomeRatio;
        this.riskScore = riskScore;
        this.approved = approved;
        this.decisionReason = decisionReason;
        this.decisionDate = decisionDate;
        this.underwriterId = underwriterId;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getApplicationNumber() {
        return applicationNumber;
    }
    
    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }
    
    public Applicant getApplicant() {
        return applicant;
    }
    
    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }
    
    public Applicant getCoApplicant() {
        return coApplicant;
    }
    
    public void setCoApplicant(Applicant coApplicant) {
        this.coApplicant = coApplicant;
    }
    
    public String getLoanType() {
        return loanType;
    }
    
    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }
    
    public String getLoanPurpose() {
        return loanPurpose;
    }
    
    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }
    
    public double getLoanAmount() {
        return loanAmount;
    }
    
    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
        updateLoanToValueRatio();
    }
    
    public int getLoanTermMonths() {
        return loanTermMonths;
    }
    
    public void setLoanTermMonths(int loanTermMonths) {
        this.loanTermMonths = loanTermMonths;
        calculateMonthlyPayment();
    }
    
    public double getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
        calculateMonthlyPayment();
    }
    
    public LocalDate getApplicationDate() {
        return applicationDate;
    }
    
    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getPropertyAddress() {
        return propertyAddress;
    }
    
    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }
    
    public double getPropertyValue() {
        return propertyValue;
    }
    
    public void setPropertyValue(double propertyValue) {
        this.propertyValue = propertyValue;
        updateLoanToValueRatio();
    }
    
    public double getLoanToValueRatio() {
        return loanToValueRatio;
    }
    
    public void setLoanToValueRatio(double loanToValueRatio) {
        this.loanToValueRatio = loanToValueRatio;
    }
    
    public double getDownPayment() {
        return downPayment;
    }
    
    public void setDownPayment(double downPayment) {
        this.downPayment = downPayment;
    }
    
    public String getVehicleMake() {
        return vehicleMake;
    }
    
    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }
    
    public String getVehicleModel() {
        return vehicleModel;
    }
    
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }
    
    public int getVehicleYear() {
        return vehicleYear;
    }
    
    public void setVehicleYear(int vehicleYear) {
        this.vehicleYear = vehicleYear;
    }
    
    public double getVehicleValue() {
        return vehicleValue;
    }
    
    public void setVehicleValue(double vehicleValue) {
        this.vehicleValue = vehicleValue;
    }
    
    public double getMonthlyPayment() {
        return monthlyPayment;
    }
    
    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }
    
    public double getDebtToIncomeRatio() {
        return debtToIncomeRatio;
    }
    
    public void setDebtToIncomeRatio(double debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }
    
    public int getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }
    
    public boolean isApproved() {
        return approved;
    }
    
    public void setApproved(boolean approved) {
        this.approved = approved;
        this.status = approved ? "APPROVED" : "DENIED";
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getDecisionReason() {
        return decisionReason;
    }
    
    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }
    
    public LocalDate getDecisionDate() {
        return decisionDate;
    }
    
    public void setDecisionDate(LocalDate decisionDate) {
        this.decisionDate = decisionDate;
    }
    
    public String getUnderwriterId() {
        return underwriterId;
    }
    
    public void setUnderwriterId(String underwriterId) {
        this.underwriterId = underwriterId;
    }
    
    /**
     * Updates the loan-to-value ratio based on the loan amount and property value.
     * This is primarily used for mortgage loans.
     */
    private void updateLoanToValueRatio() {
        if (propertyValue > 0) {
            this.loanToValueRatio = (loanAmount / propertyValue) * 100;
        }
    }
    
    /**
     * Calculates the monthly payment based on loan amount, interest rate, and term.
     * Uses the standard amortization formula: P = (r*A) / (1 - (1+r)^-n)
     * Where:
     * P = monthly payment
     * A = loan amount
     * r = monthly interest rate (annual rate / 12 / 100)
     * n = number of payments (term in months)
     */
    public void calculateMonthlyPayment() {
        if (loanAmount <= 0 || loanTermMonths <= 0) {
            this.monthlyPayment = 0;
            return;
        }
        
        double monthlyRate = interestRate / 12 / 100;
        
        if (monthlyRate == 0) {
            // Simple division for zero-interest loans
            this.monthlyPayment = loanAmount / loanTermMonths;
        } else {
            // Standard amortization formula
            double denominator = 1 - Math.pow(1 + monthlyRate, -loanTermMonths);
            this.monthlyPayment = (monthlyRate * loanAmount) / denominator;
        }
    }
    
    /**
     * Calculates the debt-to-income ratio including this loan's monthly payment.
     * DTI = (Existing Monthly Debt + New Loan Payment) / Monthly Income
     * 
     * @return The calculated debt-to-income ratio
     */
    public double calculateDebtToIncomeRatio() {
        if (applicant == null || applicant.getMonthlyIncome() <= 0) {
            return 0;
        }
        
        double totalIncome = applicant.getMonthlyIncome();
        double totalDebt = applicant.getMonthlyDebtPayments() + monthlyPayment;
        
        // Include co-applicant income and debt if available
        if (coApplicant != null) {
            totalIncome += coApplicant.getMonthlyIncome();
            totalDebt += coApplicant.getMonthlyDebtPayments();
        }
        
        if (totalIncome <= 0) {
            return 0;
        }
        
        this.debtToIncomeRatio = totalDebt / totalIncome;
        return this.debtToIncomeRatio;
    }
    
    /**
     * Checks if the loan is a mortgage loan.
     * 
     * @return true if the loan type is MORTGAGE, false otherwise
     */
    public boolean isMortgage() {
        return "MORTGAGE".equalsIgnoreCase(loanType);
    }
    
    /**
     * Checks if the loan is an auto loan.
     * 
     * @return true if the loan type is AUTO, false otherwise
     */
    public boolean isAutoLoan() {
        return "AUTO".equalsIgnoreCase(loanType);
    }
    
    /**
     * Checks if the loan is a personal loan.
     * 
     * @return true if the loan type is PERSONAL, false otherwise
     */
    public boolean isPersonalLoan() {
        return "PERSONAL".equalsIgnoreCase(loanType);
    }
    
    /**
     * Checks if the loan is a student loan.
     * 
     * @return true if the loan type is STUDENT, false otherwise
     */
    public boolean isStudentLoan() {
        return "STUDENT".equalsIgnoreCase(loanType);
    }
    
    /**
     * Checks if the loan is a business loan.
     * 
     * @return true if the loan type is BUSINESS, false otherwise
     */
    public boolean isBusinessLoan() {
        return "BUSINESS".equalsIgnoreCase(loanType);
    }
    
    /**
     * Checks if the loan has a co-applicant.
     * 
     * @return true if there is a co-applicant, false otherwise
     */
    public boolean hasCoApplicant() {
        return coApplicant != null;
    }
    
    /**
     * Checks if the loan-to-value ratio is within acceptable limits.
     * For most mortgage loans, LTV should be 80% or less to avoid PMI.
     * 
     * @return true if LTV is 80% or less, false otherwise
     */
    public boolean hasAcceptableLTV() {
        return loanToValueRatio <= 80.0;
    }
    
    /**
     * Checks if the debt-to-income ratio is within acceptable limits.
     * For most loans, DTI should be 43% or less.
     * 
     * @return true if DTI is 43% or less, false otherwise
     */
    public boolean hasAcceptableDTI() {
        return debtToIncomeRatio <= 0.43;
    }
    
    /**
     * Updates the application status and sets the last updated timestamp.
     * 
     * @param newStatus The new status to set
     */
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Records a loan decision with reason and underwriter information.
     * 
     * @param approved Whether the loan is approved
     * @param reason The reason for the decision
     * @param underwriterId The ID of the underwriter making the decision
     */
    public void recordDecision(boolean approved, String reason, String underwriterId) {
        this.approved = approved;
        this.status = approved ? "APPROVED" : "DENIED";
        this.decisionReason = reason;
        this.decisionDate = LocalDate.now();
        this.underwriterId = underwriterId;
        this.lastUpdated = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "LoanApplication{" +
                "id=" + id +
                ", applicationNumber='" + applicationNumber + '\'' +
                ", applicant=" + (applicant != null ? applicant.getFullName() : "null") +
                ", loanType='" + loanType + '\'' +
                ", loanAmount=" + loanAmount +
                ", loanTermMonths=" + loanTermMonths +
                ", status='" + status + '\'' +
                ", approved=" + approved +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanApplication that = (LoanApplication) o;
        return Objects.equals(id, that.id) || 
               Objects.equals(applicationNumber, that.applicationNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, applicationNumber);
    }
}