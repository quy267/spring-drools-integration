package com.example.springdroolsintegration.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request model for loan approval rule execution.
 * This DTO captures all input data needed for evaluating loan approval rules.
 */
@Schema(description = "Request model for loan approval rule evaluation")
public class LoanApprovalRequest {
    
    // Applicant Information
    
    /**
     * First name of the primary applicant
     */
    @Schema(description = "First name of the primary applicant", example = "John")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    /**
     * Last name of the primary applicant
     */
    @Schema(description = "Last name of the primary applicant", example = "Smith")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    /**
     * Date of birth of the primary applicant
     */
    @Schema(description = "Date of birth of the primary applicant", example = "1985-07-15")
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    /**
     * Social Security Number of the primary applicant
     */
    @Schema(description = "Social Security Number of the primary applicant", example = "123-45-6789")
    @NotBlank(message = "SSN is required")
    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{4}$", message = "SSN must be in format XXX-XX-XXXX")
    private String ssn;
    
    /**
     * Email address of the primary applicant
     */
    @Schema(description = "Email address of the primary applicant", example = "john.smith@example.com")
    @Email(message = "Email must be valid")
    private String email;
    
    /**
     * Phone number of the primary applicant
     */
    @Schema(description = "Phone number of the primary applicant", example = "5551234567")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;
    
    /**
     * Current address of the primary applicant
     */
    @Schema(description = "Current address of the primary applicant", example = "123 Main St, Anytown, CA 12345")
    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;
    
    /**
     * Employment status of the primary applicant
     */
    @Schema(description = "Employment status of the primary applicant", example = "EMPLOYED")
    @NotBlank(message = "Employment status is required")
    private String employmentStatus;
    
    /**
     * Current employer name of the primary applicant
     */
    @Schema(description = "Current employer name of the primary applicant", example = "Acme Corporation")
    private String employerName;
    
    /**
     * Years at current employer for the primary applicant
     */
    @Schema(description = "Years at current employer for the primary applicant", example = "5.5")
    @Min(value = 0, message = "Years at employer must be non-negative")
    private double yearsAtEmployer;
    
    /**
     * Annual income of the primary applicant
     */
    @Schema(description = "Annual income of the primary applicant", example = "75000")
    @DecimalMin(value = "0.0", message = "Annual income must be non-negative")
    private double annualIncome;
    
    /**
     * Monthly debt payments of the primary applicant (excluding the requested loan)
     */
    @Schema(description = "Monthly debt payments of the primary applicant", example = "1500")
    @DecimalMin(value = "0.0", message = "Monthly debt payments must be non-negative")
    private double monthlyDebtPayments;
    
    // Co-Applicant Information (Optional)
    
    /**
     * Whether there is a co-applicant for the loan
     */
    @Schema(description = "Whether there is a co-applicant for the loan", example = "false")
    private boolean hasCoApplicant;
    
    /**
     * First name of the co-applicant
     */
    @Schema(description = "First name of the co-applicant", example = "Jane")
    @Size(min = 2, max = 50, message = "Co-applicant first name must be between 2 and 50 characters")
    private String coApplicantFirstName;
    
    /**
     * Last name of the co-applicant
     */
    @Schema(description = "Last name of the co-applicant", example = "Smith")
    @Size(min = 2, max = 50, message = "Co-applicant last name must be between 2 and 50 characters")
    private String coApplicantLastName;
    
    /**
     * Date of birth of the co-applicant
     */
    @Schema(description = "Date of birth of the co-applicant", example = "1987-03-20")
    @Past(message = "Co-applicant date of birth must be in the past")
    private LocalDate coApplicantDateOfBirth;
    
    /**
     * Social Security Number of the co-applicant
     */
    @Schema(description = "Social Security Number of the co-applicant", example = "987-65-4321")
    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{4}$", message = "Co-applicant SSN must be in format XXX-XX-XXXX")
    private String coApplicantSsn;
    
    /**
     * Employment status of the co-applicant
     */
    @Schema(description = "Employment status of the co-applicant", example = "EMPLOYED")
    private String coApplicantEmploymentStatus;
    
    /**
     * Annual income of the co-applicant
     */
    @Schema(description = "Annual income of the co-applicant", example = "65000")
    @DecimalMin(value = "0.0", message = "Co-applicant annual income must be non-negative")
    private double coApplicantAnnualIncome;
    
    /**
     * Monthly debt payments of the co-applicant (excluding the requested loan)
     */
    @Schema(description = "Monthly debt payments of the co-applicant", example = "1200")
    @DecimalMin(value = "0.0", message = "Co-applicant monthly debt payments must be non-negative")
    private double coApplicantMonthlyDebtPayments;
    
    // Credit Information
    
    /**
     * Credit score of the primary applicant
     */
    @Schema(description = "Credit score of the primary applicant", example = "720")
    @Min(value = 300, message = "Credit score must be at least 300")
    private int creditScore;
    
    /**
     * Credit bureau that provided the score
     */
    @Schema(description = "Credit bureau that provided the score", example = "EXPERIAN")
    private String creditBureau;
    
    /**
     * Number of credit inquiries in the last 6 months
     */
    @Schema(description = "Number of credit inquiries in the last 6 months", example = "2")
    @Min(value = 0, message = "Credit inquiries must be non-negative")
    private int creditInquiries;
    
    /**
     * Whether the applicant has a bankruptcy in the last 7 years
     */
    @Schema(description = "Whether the applicant has a bankruptcy in the last 7 years", example = "false")
    private boolean hasBankruptcy;
    
    /**
     * Whether the applicant has a foreclosure in the last 7 years
     */
    @Schema(description = "Whether the applicant has a foreclosure in the last 7 years", example = "false")
    private boolean hasForeclosure;
    
    /**
     * Credit score of the co-applicant (if applicable)
     */
    @Schema(description = "Credit score of the co-applicant", example = "740")
    @Min(value = 300, message = "Co-applicant credit score must be at least 300")
    private Integer coApplicantCreditScore;
    
    // Loan Information
    
    /**
     * Type of loan (e.g., MORTGAGE, AUTO, PERSONAL, STUDENT, BUSINESS)
     */
    @Schema(description = "Type of loan", example = "MORTGAGE")
    @NotBlank(message = "Loan type is required")
    private String loanType;
    
    /**
     * Purpose of the loan (e.g., HOME_PURCHASE, REFINANCE, EDUCATION, DEBT_CONSOLIDATION)
     */
    @Schema(description = "Purpose of the loan", example = "HOME_PURCHASE")
    @NotBlank(message = "Loan purpose is required")
    private String loanPurpose;
    
    /**
     * Requested loan amount in base currency
     */
    @Schema(description = "Requested loan amount", example = "250000")
    @DecimalMin(value = "1.0", message = "Loan amount must be positive")
    private double loanAmount;
    
    /**
     * Requested loan term in months
     */
    @Schema(description = "Requested loan term in months", example = "360")
    @Min(value = 1, message = "Loan term must be at least 1 month")
    private int loanTermMonths;
    
    /**
     * Interest rate for the loan (percentage)
     */
    @Schema(description = "Interest rate for the loan (percentage)", example = "4.5")
    @DecimalMin(value = "0.0", message = "Interest rate cannot be negative")
    private double interestRate;
    
    // Property Information (for mortgage loans)
    
    /**
     * Property address (for mortgage loans)
     */
    @Schema(description = "Property address (for mortgage loans)", example = "456 Oak St, Anytown, CA 12345")
    private String propertyAddress;
    
    /**
     * Property value (for mortgage loans)
     */
    @Schema(description = "Property value (for mortgage loans)", example = "300000")
    @DecimalMin(value = "0.0", message = "Property value must be non-negative")
    private double propertyValue;
    
    /**
     * Down payment amount (for mortgage loans)
     */
    @Schema(description = "Down payment amount (for mortgage loans)", example = "60000")
    @DecimalMin(value = "0.0", message = "Down payment must be non-negative")
    private double downPayment;
    
    // Vehicle Information (for auto loans)
    
    /**
     * Vehicle make (for auto loans)
     */
    @Schema(description = "Vehicle make (for auto loans)", example = "Toyota")
    private String vehicleMake;
    
    /**
     * Vehicle model (for auto loans)
     */
    @Schema(description = "Vehicle model (for auto loans)", example = "Camry")
    private String vehicleModel;
    
    /**
     * Vehicle year (for auto loans)
     */
    @Schema(description = "Vehicle year (for auto loans)", example = "2023")
    @Min(value = 1900, message = "Vehicle year must be valid")
    private Integer vehicleYear;
    
    /**
     * Vehicle value (for auto loans)
     */
    @Schema(description = "Vehicle value (for auto loans)", example = "25000")
    @DecimalMin(value = "0.0", message = "Vehicle value must be non-negative")
    private Double vehicleValue;
    
    /**
     * Default constructor
     */
    public LoanApprovalRequest() {
    }
    
    /**
     * Constructor with essential fields for a basic personal loan
     *
     * @param firstName The applicant's first name
     * @param lastName The applicant's last name
     * @param dateOfBirth The applicant's date of birth
     * @param ssn The applicant's SSN
     * @param annualIncome The applicant's annual income
     * @param creditScore The applicant's credit score
     * @param loanType The type of loan
     * @param loanAmount The requested loan amount
     * @param loanTermMonths The requested loan term in months
     */
    public LoanApprovalRequest(String firstName, String lastName, LocalDate dateOfBirth, 
                              String ssn, double annualIncome, int creditScore,
                              String loanType, double loanAmount, int loanTermMonths) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.ssn = ssn;
        this.annualIncome = annualIncome;
        this.creditScore = creditScore;
        this.loanType = loanType;
        this.loanAmount = loanAmount;
        this.loanTermMonths = loanTermMonths;
        this.hasCoApplicant = false;
    }
    
    // Getters and setters
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getSsn() {
        return ssn;
    }
    
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getEmploymentStatus() {
        return employmentStatus;
    }
    
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
    
    public String getEmployerName() {
        return employerName;
    }
    
    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }
    
    public double getYearsAtEmployer() {
        return yearsAtEmployer;
    }
    
    public void setYearsAtEmployer(double yearsAtEmployer) {
        this.yearsAtEmployer = yearsAtEmployer;
    }
    
    public double getAnnualIncome() {
        return annualIncome;
    }
    
    public void setAnnualIncome(double annualIncome) {
        this.annualIncome = annualIncome;
    }
    
    public double getMonthlyDebtPayments() {
        return monthlyDebtPayments;
    }
    
    public void setMonthlyDebtPayments(double monthlyDebtPayments) {
        this.monthlyDebtPayments = monthlyDebtPayments;
    }
    
    public boolean isHasCoApplicant() {
        return hasCoApplicant;
    }
    
    public void setHasCoApplicant(boolean hasCoApplicant) {
        this.hasCoApplicant = hasCoApplicant;
    }
    
    public String getCoApplicantFirstName() {
        return coApplicantFirstName;
    }
    
    public void setCoApplicantFirstName(String coApplicantFirstName) {
        this.coApplicantFirstName = coApplicantFirstName;
    }
    
    public String getCoApplicantLastName() {
        return coApplicantLastName;
    }
    
    public void setCoApplicantLastName(String coApplicantLastName) {
        this.coApplicantLastName = coApplicantLastName;
    }
    
    public LocalDate getCoApplicantDateOfBirth() {
        return coApplicantDateOfBirth;
    }
    
    public void setCoApplicantDateOfBirth(LocalDate coApplicantDateOfBirth) {
        this.coApplicantDateOfBirth = coApplicantDateOfBirth;
    }
    
    public String getCoApplicantSsn() {
        return coApplicantSsn;
    }
    
    public void setCoApplicantSsn(String coApplicantSsn) {
        this.coApplicantSsn = coApplicantSsn;
    }
    
    public String getCoApplicantEmploymentStatus() {
        return coApplicantEmploymentStatus;
    }
    
    public void setCoApplicantEmploymentStatus(String coApplicantEmploymentStatus) {
        this.coApplicantEmploymentStatus = coApplicantEmploymentStatus;
    }
    
    public double getCoApplicantAnnualIncome() {
        return coApplicantAnnualIncome;
    }
    
    public void setCoApplicantAnnualIncome(double coApplicantAnnualIncome) {
        this.coApplicantAnnualIncome = coApplicantAnnualIncome;
    }
    
    public double getCoApplicantMonthlyDebtPayments() {
        return coApplicantMonthlyDebtPayments;
    }
    
    public void setCoApplicantMonthlyDebtPayments(double coApplicantMonthlyDebtPayments) {
        this.coApplicantMonthlyDebtPayments = coApplicantMonthlyDebtPayments;
    }
    
    public int getCreditScore() {
        return creditScore;
    }
    
    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }
    
    public String getCreditBureau() {
        return creditBureau;
    }
    
    public void setCreditBureau(String creditBureau) {
        this.creditBureau = creditBureau;
    }
    
    public int getCreditInquiries() {
        return creditInquiries;
    }
    
    public void setCreditInquiries(int creditInquiries) {
        this.creditInquiries = creditInquiries;
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
    
    public Integer getCoApplicantCreditScore() {
        return coApplicantCreditScore;
    }
    
    public void setCoApplicantCreditScore(Integer coApplicantCreditScore) {
        this.coApplicantCreditScore = coApplicantCreditScore;
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
    
    public Integer getVehicleYear() {
        return vehicleYear;
    }
    
    public void setVehicleYear(Integer vehicleYear) {
        this.vehicleYear = vehicleYear;
    }
    
    public Double getVehicleValue() {
        return vehicleValue;
    }
    
    public void setVehicleValue(Double vehicleValue) {
        this.vehicleValue = vehicleValue;
    }
    
    /**
     * Calculates the loan-to-value ratio for mortgage loans.
     * LTV = (Loan Amount / Property Value) * 100
     * 
     * @return The loan-to-value ratio as a percentage, or 0 if not applicable
     */
    public double calculateLoanToValueRatio() {
        if ("MORTGAGE".equalsIgnoreCase(loanType) && propertyValue > 0) {
            return (loanAmount / propertyValue) * 100;
        }
        return 0;
    }
    
    /**
     * Calculates the debt-to-income ratio.
     * DTI = (Monthly Debt Payments / Monthly Income) * 100
     * 
     * @return The debt-to-income ratio as a percentage, or 0 if not applicable
     */
    public double calculateDebtToIncomeRatio() {
        double monthlyIncome = annualIncome / 12;
        
        if (hasCoApplicant) {
            monthlyIncome += coApplicantAnnualIncome / 12;
        }
        
        if (monthlyIncome <= 0) {
            return 0;
        }
        
        // Estimate monthly payment for this loan (simplified calculation)
        double monthlyRate = interestRate / 12 / 100;
        double monthlyPayment;
        
        if (monthlyRate == 0) {
            monthlyPayment = loanAmount / loanTermMonths;
        } else {
            double denominator = 1 - Math.pow(1 + monthlyRate, -loanTermMonths);
            monthlyPayment = (monthlyRate * loanAmount) / denominator;
        }
        
        // Calculate total monthly debt including this loan
        double totalMonthlyDebt = monthlyDebtPayments + monthlyPayment;
        
        if (hasCoApplicant) {
            totalMonthlyDebt += coApplicantMonthlyDebtPayments;
        }
        
        return (totalMonthlyDebt / monthlyIncome) * 100;
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
     * Gets the full name of the primary applicant.
     * 
     * @return The full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Gets the full name of the co-applicant if present.
     * 
     * @return The full name of the co-applicant, or null if no co-applicant
     */
    public String getCoApplicantFullName() {
        if (hasCoApplicant && coApplicantFirstName != null && coApplicantLastName != null) {
            return coApplicantFirstName + " " + coApplicantLastName;
        }
        return null;
    }
    
    /**
     * Calculates the age of the primary applicant based on the date of birth.
     * 
     * @return The age in years
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
    
    /**
     * Calculates the age of the co-applicant based on the date of birth.
     * 
     * @return The age in years, or 0 if no co-applicant
     */
    public int getCoApplicantAge() {
        if (!hasCoApplicant || coApplicantDateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - coApplicantDateOfBirth.getYear();
    }
    
    /**
     * Calculates the combined annual income of the primary applicant and co-applicant.
     * 
     * @return The combined annual income
     */
    public double getCombinedAnnualIncome() {
        if (hasCoApplicant) {
            return annualIncome + coApplicantAnnualIncome;
        }
        return annualIncome;
    }
    
    /**
     * Calculates the combined monthly income of the primary applicant and co-applicant.
     * 
     * @return The combined monthly income
     */
    public double getCombinedMonthlyIncome() {
        return getCombinedAnnualIncome() / 12;
    }
    
    /**
     * Checks if the primary applicant has stable employment (2 or more years at current employer).
     * 
     * @return true if the applicant has stable employment, false otherwise
     */
    public boolean hasStableEmployment() {
        return yearsAtEmployer >= 2;
    }
    
    /**
     * Checks if the primary applicant has a good credit score (700 or higher).
     * 
     * @return true if the credit score is good, false otherwise
     */
    public boolean hasGoodCredit() {
        return creditScore >= 700;
    }
    
    /**
     * Checks if the primary applicant has a fair credit score (between 640 and 699).
     * 
     * @return true if the credit score is fair, false otherwise
     */
    public boolean hasFairCredit() {
        return creditScore >= 640 && creditScore < 700;
    }
    
    /**
     * Checks if the primary applicant has a poor credit score (below 640).
     * 
     * @return true if the credit score is poor, false otherwise
     */
    public boolean hasPoorCredit() {
        return creditScore < 640;
    }
    
    @Override
    public String toString() {
        return "LoanApprovalRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", loanType='" + loanType + '\'' +
                ", loanAmount=" + loanAmount +
                ", loanTermMonths=" + loanTermMonths +
                ", creditScore=" + creditScore +
                ", hasCoApplicant=" + hasCoApplicant +
                '}';
    }
}