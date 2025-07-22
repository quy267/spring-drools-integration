package com.example.springdroolsintegration.model.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;

/**
 * CreditScore entity for the loan approval rules use case.
 * This entity represents a credit assessment for an applicant and contains
 * detailed credit information needed for loan approval decision-making.
 */
public class CreditScore {
    
    /**
     * Unique identifier for the credit score record
     */
    private Long id;
    
    /**
     * The applicant this credit score belongs to
     */
    @NotNull(message = "Applicant is required")
    private Applicant applicant;
    
    /**
     * The primary credit score (e.g., FICO score)
     * Range is typically 300-850
     */
    @Min(value = 300, message = "Credit score must be at least 300")
    @Max(value = 850, message = "Credit score cannot exceed 850")
    private int score;
    
    /**
     * The credit bureau that provided the score (e.g., EXPERIAN, TRANSUNION, EQUIFAX)
     */
    @NotBlank(message = "Credit bureau is required")
    private String bureau;
    
    /**
     * The date when the credit score was retrieved
     */
    @NotNull(message = "Report date is required")
    @PastOrPresent(message = "Report date cannot be in the future")
    private LocalDate reportDate;
    
    /**
     * The credit score model used (e.g., FICO 8, FICO 9, VantageScore 3.0)
     */
    @NotBlank(message = "Score model is required")
    private String scoreModel;
    
    /**
     * Number of open credit accounts
     */
    @Min(value = 0, message = "Open accounts must be non-negative")
    private int openAccounts;
    
    /**
     * Total credit limit across all accounts
     */
    @Min(value = 0, message = "Total credit limit must be non-negative")
    private double totalCreditLimit;
    
    /**
     * Total current balance across all accounts
     */
    @Min(value = 0, message = "Total balance must be non-negative")
    private double totalBalance;
    
    /**
     * Credit utilization ratio (balance / limit)
     */
    @Min(value = 0, message = "Credit utilization must be non-negative")
    private double creditUtilization;
    
    /**
     * Number of accounts with on-time payments
     */
    @Min(value = 0, message = "On-time accounts must be non-negative")
    private int onTimeAccounts;
    
    /**
     * Number of accounts with late payments
     */
    @Min(value = 0, message = "Late accounts must be non-negative")
    private int lateAccounts;
    
    /**
     * Number of accounts in collections
     */
    @Min(value = 0, message = "Collections must be non-negative")
    private int collectionsAccounts;
    
    /**
     * Number of derogatory marks (collections, charge-offs, etc.)
     */
    @Min(value = 0, message = "Derogatory marks must be non-negative")
    private int derogatoryMarks;
    
    /**
     * Number of hard inquiries in the last 2 years
     */
    @Min(value = 0, message = "Hard inquiries must be non-negative")
    private int hardInquiries;
    
    /**
     * Length of credit history in months
     */
    @Min(value = 0, message = "Credit history length must be non-negative")
    private int creditHistoryMonths;
    
    /**
     * Whether the applicant has a bankruptcy in the last 7 years
     */
    private boolean hasBankruptcy;
    
    /**
     * Whether the applicant has a foreclosure in the last 7 years
     */
    private boolean hasForeclosure;
    
    /**
     * Whether the applicant has any tax liens
     */
    private boolean hasTaxLiens;
    
    /**
     * Whether the applicant has any judgments
     */
    private boolean hasJudgments;
    
    /**
     * Additional notes or comments about the credit report
     */
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    /**
     * Default constructor
     */
    public CreditScore() {
        this.reportDate = LocalDate.now();
    }
    
    /**
     * Constructor with essential fields
     *
     * @param applicant The applicant
     * @param score The credit score
     * @param bureau The credit bureau
     * @param scoreModel The score model
     */
    public CreditScore(Applicant applicant, int score, String bureau, String scoreModel) {
        this.applicant = applicant;
        this.score = score;
        this.bureau = bureau;
        this.scoreModel = scoreModel;
        this.reportDate = LocalDate.now();
    }
    
    /**
     * Full constructor with all fields
     *
     * @param id The record ID
     * @param applicant The applicant
     * @param score The credit score
     * @param bureau The credit bureau
     * @param reportDate The report date
     * @param scoreModel The score model
     * @param openAccounts Number of open accounts
     * @param totalCreditLimit Total credit limit
     * @param totalBalance Total balance
     * @param creditUtilization Credit utilization ratio
     * @param onTimeAccounts Number of on-time accounts
     * @param lateAccounts Number of late accounts
     * @param collectionsAccounts Number of collections accounts
     * @param derogatoryMarks Number of derogatory marks
     * @param hardInquiries Number of hard inquiries
     * @param creditHistoryMonths Length of credit history in months
     * @param hasBankruptcy Whether there's a bankruptcy
     * @param hasForeclosure Whether there's a foreclosure
     * @param hasTaxLiens Whether there are tax liens
     * @param hasJudgments Whether there are judgments
     * @param notes Additional notes
     */
    public CreditScore(Long id, Applicant applicant, int score, String bureau, LocalDate reportDate,
                      String scoreModel, int openAccounts, double totalCreditLimit, double totalBalance,
                      double creditUtilization, int onTimeAccounts, int lateAccounts, int collectionsAccounts,
                      int derogatoryMarks, int hardInquiries, int creditHistoryMonths, boolean hasBankruptcy,
                      boolean hasForeclosure, boolean hasTaxLiens, boolean hasJudgments, String notes) {
        this.id = id;
        this.applicant = applicant;
        this.score = score;
        this.bureau = bureau;
        this.reportDate = reportDate;
        this.scoreModel = scoreModel;
        this.openAccounts = openAccounts;
        this.totalCreditLimit = totalCreditLimit;
        this.totalBalance = totalBalance;
        this.creditUtilization = creditUtilization;
        this.onTimeAccounts = onTimeAccounts;
        this.lateAccounts = lateAccounts;
        this.collectionsAccounts = collectionsAccounts;
        this.derogatoryMarks = derogatoryMarks;
        this.hardInquiries = hardInquiries;
        this.creditHistoryMonths = creditHistoryMonths;
        this.hasBankruptcy = hasBankruptcy;
        this.hasForeclosure = hasForeclosure;
        this.hasTaxLiens = hasTaxLiens;
        this.hasJudgments = hasJudgments;
        this.notes = notes;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Applicant getApplicant() {
        return applicant;
    }
    
    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public String getBureau() {
        return bureau;
    }
    
    public void setBureau(String bureau) {
        this.bureau = bureau;
    }
    
    public LocalDate getReportDate() {
        return reportDate;
    }
    
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }
    
    public String getScoreModel() {
        return scoreModel;
    }
    
    public void setScoreModel(String scoreModel) {
        this.scoreModel = scoreModel;
    }
    
    public int getOpenAccounts() {
        return openAccounts;
    }
    
    public void setOpenAccounts(int openAccounts) {
        this.openAccounts = openAccounts;
    }
    
    public double getTotalCreditLimit() {
        return totalCreditLimit;
    }
    
    public void setTotalCreditLimit(double totalCreditLimit) {
        this.totalCreditLimit = totalCreditLimit;
        calculateCreditUtilization();
    }
    
    public double getTotalBalance() {
        return totalBalance;
    }
    
    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
        calculateCreditUtilization();
    }
    
    public double getCreditUtilization() {
        return creditUtilization;
    }
    
    public void setCreditUtilization(double creditUtilization) {
        this.creditUtilization = creditUtilization;
    }
    
    public int getOnTimeAccounts() {
        return onTimeAccounts;
    }
    
    public void setOnTimeAccounts(int onTimeAccounts) {
        this.onTimeAccounts = onTimeAccounts;
    }
    
    public int getLateAccounts() {
        return lateAccounts;
    }
    
    public void setLateAccounts(int lateAccounts) {
        this.lateAccounts = lateAccounts;
    }
    
    public int getCollectionsAccounts() {
        return collectionsAccounts;
    }
    
    public void setCollectionsAccounts(int collectionsAccounts) {
        this.collectionsAccounts = collectionsAccounts;
    }
    
    public int getDerogatoryMarks() {
        return derogatoryMarks;
    }
    
    public void setDerogatoryMarks(int derogatoryMarks) {
        this.derogatoryMarks = derogatoryMarks;
    }
    
    public int getHardInquiries() {
        return hardInquiries;
    }
    
    public void setHardInquiries(int hardInquiries) {
        this.hardInquiries = hardInquiries;
    }
    
    public int getCreditHistoryMonths() {
        return creditHistoryMonths;
    }
    
    public void setCreditHistoryMonths(int creditHistoryMonths) {
        this.creditHistoryMonths = creditHistoryMonths;
    }
    
    public boolean isHasBankruptcy() {
        return hasBankruptcy;
    }
    
    public void setHasBankruptcy(boolean hasBankruptcy) {
        this.hasBankruptcy = hasBankruptcy;
    }
    
    public boolean isHasForeclosure() {
        return hasForeclosure;
    }
    
    public void setHasForeclosure(boolean hasForeclosure) {
        this.hasForeclosure = hasForeclosure;
    }
    
    public boolean isHasTaxLiens() {
        return hasTaxLiens;
    }
    
    public void setHasTaxLiens(boolean hasTaxLiens) {
        this.hasTaxLiens = hasTaxLiens;
    }
    
    public boolean isHasJudgments() {
        return hasJudgments;
    }
    
    public void setHasJudgments(boolean hasJudgments) {
        this.hasJudgments = hasJudgments;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * Calculates the credit utilization ratio based on total balance and total credit limit.
     * Credit utilization = (total balance / total credit limit) * 100
     */
    private void calculateCreditUtilization() {
        if (totalCreditLimit > 0) {
            this.creditUtilization = (totalBalance / totalCreditLimit) * 100;
        } else {
            this.creditUtilization = 0;
        }
    }
    
    /**
     * Checks if the credit score is excellent (740 or higher).
     * 
     * @return true if the score is excellent, false otherwise
     */
    public boolean isExcellentScore() {
        return score >= 740;
    }
    
    /**
     * Checks if the credit score is good (670-739).
     * 
     * @return true if the score is good, false otherwise
     */
    public boolean isGoodScore() {
        return score >= 670 && score < 740;
    }
    
    /**
     * Checks if the credit score is fair (580-669).
     * 
     * @return true if the score is fair, false otherwise
     */
    public boolean isFairScore() {
        return score >= 580 && score < 670;
    }
    
    /**
     * Checks if the credit score is poor (below 580).
     * 
     * @return true if the score is poor, false otherwise
     */
    public boolean isPoorScore() {
        return score < 580;
    }
    
    /**
     * Checks if the credit utilization is low (below 30%).
     * 
     * @return true if credit utilization is low, false otherwise
     */
    public boolean hasLowUtilization() {
        return creditUtilization < 30;
    }
    
    /**
     * Checks if the credit utilization is high (above 50%).
     * 
     * @return true if credit utilization is high, false otherwise
     */
    public boolean hasHighUtilization() {
        return creditUtilization > 50;
    }
    
    /**
     * Checks if the credit history is considered long (5+ years).
     * 
     * @return true if credit history is long, false otherwise
     */
    public boolean hasLongCreditHistory() {
        return creditHistoryMonths >= 60; // 5 years
    }
    
    /**
     * Checks if there are any negative items on the credit report.
     * 
     * @return true if there are negative items, false otherwise
     */
    public boolean hasNegativeItems() {
        return lateAccounts > 0 || collectionsAccounts > 0 || derogatoryMarks > 0 || 
               hasBankruptcy || hasForeclosure || hasTaxLiens || hasJudgments;
    }
    
    /**
     * Checks if there are too many recent inquiries (more than 3 in the last 2 years).
     * 
     * @return true if there are too many inquiries, false otherwise
     */
    public boolean hasTooManyInquiries() {
        return hardInquiries > 3;
    }
    
    /**
     * Calculates a risk factor based on credit attributes.
     * Higher values indicate higher risk.
     * 
     * @return A risk factor value between 0 and 100
     */
    public int calculateRiskFactor() {
        int riskFactor = 0;
        
        // Base risk from credit score (inverted scale)
        if (score < 580) {
            riskFactor += 40;
        } else if (score < 670) {
            riskFactor += 25;
        } else if (score < 740) {
            riskFactor += 10;
        }
        
        // Add risk for high utilization
        if (creditUtilization > 70) {
            riskFactor += 15;
        } else if (creditUtilization > 50) {
            riskFactor += 10;
        } else if (creditUtilization > 30) {
            riskFactor += 5;
        }
        
        // Add risk for negative items
        riskFactor += (lateAccounts * 3);
        riskFactor += (collectionsAccounts * 5);
        riskFactor += (derogatoryMarks * 5);
        
        // Add risk for bankruptcies and foreclosures
        if (hasBankruptcy) riskFactor += 15;
        if (hasForeclosure) riskFactor += 15;
        if (hasTaxLiens) riskFactor += 10;
        if (hasJudgments) riskFactor += 10;
        
        // Add risk for too many inquiries
        riskFactor += (hardInquiries * 2);
        
        // Reduce risk for long credit history
        if (creditHistoryMonths >= 120) { // 10+ years
            riskFactor -= 10;
        } else if (creditHistoryMonths >= 60) { // 5+ years
            riskFactor -= 5;
        }
        
        // Ensure risk factor is between 0 and 100
        return Math.max(0, Math.min(100, riskFactor));
    }
    
    @Override
    public String toString() {
        return "CreditScore{" +
                "id=" + id +
                ", applicant=" + (applicant != null ? applicant.getFullName() : "null") +
                ", score=" + score +
                ", bureau='" + bureau + '\'' +
                ", reportDate=" + reportDate +
                ", creditUtilization=" + creditUtilization +
                ", derogatoryMarks=" + derogatoryMarks +
                ", hasBankruptcy=" + hasBankruptcy +
                ", hasForeclosure=" + hasForeclosure +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditScore that = (CreditScore) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}