package com.example.springdroolsintegration.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for loan approval rule execution response.
 * This class returns the results of loan approval rule evaluation, including
 * approval status, risk assessment, and decision details.
 */
@Schema(description = "Response model for loan approval rule evaluation results")
public class LoanApprovalResponse {
    
    /**
     * Application reference number
     */
    @Schema(description = "Application reference number", example = "LOAN12345678")
    private String applicationNumber;
    
    /**
     * Full name of the primary applicant
     */
    @Schema(description = "Full name of the primary applicant", example = "John Smith")
    private String applicantName;
    
    /**
     * Full name of the co-applicant (if applicable)
     */
    @Schema(description = "Full name of the co-applicant (if applicable)", example = "Jane Smith")
    private String coApplicantName;
    
    /**
     * Type of loan (e.g., MORTGAGE, AUTO, PERSONAL, STUDENT, BUSINESS)
     */
    @Schema(description = "Type of loan", example = "MORTGAGE")
    private String loanType;
    
    /**
     * Purpose of the loan (e.g., HOME_PURCHASE, REFINANCE, EDUCATION, DEBT_CONSOLIDATION)
     */
    @Schema(description = "Purpose of the loan", example = "HOME_PURCHASE")
    private String loanPurpose;
    
    /**
     * Requested loan amount
     */
    @Schema(description = "Requested loan amount", example = "250000")
    private double loanAmount;
    
    /**
     * Requested loan term in months
     */
    @Schema(description = "Requested loan term in months", example = "360")
    private int loanTermMonths;
    
    /**
     * Approved interest rate for the loan (percentage)
     */
    @Schema(description = "Approved interest rate for the loan (percentage)", example = "4.5")
    private double interestRate;
    
    /**
     * Calculated monthly payment amount
     */
    @Schema(description = "Calculated monthly payment amount", example = "1266.71")
    private double monthlyPayment;
    
    /**
     * Whether the loan application is approved
     */
    @Schema(description = "Whether the loan application is approved", example = "true")
    private boolean approved;
    
    /**
     * Current status of the application (e.g., APPROVED, DENIED, PENDING_REVIEW)
     */
    @Schema(description = "Current status of the application", example = "APPROVED")
    private String status;
    
    /**
     * Primary reason for the decision
     */
    @Schema(description = "Primary reason for the decision", example = "Credit score meets requirements")
    private String decisionReason;
    
    /**
     * List of all reasons that contributed to the decision
     */
    @Schema(description = "List of all reasons that contributed to the decision")
    private List<String> decisionFactors = new ArrayList<>();
    
    /**
     * Risk score calculated for this application (0-100, higher is riskier)
     */
    @Schema(description = "Risk score calculated for this application (0-100, higher is riskier)", example = "35")
    private int riskScore;
    
    /**
     * Risk category (e.g., LOW, MEDIUM, HIGH)
     */
    @Schema(description = "Risk category", example = "MEDIUM")
    private String riskCategory;
    
    /**
     * Calculated debt-to-income ratio (percentage)
     */
    @Schema(description = "Calculated debt-to-income ratio (percentage)", example = "32.5")
    private double debtToIncomeRatio;
    
    /**
     * Calculated loan-to-value ratio for mortgage loans (percentage)
     */
    @Schema(description = "Calculated loan-to-value ratio for mortgage loans (percentage)", example = "80.0")
    private double loanToValueRatio;
    
    /**
     * Maximum approved loan amount (may differ from requested amount)
     */
    @Schema(description = "Maximum approved loan amount", example = "240000")
    private double approvedAmount;
    
    /**
     * List of conditions that must be met for final approval
     */
    @Schema(description = "List of conditions that must be met for final approval")
    private List<String> conditions = new ArrayList<>();
    
    /**
     * List of required documentation for final approval
     */
    @Schema(description = "List of required documentation for final approval")
    private List<String> requiredDocuments = new ArrayList<>();
    
    /**
     * List of alternative loan options if the requested loan was not approved
     */
    @Schema(description = "List of alternative loan options if the requested loan was not approved")
    private List<AlternativeLoanOption> alternativeOptions = new ArrayList<>();
    
    /**
     * Names of the rules that were applied during evaluation
     */
    @Schema(description = "Names of the rules that were applied during evaluation")
    private List<String> appliedRules = new ArrayList<>();
    
    /**
     * Date when the decision was made
     */
    @Schema(description = "Date when the decision was made")
    private LocalDate decisionDate;
    
    /**
     * Timestamp when the response was generated
     */
    @Schema(description = "Timestamp when the response was generated")
    private LocalDateTime timestamp;
    
    /**
     * Expiration date of the loan offer (if approved)
     */
    @Schema(description = "Expiration date of the loan offer (if approved)")
    private LocalDate offerExpirationDate;
    
    /**
     * Additional notes or comments about the decision
     */
    @Schema(description = "Additional notes or comments about the decision")
    private String notes;
    
    /**
     * Default constructor
     */
    public LoanApprovalResponse() {
        this.timestamp = LocalDateTime.now();
        this.decisionDate = LocalDate.now();
    }
    
    /**
     * Constructor with essential fields
     *
     * @param applicationNumber The application reference number
     * @param applicantName The applicant's name
     * @param loanType The type of loan
     * @param loanAmount The requested loan amount
     * @param approved Whether the loan is approved
     * @param status The application status
     * @param decisionReason The primary reason for the decision
     */
    public LoanApprovalResponse(String applicationNumber, String applicantName, String loanType,
                               double loanAmount, boolean approved, String status, String decisionReason) {
        this.applicationNumber = applicationNumber;
        this.applicantName = applicantName;
        this.loanType = loanType;
        this.loanAmount = loanAmount;
        this.approved = approved;
        this.status = status;
        this.decisionReason = decisionReason;
        this.timestamp = LocalDateTime.now();
        this.decisionDate = LocalDate.now();
        
        // Set offer expiration date to 30 days from now if approved
        if (approved) {
            this.offerExpirationDate = LocalDate.now().plusDays(30);
        }
    }
    
    // Getters and setters
    
    public String getApplicationNumber() {
        return applicationNumber;
    }
    
    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }
    
    public String getApplicantName() {
        return applicantName;
    }
    
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
    
    public String getCoApplicantName() {
        return coApplicantName;
    }
    
    public void setCoApplicantName(String coApplicantName) {
        this.coApplicantName = coApplicantName;
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
    }
    
    public int getLoanTermMonths() {
        return loanTermMonths;
    }
    
    public void setLoanTermMonths(int loanTermMonths) {
        this.loanTermMonths = loanTermMonths;
    }
    
    public double getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
    
    public double getMonthlyPayment() {
        return monthlyPayment;
    }
    
    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }
    
    public boolean isApproved() {
        return approved;
    }
    
    public void setApproved(boolean approved) {
        this.approved = approved;
        
        // Set offer expiration date to 30 days from now if approved
        if (approved) {
            this.offerExpirationDate = LocalDate.now().plusDays(30);
        } else {
            this.offerExpirationDate = null;
        }
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDecisionReason() {
        return decisionReason;
    }
    
    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }
    
    public List<String> getDecisionFactors() {
        return decisionFactors;
    }
    
    public void setDecisionFactors(List<String> decisionFactors) {
        this.decisionFactors = decisionFactors != null ? decisionFactors : new ArrayList<>();
    }
    
    public int getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
        updateRiskCategory();
    }
    
    public String getRiskCategory() {
        return riskCategory;
    }
    
    public void setRiskCategory(String riskCategory) {
        this.riskCategory = riskCategory;
    }
    
    public double getDebtToIncomeRatio() {
        return debtToIncomeRatio;
    }
    
    public void setDebtToIncomeRatio(double debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }
    
    public double getLoanToValueRatio() {
        return loanToValueRatio;
    }
    
    public void setLoanToValueRatio(double loanToValueRatio) {
        this.loanToValueRatio = loanToValueRatio;
    }
    
    public double getApprovedAmount() {
        return approvedAmount;
    }
    
    public void setApprovedAmount(double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }
    
    public List<String> getConditions() {
        return conditions;
    }
    
    public void setConditions(List<String> conditions) {
        this.conditions = conditions != null ? conditions : new ArrayList<>();
    }
    
    public List<String> getRequiredDocuments() {
        return requiredDocuments;
    }
    
    public void setRequiredDocuments(List<String> requiredDocuments) {
        this.requiredDocuments = requiredDocuments != null ? requiredDocuments : new ArrayList<>();
    }
    
    public List<AlternativeLoanOption> getAlternativeOptions() {
        return alternativeOptions;
    }
    
    public void setAlternativeOptions(List<AlternativeLoanOption> alternativeOptions) {
        this.alternativeOptions = alternativeOptions != null ? alternativeOptions : new ArrayList<>();
    }
    
    public List<String> getAppliedRules() {
        return appliedRules;
    }
    
    public void setAppliedRules(List<String> appliedRules) {
        this.appliedRules = appliedRules != null ? appliedRules : new ArrayList<>();
    }
    
    public LocalDate getDecisionDate() {
        return decisionDate;
    }
    
    public void setDecisionDate(LocalDate decisionDate) {
        this.decisionDate = decisionDate;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public LocalDate getOfferExpirationDate() {
        return offerExpirationDate;
    }
    
    public void setOfferExpirationDate(LocalDate offerExpirationDate) {
        this.offerExpirationDate = offerExpirationDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * Adds a decision factor to the list of factors.
     * 
     * @param factor The decision factor to add
     */
    public void addDecisionFactor(String factor) {
        if (this.decisionFactors == null) {
            this.decisionFactors = new ArrayList<>();
        }
        this.decisionFactors.add(factor);
    }
    
    /**
     * Adds a condition to the list of conditions.
     * 
     * @param condition The condition to add
     */
    public void addCondition(String condition) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<>();
        }
        this.conditions.add(condition);
    }
    
    /**
     * Adds a required document to the list of required documents.
     * 
     * @param document The required document to add
     */
    public void addRequiredDocument(String document) {
        if (this.requiredDocuments == null) {
            this.requiredDocuments = new ArrayList<>();
        }
        this.requiredDocuments.add(document);
    }
    
    /**
     * Adds an alternative loan option to the list of alternatives.
     * 
     * @param option The alternative loan option to add
     */
    public void addAlternativeOption(AlternativeLoanOption option) {
        if (this.alternativeOptions == null) {
            this.alternativeOptions = new ArrayList<>();
        }
        this.alternativeOptions.add(option);
    }
    
    /**
     * Adds a rule name to the list of applied rules.
     * 
     * @param ruleName The name of the rule to add
     */
    public void addAppliedRule(String ruleName) {
        if (this.appliedRules == null) {
            this.appliedRules = new ArrayList<>();
        }
        this.appliedRules.add(ruleName);
    }
    
    /**
     * Updates the risk category based on the risk score.
     * This method is called automatically when the risk score is set.
     */
    private void updateRiskCategory() {
        if (riskScore < 30) {
            this.riskCategory = "LOW";
        } else if (riskScore < 60) {
            this.riskCategory = "MEDIUM";
        } else {
            this.riskCategory = "HIGH";
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
     * 
     * @return The calculated monthly payment
     */
    public double calculateMonthlyPayment() {
        if (loanAmount <= 0 || loanTermMonths <= 0) {
            this.monthlyPayment = 0;
            return 0;
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
        
        return this.monthlyPayment;
    }
    
    /**
     * Calculates the total cost of the loan (principal + interest).
     * 
     * @return The total cost of the loan
     */
    public double calculateTotalCost() {
        return monthlyPayment * loanTermMonths;
    }
    
    /**
     * Calculates the total interest paid over the life of the loan.
     * 
     * @return The total interest paid
     */
    public double calculateTotalInterest() {
        return calculateTotalCost() - loanAmount;
    }
    
    /**
     * Checks if the loan has conditions that must be met.
     * 
     * @return true if there are conditions, false otherwise
     */
    public boolean hasConditions() {
        return conditions != null && !conditions.isEmpty();
    }
    
    /**
     * Checks if the loan has alternative options.
     * 
     * @return true if there are alternative options, false otherwise
     */
    public boolean hasAlternativeOptions() {
        return alternativeOptions != null && !alternativeOptions.isEmpty();
    }
    
    /**
     * Checks if the approved amount is less than the requested amount.
     * 
     * @return true if the approved amount is less than requested, false otherwise
     */
    public boolean isPartiallyApproved() {
        return approved && approvedAmount > 0 && approvedAmount < loanAmount;
    }
    
    /**
     * Checks if the loan offer has expired.
     * 
     * @return true if the offer has expired, false otherwise
     */
    public boolean isOfferExpired() {
        return offerExpirationDate != null && offerExpirationDate.isBefore(LocalDate.now());
    }
    
    @Override
    public String toString() {
        return "LoanApprovalResponse{" +
                "applicationNumber='" + applicationNumber + '\'' +
                ", applicantName='" + applicantName + '\'' +
                ", loanType='" + loanType + '\'' +
                ", loanAmount=" + loanAmount +
                ", approved=" + approved +
                ", status='" + status + '\'' +
                ", riskCategory='" + riskCategory + '\'' +
                ", decisionDate=" + decisionDate +
                '}';
    }
    
    /**
     * Inner class representing an alternative loan option.
     */
    @Schema(description = "Alternative loan option")
    public static class AlternativeLoanOption {
        
        /**
         * Type of the alternative loan
         */
        @Schema(description = "Type of the alternative loan", example = "PERSONAL")
        private String loanType;
        
        /**
         * Maximum amount for the alternative loan
         */
        @Schema(description = "Maximum amount for the alternative loan", example = "15000")
        private double maxAmount;
        
        /**
         * Term in months for the alternative loan
         */
        @Schema(description = "Term in months for the alternative loan", example = "60")
        private int termMonths;
        
        /**
         * Interest rate for the alternative loan
         */
        @Schema(description = "Interest rate for the alternative loan", example = "7.5")
        private double interestRate;
        
        /**
         * Estimated monthly payment for the alternative loan
         */
        @Schema(description = "Estimated monthly payment for the alternative loan", example = "301.12")
        private double monthlyPayment;
        
        /**
         * Description of the alternative loan
         */
        @Schema(description = "Description of the alternative loan", example = "Personal loan with reduced amount")
        private String description;
        
        /**
         * Default constructor
         */
        public AlternativeLoanOption() {
        }
        
        /**
         * Constructor with all fields
         *
         * @param loanType The type of loan
         * @param maxAmount The maximum amount
         * @param termMonths The term in months
         * @param interestRate The interest rate
         * @param monthlyPayment The monthly payment
         * @param description The description
         */
        public AlternativeLoanOption(String loanType, double maxAmount, int termMonths,
                                    double interestRate, double monthlyPayment, String description) {
            this.loanType = loanType;
            this.maxAmount = maxAmount;
            this.termMonths = termMonths;
            this.interestRate = interestRate;
            this.monthlyPayment = monthlyPayment;
            this.description = description;
        }
        
        // Getters and setters
        
        public String getLoanType() {
            return loanType;
        }
        
        public void setLoanType(String loanType) {
            this.loanType = loanType;
        }
        
        public double getMaxAmount() {
            return maxAmount;
        }
        
        public void setMaxAmount(double maxAmount) {
            this.maxAmount = maxAmount;
        }
        
        public int getTermMonths() {
            return termMonths;
        }
        
        public void setTermMonths(int termMonths) {
            this.termMonths = termMonths;
        }
        
        public double getInterestRate() {
            return interestRate;
        }
        
        public void setInterestRate(double interestRate) {
            this.interestRate = interestRate;
        }
        
        public double getMonthlyPayment() {
            return monthlyPayment;
        }
        
        public void setMonthlyPayment(double monthlyPayment) {
            this.monthlyPayment = monthlyPayment;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return "AlternativeLoanOption{" +
                    "loanType='" + loanType + '\'' +
                    ", maxAmount=" + maxAmount +
                    ", termMonths=" + termMonths +
                    ", interestRate=" + interestRate +
                    ", monthlyPayment=" + monthlyPayment +
                    '}';
        }
    }
}