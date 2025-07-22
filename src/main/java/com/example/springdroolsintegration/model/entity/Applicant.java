package com.example.springdroolsintegration.model.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Applicant entity for the loan approval rules use case.
 * This entity represents a loan applicant in the system and contains all relevant
 * personal and financial information needed for loan approval.
 */
public class Applicant {
    
    /**
     * Unique identifier for the applicant
     */
    private Long id;
    
    /**
     * First name of the applicant
     */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    /**
     * Last name of the applicant
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    /**
     * Date of birth of the applicant
     */
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    /**
     * Social Security Number or Tax ID of the applicant
     */
    @NotBlank(message = "SSN/Tax ID is required")
    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{4}$", message = "SSN must be in format XXX-XX-XXXX")
    private String ssn;
    
    /**
     * Email address of the applicant
     */
    @Email(message = "Email must be valid")
    private String email;
    
    /**
     * Phone number of the applicant
     */
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;
    
    /**
     * Current address of the applicant
     */
    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;
    
    /**
     * City of residence
     */
    @NotBlank(message = "City is required")
    private String city;
    
    /**
     * State or province of residence
     */
    @NotBlank(message = "State is required")
    private String state;
    
    /**
     * Postal code
     */
    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Zip code must be in format XXXXX or XXXXX-XXXX")
    private String zipCode;
    
    /**
     * Country of residence
     */
    @NotBlank(message = "Country is required")
    private String country;
    
    /**
     * Employment status (e.g., EMPLOYED, SELF_EMPLOYED, UNEMPLOYED, RETIRED)
     */
    @NotBlank(message = "Employment status is required")
    private String employmentStatus;
    
    /**
     * Current employer name
     */
    private String employerName;
    
    /**
     * Job title or position
     */
    private String jobTitle;
    
    /**
     * Years at current employer
     */
    @Min(value = 0, message = "Years at employer must be non-negative")
    private double yearsAtEmployer;
    
    /**
     * Annual income in base currency
     */
    @Min(value = 0, message = "Annual income must be non-negative")
    private double annualIncome;
    
    /**
     * Monthly income in base currency
     */
    @Min(value = 0, message = "Monthly income must be non-negative")
    private double monthlyIncome;
    
    /**
     * Total monthly debt payments (excluding the requested loan)
     */
    @Min(value = 0, message = "Monthly debt payments must be non-negative")
    private double monthlyDebtPayments;
    
    /**
     * Credit score of the applicant
     */
    @Min(value = 300, message = "Credit score must be at least 300")
    private int creditScore;
    
    /**
     * Number of credit inquiries in the last 6 months
     */
    @Min(value = 0, message = "Credit inquiries must be non-negative")
    private int creditInquiries;
    
    /**
     * Number of delinquencies in the last 2 years
     */
    @Min(value = 0, message = "Delinquencies must be non-negative")
    private int delinquencies;
    
    /**
     * Whether the applicant has filed for bankruptcy in the last 7 years
     */
    private boolean bankruptcyHistory;
    
    /**
     * Whether the applicant has a foreclosure in the last 7 years
     */
    private boolean foreclosureHistory;
    
    /**
     * Whether the applicant is a first-time homebuyer (for home loans)
     */
    private boolean firstTimeHomeBuyer;
    
    /**
     * Whether the applicant is a US citizen
     */
    private boolean usCitizen;
    
    /**
     * Default constructor
     */
    public Applicant() {
    }
    
    /**
     * Constructor with essential fields
     *
     * @param firstName The applicant's first name
     * @param lastName The applicant's last name
     * @param dateOfBirth The applicant's date of birth
     * @param ssn The applicant's SSN
     * @param annualIncome The applicant's annual income
     * @param creditScore The applicant's credit score
     */
    public Applicant(String firstName, String lastName, LocalDate dateOfBirth, 
                    String ssn, double annualIncome, int creditScore) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.ssn = ssn;
        this.annualIncome = annualIncome;
        this.monthlyIncome = annualIncome / 12;
        this.creditScore = creditScore;
    }
    
    /**
     * Full constructor with all fields
     *
     * @param id The applicant's ID
     * @param firstName The applicant's first name
     * @param lastName The applicant's last name
     * @param dateOfBirth The applicant's date of birth
     * @param ssn The applicant's SSN
     * @param email The applicant's email
     * @param phoneNumber The applicant's phone number
     * @param address The applicant's address
     * @param city The applicant's city
     * @param state The applicant's state
     * @param zipCode The applicant's zip code
     * @param country The applicant's country
     * @param employmentStatus The applicant's employment status
     * @param employerName The applicant's employer name
     * @param jobTitle The applicant's job title
     * @param yearsAtEmployer The applicant's years at current employer
     * @param annualIncome The applicant's annual income
     * @param monthlyIncome The applicant's monthly income
     * @param monthlyDebtPayments The applicant's monthly debt payments
     * @param creditScore The applicant's credit score
     * @param creditInquiries The applicant's credit inquiries
     * @param delinquencies The applicant's delinquencies
     * @param bankruptcyHistory Whether the applicant has bankruptcy history
     * @param foreclosureHistory Whether the applicant has foreclosure history
     * @param firstTimeHomeBuyer Whether the applicant is a first-time homebuyer
     * @param usCitizen Whether the applicant is a US citizen
     */
    public Applicant(Long id, String firstName, String lastName, LocalDate dateOfBirth, 
                    String ssn, String email, String phoneNumber, String address, 
                    String city, String state, String zipCode, String country, 
                    String employmentStatus, String employerName, String jobTitle, 
                    double yearsAtEmployer, double annualIncome, double monthlyIncome, 
                    double monthlyDebtPayments, int creditScore, int creditInquiries, 
                    int delinquencies, boolean bankruptcyHistory, boolean foreclosureHistory, 
                    boolean firstTimeHomeBuyer, boolean usCitizen) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.ssn = ssn;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.employmentStatus = employmentStatus;
        this.employerName = employerName;
        this.jobTitle = jobTitle;
        this.yearsAtEmployer = yearsAtEmployer;
        this.annualIncome = annualIncome;
        this.monthlyIncome = monthlyIncome;
        this.monthlyDebtPayments = monthlyDebtPayments;
        this.creditScore = creditScore;
        this.creditInquiries = creditInquiries;
        this.delinquencies = delinquencies;
        this.bankruptcyHistory = bankruptcyHistory;
        this.foreclosureHistory = foreclosureHistory;
        this.firstTimeHomeBuyer = firstTimeHomeBuyer;
        this.usCitizen = usCitizen;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
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
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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
        this.monthlyIncome = annualIncome / 12;
    }
    
    public double getMonthlyIncome() {
        return monthlyIncome;
    }
    
    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
        this.annualIncome = monthlyIncome * 12;
    }
    
    public double getMonthlyDebtPayments() {
        return monthlyDebtPayments;
    }
    
    public void setMonthlyDebtPayments(double monthlyDebtPayments) {
        this.monthlyDebtPayments = monthlyDebtPayments;
    }
    
    public int getCreditScore() {
        return creditScore;
    }
    
    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }
    
    public int getCreditInquiries() {
        return creditInquiries;
    }
    
    public void setCreditInquiries(int creditInquiries) {
        this.creditInquiries = creditInquiries;
    }
    
    public int getDelinquencies() {
        return delinquencies;
    }
    
    public void setDelinquencies(int delinquencies) {
        this.delinquencies = delinquencies;
    }
    
    public boolean isBankruptcyHistory() {
        return bankruptcyHistory;
    }
    
    public void setBankruptcyHistory(boolean bankruptcyHistory) {
        this.bankruptcyHistory = bankruptcyHistory;
    }
    
    public boolean isForeclosureHistory() {
        return foreclosureHistory;
    }
    
    public void setForeclosureHistory(boolean foreclosureHistory) {
        this.foreclosureHistory = foreclosureHistory;
    }
    
    public boolean isFirstTimeHomeBuyer() {
        return firstTimeHomeBuyer;
    }
    
    public void setFirstTimeHomeBuyer(boolean firstTimeHomeBuyer) {
        this.firstTimeHomeBuyer = firstTimeHomeBuyer;
    }
    
    public boolean isUsCitizen() {
        return usCitizen;
    }
    
    public void setUsCitizen(boolean usCitizen) {
        this.usCitizen = usCitizen;
    }
    
    /**
     * Gets the full name of the applicant.
     * 
     * @return The full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Calculates the age of the applicant based on the date of birth.
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
     * Calculates the debt-to-income ratio of the applicant.
     * DTI = Monthly Debt Payments / Monthly Income
     * 
     * @return The debt-to-income ratio
     */
    public double calculateDebtToIncomeRatio() {
        if (monthlyIncome <= 0) {
            return 0;
        }
        return monthlyDebtPayments / monthlyIncome;
    }
    
    /**
     * Checks if the applicant has a good credit score (700 or higher).
     * 
     * @return true if the credit score is good, false otherwise
     */
    public boolean hasGoodCredit() {
        return creditScore >= 700;
    }
    
    /**
     * Checks if the applicant has a fair credit score (between 640 and 699).
     * 
     * @return true if the credit score is fair, false otherwise
     */
    public boolean hasFairCredit() {
        return creditScore >= 640 && creditScore < 700;
    }
    
    /**
     * Checks if the applicant has a poor credit score (below 640).
     * 
     * @return true if the credit score is poor, false otherwise
     */
    public boolean hasPoorCredit() {
        return creditScore < 640;
    }
    
    /**
     * Checks if the applicant has stable employment (2 or more years at current employer).
     * 
     * @return true if the applicant has stable employment, false otherwise
     */
    public boolean hasStableEmployment() {
        return yearsAtEmployer >= 2;
    }
    
    @Override
    public String toString() {
        return "Applicant{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", ssn='" + ssn + '\'' +
                ", email='" + email + '\'' +
                ", employmentStatus='" + employmentStatus + '\'' +
                ", annualIncome=" + annualIncome +
                ", creditScore=" + creditScore +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Applicant applicant = (Applicant) o;
        return Objects.equals(id, applicant.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}