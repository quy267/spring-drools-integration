package com.example.springdroolsintegration.mapper;

import com.example.springdroolsintegration.model.dto.LoanApprovalResponse;
import com.example.springdroolsintegration.model.entity.Applicant;
import com.example.springdroolsintegration.model.entity.CreditScore;
import com.example.springdroolsintegration.model.entity.LoanApplication;
import com.example.springdroolsintegration.model.request.LoanApprovalRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

/**
 * MapStruct mapper for Loan Approval System entities and DTOs.
 * This interface defines mapping methods between Loan entities (Applicant, LoanApplication, CreditScore)
 * and DTOs (LoanApprovalRequest, LoanApprovalResponse).
 */
@Mapper(componentModel = "spring")
public interface LoanMapper {
    
    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);
    
    /**
     * Converts a LoanApprovalRequest to an Applicant entity.
     * 
     * @param request The loan approval request
     * @return The applicant entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "ssn", source = "ssn")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "city", ignore = true) // Not available in request
    @Mapping(target = "state", ignore = true) // Not available in request
    @Mapping(target = "zipCode", ignore = true) // Not available in request
    @Mapping(target = "country", ignore = true) // Not available in request
    @Mapping(target = "employmentStatus", source = "employmentStatus")
    @Mapping(target = "employerName", source = "employerName")
    @Mapping(target = "jobTitle", ignore = true) // Not available in request
    @Mapping(target = "yearsAtEmployer", source = "yearsAtEmployer")
    @Mapping(target = "annualIncome", source = "annualIncome")
    @Mapping(target = "monthlyIncome", expression = "java(request.getAnnualIncome() / 12)")
    @Mapping(target = "monthlyDebtPayments", source = "monthlyDebtPayments")
    @Mapping(target = "creditScore", source = "creditScore")
    @Mapping(target = "creditInquiries", source = "creditInquiries")
    @Mapping(target = "delinquencies", constant = "0") // Not available in request
    @Mapping(target = "bankruptcyHistory", source = "hasBankruptcy")
    @Mapping(target = "foreclosureHistory", source = "hasForeclosure")
    @Mapping(target = "firstTimeHomeBuyer", ignore = true) // Not available in request
    @Mapping(target = "usCitizen", ignore = true) // Not available in request
    Applicant requestToApplicant(LoanApprovalRequest request);
    
    /**
     * Converts a LoanApprovalRequest to a co-applicant entity.
     * This method is used when the request has a co-applicant.
     * 
     * @param request The loan approval request
     * @return The co-applicant entity, or null if no co-applicant
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", source = "coApplicantFirstName")
    @Mapping(target = "lastName", source = "coApplicantLastName")
    @Mapping(target = "dateOfBirth", source = "coApplicantDateOfBirth")
    @Mapping(target = "ssn", source = "coApplicantSsn")
    @Mapping(target = "email", ignore = true) // Not available in request
    @Mapping(target = "phoneNumber", ignore = true) // Not available in request
    @Mapping(target = "address", ignore = true) // Not available in request
    @Mapping(target = "city", ignore = true) // Not available in request
    @Mapping(target = "state", ignore = true) // Not available in request
    @Mapping(target = "zipCode", ignore = true) // Not available in request
    @Mapping(target = "country", ignore = true) // Not available in request
    @Mapping(target = "employmentStatus", source = "coApplicantEmploymentStatus")
    @Mapping(target = "employerName", ignore = true) // Not available in request
    @Mapping(target = "jobTitle", ignore = true) // Not available in request
    @Mapping(target = "yearsAtEmployer", constant = "0") // Not available in request
    @Mapping(target = "annualIncome", source = "coApplicantAnnualIncome")
    @Mapping(target = "monthlyIncome", expression = "java(request.getCoApplicantAnnualIncome() / 12)")
    @Mapping(target = "monthlyDebtPayments", source = "coApplicantMonthlyDebtPayments")
    @Mapping(target = "creditScore", source = "coApplicantCreditScore")
    @Mapping(target = "creditInquiries", constant = "0") // Not available in request
    @Mapping(target = "delinquencies", constant = "0") // Not available in request
    @Mapping(target = "bankruptcyHistory", constant = "false") // Not available in request
    @Mapping(target = "foreclosureHistory", constant = "false") // Not available in request
    @Mapping(target = "firstTimeHomeBuyer", ignore = true) // Not available in request
    @Mapping(target = "usCitizen", ignore = true) // Not available in request
    Applicant requestToCoApplicant(LoanApprovalRequest request);
    
    /**
     * Converts a LoanApprovalRequest to a CreditScore entity.
     * 
     * @param request The loan approval request
     * @param applicant The applicant entity
     * @return The credit score entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", source = "applicant")
    @Mapping(target = "score", source = "request.creditScore")
    @Mapping(target = "bureau", source = "request.creditBureau")
    @Mapping(target = "reportDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "scoreModel", constant = "FICO 8") // Default value
    @Mapping(target = "openAccounts", constant = "0") // Not available in request
    @Mapping(target = "totalCreditLimit", constant = "0") // Not available in request
    @Mapping(target = "totalBalance", constant = "0") // Not available in request
    @Mapping(target = "creditUtilization", constant = "0") // Not available in request
    @Mapping(target = "onTimeAccounts", constant = "0") // Not available in request
    @Mapping(target = "lateAccounts", constant = "0") // Not available in request
    @Mapping(target = "collectionsAccounts", constant = "0") // Not available in request
    @Mapping(target = "derogatoryMarks", constant = "0") // Not available in request
    @Mapping(target = "hardInquiries", source = "request.creditInquiries")
    @Mapping(target = "creditHistoryMonths", constant = "0") // Not available in request
    @Mapping(target = "hasBankruptcy", source = "request.hasBankruptcy")
    @Mapping(target = "hasForeclosure", source = "request.hasForeclosure")
    @Mapping(target = "hasTaxLiens", constant = "false") // Not available in request
    @Mapping(target = "hasJudgments", constant = "false") // Not available in request
    @Mapping(target = "notes", ignore = true) // Not available in request
    CreditScore requestToCreditScore(LoanApprovalRequest request, Applicant applicant);
    
    /**
     * Converts a LoanApprovalRequest to a LoanApplication entity.
     * 
     * @param request The loan approval request
     * @param applicant The primary applicant entity
     * @param coApplicant The co-applicant entity (can be null)
     * @return The loan application entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicationNumber", expression = "java(generateApplicationNumber())")
    @Mapping(target = "applicant", source = "applicant")
    @Mapping(target = "coApplicant", source = "coApplicant")
    @Mapping(target = "loanType", source = "request.loanType")
    @Mapping(target = "loanPurpose", source = "request.loanPurpose")
    @Mapping(target = "loanAmount", source = "request.loanAmount")
    @Mapping(target = "loanTermMonths", source = "request.loanTermMonths")
    @Mapping(target = "interestRate", source = "request.interestRate")
    @Mapping(target = "applicationDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "propertyAddress", source = "request.propertyAddress")
    @Mapping(target = "propertyValue", source = "request.propertyValue")
    @Mapping(target = "loanToValueRatio", expression = "java(calculateLTV(request))")
    @Mapping(target = "downPayment", source = "request.downPayment")
    @Mapping(target = "vehicleMake", source = "request.vehicleMake")
    @Mapping(target = "vehicleModel", source = "request.vehicleModel")
    @Mapping(target = "vehicleYear", source = "request.vehicleYear")
    @Mapping(target = "vehicleValue", source = "request.vehicleValue")
    @Mapping(target = "monthlyPayment", expression = "java(calculateMonthlyPayment(request))")
    @Mapping(target = "debtToIncomeRatio", expression = "java(calculateDTI(request))")
    @Mapping(target = "riskScore", constant = "0") // Will be calculated by rules
    @Mapping(target = "approved", constant = "false") // Will be determined by rules
    @Mapping(target = "decisionReason", ignore = true) // Will be determined by rules
    @Mapping(target = "decisionDate", ignore = true) // Will be determined by rules
    @Mapping(target = "underwriterId", ignore = true) // Will be determined by rules
    LoanApplication requestToLoanApplication(LoanApprovalRequest request, Applicant applicant, Applicant coApplicant);
    
    /**
     * Converts a LoanApplication entity to a LoanApprovalResponse DTO.
     * 
     * @param application The loan application entity
     * @return The loan approval response DTO
     */
    @Mapping(target = "applicationNumber", source = "applicationNumber")
    @Mapping(target = "applicantName", expression = "java(application.getApplicant().getFullName())")
    @Mapping(target = "coApplicantName", expression = "java(application.hasCoApplicant() ? application.getCoApplicant().getFullName() : null)")
    @Mapping(target = "loanType", source = "loanType")
    @Mapping(target = "loanPurpose", source = "loanPurpose")
    @Mapping(target = "loanAmount", source = "loanAmount")
    @Mapping(target = "loanTermMonths", source = "loanTermMonths")
    @Mapping(target = "interestRate", source = "interestRate")
    @Mapping(target = "monthlyPayment", source = "monthlyPayment")
    @Mapping(target = "approved", source = "approved")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "decisionReason", source = "decisionReason")
    @Mapping(target = "decisionFactors", ignore = true) // Will be populated separately
    @Mapping(target = "riskScore", source = "riskScore")
    @Mapping(target = "riskCategory", expression = "java(getRiskCategory(application.getRiskScore()))")
    @Mapping(target = "debtToIncomeRatio", source = "debtToIncomeRatio")
    @Mapping(target = "loanToValueRatio", source = "loanToValueRatio")
    @Mapping(target = "approvedAmount", expression = "java(application.isApproved() ? application.getLoanAmount() : 0)")
    @Mapping(target = "conditions", ignore = true) // Will be populated separately
    @Mapping(target = "requiredDocuments", ignore = true) // Will be populated separately
    @Mapping(target = "alternativeOptions", ignore = true) // Will be populated separately
    @Mapping(target = "appliedRules", ignore = true) // Will be populated separately
    @Mapping(target = "decisionDate", source = "decisionDate")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "offerExpirationDate", expression = "java(getOfferExpirationDate(application))")
    @Mapping(target = "notes", source = "notes")
    LoanApprovalResponse loanApplicationToResponse(LoanApplication application);
    
    /**
     * Updates an existing Applicant entity with data from a LoanApprovalRequest.
     * 
     * @param request The loan approval request
     * @param applicant The applicant entity to update
     * @return The updated applicant entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "ssn", source = "ssn")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "employmentStatus", source = "employmentStatus")
    @Mapping(target = "employerName", source = "employerName")
    @Mapping(target = "yearsAtEmployer", source = "yearsAtEmployer")
    @Mapping(target = "annualIncome", source = "annualIncome")
    @Mapping(target = "monthlyIncome", expression = "java(request.getAnnualIncome() / 12)")
    @Mapping(target = "monthlyDebtPayments", source = "monthlyDebtPayments")
    @Mapping(target = "creditScore", source = "creditScore")
    @Mapping(target = "creditInquiries", source = "creditInquiries")
    @Mapping(target = "bankruptcyHistory", source = "hasBankruptcy")
    @Mapping(target = "foreclosureHistory", source = "hasForeclosure")
    Applicant updateApplicant(LoanApprovalRequest request, @MappingTarget Applicant applicant);
    
    /**
     * Updates an existing LoanApplication entity with data from a LoanApprovalRequest.
     * 
     * @param request The loan approval request
     * @param application The loan application entity to update
     * @return The updated loan application entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicationNumber", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    @Mapping(target = "coApplicant", ignore = true)
    @Mapping(target = "loanType", source = "loanType")
    @Mapping(target = "loanPurpose", source = "loanPurpose")
    @Mapping(target = "loanAmount", source = "loanAmount")
    @Mapping(target = "loanTermMonths", source = "loanTermMonths")
    @Mapping(target = "interestRate", source = "interestRate")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "propertyAddress", source = "propertyAddress")
    @Mapping(target = "propertyValue", source = "propertyValue")
    @Mapping(target = "loanToValueRatio", expression = "java(calculateLTV(request))")
    @Mapping(target = "downPayment", source = "downPayment")
    @Mapping(target = "vehicleMake", source = "vehicleMake")
    @Mapping(target = "vehicleModel", source = "vehicleModel")
    @Mapping(target = "vehicleYear", source = "vehicleYear")
    @Mapping(target = "vehicleValue", source = "vehicleValue")
    @Mapping(target = "monthlyPayment", expression = "java(calculateMonthlyPayment(request))")
    @Mapping(target = "debtToIncomeRatio", expression = "java(calculateDTI(request))")
    @Mapping(target = "approved", ignore = true)
    @Mapping(target = "decisionReason", ignore = true)
    @Mapping(target = "decisionDate", ignore = true)
    @Mapping(target = "underwriterId", ignore = true)
    LoanApplication updateLoanApplication(LoanApprovalRequest request, @MappingTarget LoanApplication application);
    
    /**
     * Generates a unique application number.
     * This is a simple implementation that uses the current timestamp.
     * In a real application, this would be more sophisticated.
     * 
     * @return A unique application number
     */
    @Named("generateApplicationNumber")
    default String generateApplicationNumber() {
        return "LOAN" + System.currentTimeMillis();
    }
    
    /**
     * Calculates the loan-to-value ratio for a loan request.
     * 
     * @param request The loan approval request
     * @return The loan-to-value ratio
     */
    @Named("calculateLTV")
    default double calculateLTV(LoanApprovalRequest request) {
        if ("MORTGAGE".equalsIgnoreCase(request.getLoanType()) && request.getPropertyValue() > 0) {
            return (request.getLoanAmount() / request.getPropertyValue()) * 100;
        }
        return 0;
    }
    
    /**
     * Calculates the monthly payment for a loan request.
     * 
     * @param request The loan approval request
     * @return The monthly payment
     */
    @Named("calculateMonthlyPayment")
    default double calculateMonthlyPayment(LoanApprovalRequest request) {
        if (request.getLoanAmount() <= 0 || request.getLoanTermMonths() <= 0) {
            return 0;
        }
        
        double monthlyRate = request.getInterestRate() / 12 / 100;
        
        if (monthlyRate == 0) {
            // Simple division for zero-interest loans
            return request.getLoanAmount() / request.getLoanTermMonths();
        } else {
            // Standard amortization formula
            double denominator = 1 - Math.pow(1 + monthlyRate, -request.getLoanTermMonths());
            return (monthlyRate * request.getLoanAmount()) / denominator;
        }
    }
    
    /**
     * Calculates the debt-to-income ratio for a loan request.
     * 
     * @param request The loan approval request
     * @return The debt-to-income ratio
     */
    @Named("calculateDTI")
    default double calculateDTI(LoanApprovalRequest request) {
        return request.calculateDebtToIncomeRatio();
    }
    
    /**
     * Gets the risk category based on the risk score.
     * 
     * @param riskScore The risk score
     * @return The risk category
     */
    @Named("getRiskCategory")
    default String getRiskCategory(int riskScore) {
        if (riskScore < 30) {
            return "LOW";
        } else if (riskScore < 60) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }
    
    /**
     * Gets the offer expiration date for an approved loan application.
     * 
     * @param application The loan application
     * @return The offer expiration date, or null if not approved
     */
    @Named("getOfferExpirationDate")
    default LocalDate getOfferExpirationDate(LoanApplication application) {
        if (application.isApproved() && application.getDecisionDate() != null) {
            return application.getDecisionDate().plusDays(30);
        }
        return null;
    }
}