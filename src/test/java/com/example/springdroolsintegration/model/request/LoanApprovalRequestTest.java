package com.example.springdroolsintegration.model.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for LoanApprovalRequest DTO.
 * These tests verify that Bean Validation annotations work correctly.
 */
class LoanApprovalRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid LoanApprovalRequest should pass validation")
    void validRequestShouldPassValidation() {
        // Create a valid request
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                720,
                "MORTGAGE",
                250000.0,
                360
        );
        request.setEmail("john.smith@example.com");
        request.setPhoneNumber("5551234567");
        request.setAddress("123 Main St, Anytown, CA 12345");
        request.setEmploymentStatus("EMPLOYED");
        request.setLoanPurpose("HOME_PURCHASE");

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert no violations
        assertTrue(violations.isEmpty(), "Valid request should not have validation violations");
    }

    @Test
    @DisplayName("LoanApprovalRequest with blank first name should fail validation")
    void blankFirstNameShouldFailValidation() {
        // Create a request with blank first name
        LoanApprovalRequest request = new LoanApprovalRequest(
                "",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                720,
                "MORTGAGE",
                250000.0,
                360
        );

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with blank first name should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("firstName", violation.getPropertyPath().toString(), "Violation should be on firstName field");
        assertEquals("First name is required", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with too short first name should fail validation")
    void tooShortFirstNameShouldFailValidation() {
        // Create a request with too short first name
        LoanApprovalRequest request = new LoanApprovalRequest(
                "J",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                720,
                "MORTGAGE",
                250000.0,
                360
        );

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with too short first name should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("firstName", violation.getPropertyPath().toString(), "Violation should be on firstName field");
        assertEquals("First name must be between 2 and 50 characters", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with blank last name should fail validation")
    void blankLastNameShouldFailValidation() {
        // Create a request with blank last name
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                720,
                "MORTGAGE",
                250000.0,
                360
        );

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with blank last name should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("lastName", violation.getPropertyPath().toString(), "Violation should be on lastName field");
        assertEquals("Last name is required", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with future date of birth should fail validation")
    void futureDateOfBirthShouldFailValidation() {
        // Create a request with future date of birth
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.now().plusYears(1),
                "123-45-6789",
                75000.0,
                720,
                "MORTGAGE",
                250000.0,
                360
        );

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with future date of birth should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("dateOfBirth", violation.getPropertyPath().toString(), "Violation should be on dateOfBirth field");
        assertEquals("Date of birth must be in the past", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with invalid SSN format should fail validation")
    void invalidSsnFormatShouldFailValidation() {
        // Create a request with invalid SSN format
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "12345-6789", // Invalid format
                75000.0,
                720,
                "MORTGAGE",
                250000.0,
                360
        );

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with invalid SSN format should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("ssn", violation.getPropertyPath().toString(), "Violation should be on ssn field");
        assertEquals("SSN must be in format XXX-XX-XXXX", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with invalid email should fail validation")
    void invalidEmailShouldFailValidation() {
        // Create a request with invalid email
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                720,
                "MORTGAGE",
                250000.0,
                360
        );
        request.setEmail("invalid-email");

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with invalid email should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString(), "Violation should be on email field");
        assertEquals("Email must be valid", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with invalid phone number should fail validation")
    void invalidPhoneNumberShouldFailValidation() {
        // Create a request with invalid phone number
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                720,
                "MORTGAGE",
                250000.0,
                360
        );
        request.setPhoneNumber("123"); // Too short

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with invalid phone number should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("phoneNumber", violation.getPropertyPath().toString(), "Violation should be on phoneNumber field");
        assertEquals("Phone number should be valid", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with negative annual income should fail validation")
    void negativeAnnualIncomeShouldFailValidation() {
        // Create a request with negative annual income
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                -75000.0, // Negative income
                720,
                "MORTGAGE",
                250000.0,
                360
        );

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with negative annual income should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("annualIncome", violation.getPropertyPath().toString(), "Violation should be on annualIncome field");
        assertEquals("Annual income must be non-negative", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with credit score below minimum should fail validation")
    void creditScoreBelowMinimumShouldFailValidation() {
        // Create a request with credit score below minimum
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                299, // Below minimum of 300
                "MORTGAGE",
                250000.0,
                360
        );

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with credit score below minimum should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("creditScore", violation.getPropertyPath().toString(), "Violation should be on creditScore field");
        assertEquals("Credit score must be at least 300", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with blank loan type should fail validation")
    void blankLoanTypeShouldFailValidation() {
        // Create a request with blank loan type
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                720,
                "", // Blank loan type
                250000.0,
                360
        );

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with blank loan type should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("loanType", violation.getPropertyPath().toString(), "Violation should be on loanType field");
        assertEquals("Loan type is required", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with negative loan amount should fail validation")
    void negativeLoanAmountShouldFailValidation() {
        // Create a request with negative loan amount
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                720,
                "MORTGAGE",
                -250000.0, // Negative loan amount
                360
        );

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with negative loan amount should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("loanAmount", violation.getPropertyPath().toString(), "Violation should be on loanAmount field");
        assertEquals("Loan amount must be positive", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with zero loan term should fail validation")
    void zeroLoanTermShouldFailValidation() {
        // Create a request with zero loan term
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                720,
                "MORTGAGE",
                250000.0,
                0 // Zero loan term
        );

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with zero loan term should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("loanTermMonths", violation.getPropertyPath().toString(), "Violation should be on loanTermMonths field");
        assertEquals("Loan term must be at least 1 month", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("LoanApprovalRequest with negative interest rate should fail validation")
    void negativeInterestRateShouldFailValidation() {
        // Create a request with negative interest rate
        LoanApprovalRequest request = new LoanApprovalRequest(
                "John",
                "Smith",
                LocalDate.of(1985, 7, 15),
                "123-45-6789",
                75000.0,
                720,
                "MORTGAGE",
                250000.0,
                360
        );
        request.setInterestRate(-4.5); // Negative interest rate

        // Validate the request
        Set<ConstraintViolation<LoanApprovalRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with negative interest rate should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<LoanApprovalRequest> violation = violations.iterator().next();
        assertEquals("interestRate", violation.getPropertyPath().toString(), "Violation should be on interestRate field");
        assertEquals("Interest rate cannot be negative", violation.getMessage(), "Violation message should match");
    }
}