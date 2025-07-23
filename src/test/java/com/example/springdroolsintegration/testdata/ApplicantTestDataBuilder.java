package com.example.springdroolsintegration.testdata;

import com.example.springdroolsintegration.model.entity.Applicant;

import java.time.LocalDate;

/**
 * Test data builder for Applicant entity following the builder pattern.
 * Provides fluent API for creating Applicant instances with default values for testing.
 */
public class ApplicantTestDataBuilder {
    
    private Applicant applicant;
    
    private ApplicantTestDataBuilder() {
        this.applicant = new Applicant();
        // Set default values for testing
        this.applicant.setId(1L);
        this.applicant.setFirstName("John");
        this.applicant.setLastName("Smith");
        this.applicant.setDateOfBirth(LocalDate.of(1985, 6, 15));
        this.applicant.setSsn("123-45-6789");
        this.applicant.setEmail("john.smith@example.com");
        this.applicant.setPhoneNumber("555-0123");
        this.applicant.setAddress("123 Main St");
        this.applicant.setCity("Anytown");
        this.applicant.setState("CA");
        this.applicant.setZipCode("12345");
        this.applicant.setCountry("USA");
        this.applicant.setEmploymentStatus("EMPLOYED");
        this.applicant.setEmployerName("Tech Corp");
        this.applicant.setJobTitle("Software Engineer");
        this.applicant.setYearsAtEmployer(3.5);
        this.applicant.setAnnualIncome(75000.0);
        this.applicant.setMonthlyIncome(6250.0);
        this.applicant.setMonthlyDebtPayments(1500.0);
        this.applicant.setCreditScore(720);
        this.applicant.setCreditInquiries(2);
        this.applicant.setDelinquencies(0);
        this.applicant.setBankruptcyHistory(false);
        this.applicant.setForeclosureHistory(false);
        this.applicant.setFirstTimeHomeBuyer(true);
        this.applicant.setUsCitizen(true);
    }
    
    /**
     * Creates a new ApplicantTestDataBuilder instance with default values.
     * @return new ApplicantTestDataBuilder instance
     */
    public static ApplicantTestDataBuilder anApplicant() {
        return new ApplicantTestDataBuilder();
    }
    
    /**
     * Creates an ApplicantTestDataBuilder for an applicant with excellent credit (740+).
     * @return ApplicantTestDataBuilder with excellent credit defaults
     */
    public static ApplicantTestDataBuilder anExcellentCreditApplicant() {
        return new ApplicantTestDataBuilder()
                .withCreditScore(780)
                .withAnnualIncome(100000.0)
                .withMonthlyIncome(8333.0)
                .withMonthlyDebtPayments(1000.0)
                .withYearsAtEmployer(5.0)
                .withCreditInquiries(1)
                .withDelinquencies(0);
    }
    
    /**
     * Creates an ApplicantTestDataBuilder for an applicant with good credit (670-739).
     * @return ApplicantTestDataBuilder with good credit defaults
     */
    public static ApplicantTestDataBuilder aGoodCreditApplicant() {
        return new ApplicantTestDataBuilder()
                .withCreditScore(700)
                .withAnnualIncome(65000.0)
                .withMonthlyIncome(5416.0)
                .withMonthlyDebtPayments(1300.0)
                .withYearsAtEmployer(2.5)
                .withCreditInquiries(3)
                .withDelinquencies(0);
    }
    
    /**
     * Creates an ApplicantTestDataBuilder for an applicant with fair credit (580-669).
     * @return ApplicantTestDataBuilder with fair credit defaults
     */
    public static ApplicantTestDataBuilder aFairCreditApplicant() {
        return new ApplicantTestDataBuilder()
                .withCreditScore(620)
                .withAnnualIncome(45000.0)
                .withMonthlyIncome(3750.0)
                .withMonthlyDebtPayments(1800.0)
                .withYearsAtEmployer(1.5)
                .withCreditInquiries(5)
                .withDelinquencies(1);
    }
    
    /**
     * Creates an ApplicantTestDataBuilder for an applicant with poor credit (<580).
     * @return ApplicantTestDataBuilder with poor credit defaults
     */
    public static ApplicantTestDataBuilder aPoorCreditApplicant() {
        return new ApplicantTestDataBuilder()
                .withCreditScore(550)
                .withAnnualIncome(35000.0)
                .withMonthlyIncome(2916.0)
                .withMonthlyDebtPayments(2000.0)
                .withYearsAtEmployer(0.8)
                .withCreditInquiries(8)
                .withDelinquencies(3)
                .withBankruptcyHistory(true);
    }
    
    /**
     * Creates an ApplicantTestDataBuilder for a high-income applicant.
     * @return ApplicantTestDataBuilder with high income defaults
     */
    public static ApplicantTestDataBuilder aHighIncomeApplicant() {
        return new ApplicantTestDataBuilder()
                .withAnnualIncome(150000.0)
                .withMonthlyIncome(12500.0)
                .withMonthlyDebtPayments(2000.0)
                .withCreditScore(750)
                .withEmployerName("Fortune 500 Corp")
                .withJobTitle("Senior Manager")
                .withYearsAtEmployer(7.0);
    }
    
    /**
     * Creates an ApplicantTestDataBuilder for a first-time home buyer.
     * @return ApplicantTestDataBuilder with first-time buyer defaults
     */
    public static ApplicantTestDataBuilder aFirstTimeBuyer() {
        return new ApplicantTestDataBuilder()
                .withFirstTimeHomeBuyer(true)
                .withAge(28)
                .withCreditScore(680)
                .withAnnualIncome(55000.0)
                .withYearsAtEmployer(2.0);
    }
    
    public ApplicantTestDataBuilder withId(Long id) {
        this.applicant.setId(id);
        return this;
    }
    
    public ApplicantTestDataBuilder withFirstName(String firstName) {
        this.applicant.setFirstName(firstName);
        return this;
    }
    
    public ApplicantTestDataBuilder withLastName(String lastName) {
        this.applicant.setLastName(lastName);
        return this;
    }
    
    public ApplicantTestDataBuilder withDateOfBirth(LocalDate dateOfBirth) {
        this.applicant.setDateOfBirth(dateOfBirth);
        return this;
    }
    
    public ApplicantTestDataBuilder withAge(int age) {
        this.applicant.setDateOfBirth(LocalDate.now().minusYears(age));
        return this;
    }
    
    public ApplicantTestDataBuilder withSsn(String ssn) {
        this.applicant.setSsn(ssn);
        return this;
    }
    
    public ApplicantTestDataBuilder withEmail(String email) {
        this.applicant.setEmail(email);
        return this;
    }
    
    public ApplicantTestDataBuilder withPhoneNumber(String phoneNumber) {
        this.applicant.setPhoneNumber(phoneNumber);
        return this;
    }
    
    public ApplicantTestDataBuilder withAddress(String address) {
        this.applicant.setAddress(address);
        return this;
    }
    
    public ApplicantTestDataBuilder withCity(String city) {
        this.applicant.setCity(city);
        return this;
    }
    
    public ApplicantTestDataBuilder withState(String state) {
        this.applicant.setState(state);
        return this;
    }
    
    public ApplicantTestDataBuilder withZipCode(String zipCode) {
        this.applicant.setZipCode(zipCode);
        return this;
    }
    
    public ApplicantTestDataBuilder withCountry(String country) {
        this.applicant.setCountry(country);
        return this;
    }
    
    public ApplicantTestDataBuilder withEmploymentStatus(String employmentStatus) {
        this.applicant.setEmploymentStatus(employmentStatus);
        return this;
    }
    
    public ApplicantTestDataBuilder withEmployerName(String employerName) {
        this.applicant.setEmployerName(employerName);
        return this;
    }
    
    public ApplicantTestDataBuilder withJobTitle(String jobTitle) {
        this.applicant.setJobTitle(jobTitle);
        return this;
    }
    
    public ApplicantTestDataBuilder withYearsAtEmployer(double yearsAtEmployer) {
        this.applicant.setYearsAtEmployer(yearsAtEmployer);
        return this;
    }
    
    public ApplicantTestDataBuilder withAnnualIncome(double annualIncome) {
        this.applicant.setAnnualIncome(annualIncome);
        this.applicant.setMonthlyIncome(annualIncome / 12.0);
        return this;
    }
    
    public ApplicantTestDataBuilder withMonthlyIncome(double monthlyIncome) {
        this.applicant.setMonthlyIncome(monthlyIncome);
        return this;
    }
    
    public ApplicantTestDataBuilder withMonthlyDebtPayments(double monthlyDebtPayments) {
        this.applicant.setMonthlyDebtPayments(monthlyDebtPayments);
        return this;
    }
    
    public ApplicantTestDataBuilder withCreditScore(int creditScore) {
        this.applicant.setCreditScore(creditScore);
        return this;
    }
    
    public ApplicantTestDataBuilder withCreditInquiries(int creditInquiries) {
        this.applicant.setCreditInquiries(creditInquiries);
        return this;
    }
    
    public ApplicantTestDataBuilder withDelinquencies(int delinquencies) {
        this.applicant.setDelinquencies(delinquencies);
        return this;
    }
    
    public ApplicantTestDataBuilder withBankruptcyHistory(boolean bankruptcyHistory) {
        this.applicant.setBankruptcyHistory(bankruptcyHistory);
        return this;
    }
    
    public ApplicantTestDataBuilder withForeclosureHistory(boolean foreclosureHistory) {
        this.applicant.setForeclosureHistory(foreclosureHistory);
        return this;
    }
    
    public ApplicantTestDataBuilder withFirstTimeHomeBuyer(boolean firstTimeHomeBuyer) {
        this.applicant.setFirstTimeHomeBuyer(firstTimeHomeBuyer);
        return this;
    }
    
    public ApplicantTestDataBuilder withUsCitizen(boolean usCitizen) {
        this.applicant.setUsCitizen(usCitizen);
        return this;
    }
    
    /**
     * Sets the applicant as unemployed with no income.
     * @return ApplicantTestDataBuilder configured as unemployed
     */
    public ApplicantTestDataBuilder asUnemployed() {
        this.applicant.setEmploymentStatus("UNEMPLOYED");
        this.applicant.setEmployerName(null);
        this.applicant.setJobTitle(null);
        this.applicant.setYearsAtEmployer(0.0);
        this.applicant.setAnnualIncome(0.0);
        this.applicant.setMonthlyIncome(0.0);
        return this;
    }
    
    /**
     * Sets the applicant as self-employed.
     * @return ApplicantTestDataBuilder configured as self-employed
     */
    public ApplicantTestDataBuilder asSelfEmployed() {
        this.applicant.setEmploymentStatus("SELF_EMPLOYED");
        this.applicant.setEmployerName("Self-Employed");
        this.applicant.setJobTitle("Business Owner");
        this.applicant.setYearsAtEmployer(5.0);
        return this;
    }
    
    /**
     * Builds and returns the Applicant instance.
     * @return configured Applicant instance
     */
    public Applicant build() {
        return this.applicant;
    }
}