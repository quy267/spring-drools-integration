package com.example.springdroolsintegration.controller;

import com.example.springdroolsintegration.model.request.LoanApprovalRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for LoanApprovalController.
 * These tests verify that the loan approval endpoints work correctly.
 */
public class LoanApprovalControllerTest extends BaseApiIntegrationTest {
    
    @Test
    @DisplayName("Test evaluate loan application endpoint with a valid request")
    public void testEvaluateLoanApplication() throws Exception {
        // Create a test request
        LoanApprovalRequest request = createLoanRequest("John", "Doe", 720, 75000.0, 250000.0);
        
        // Execute the request
        ResultActions result = performPost("/api/v1/loans/evaluate", request);
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.applicantName", is("John Doe")))
              .andExpect(jsonPath("$.loanType", is("MORTGAGE")))
              .andExpect(jsonPath("$.loanAmount", is(250000.0)))
              .andExpect(jsonPath("$.loanTermMonths", is(360)))
              .andExpect(jsonPath("$.approved", notNullValue()))
              .andExpect(jsonPath("$.status", notNullValue()))
              .andExpect(jsonPath("$.decisionReason", notNullValue()))
              .andExpect(jsonPath("$.riskCategory", notNullValue()))
              .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
    
    @Test
    @DisplayName("Test evaluate loan application endpoint with an invalid request")
    public void testEvaluateLoanApplicationWithInvalidRequest() throws Exception {
        // Create an invalid request (missing required fields)
        LoanApprovalRequest request = new LoanApprovalRequest();
        
        // Execute the request
        ResultActions result = performPost("/api/v1/loans/evaluate", request);
        
        // Verify the response (should be 400 Bad Request due to validation)
        result.andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Test batch loan application evaluation endpoint with valid requests")
    public void testEvaluateLoanApplicationBatch() throws Exception {
        // Create a list of test requests
        List<LoanApprovalRequest> requests = new ArrayList<>();
        requests.add(createLoanRequest("John", "Doe", 720, 75000.0, 250000.0));
        requests.add(createLoanRequest("Jane", "Smith", 680, 60000.0, 200000.0));
        requests.add(createLoanRequest("Bob", "Johnson", 620, 50000.0, 150000.0));
        
        // Execute the request
        ResultActions result = performPost("/api/v1/loans/batch", requests, "page", "0", "size", "10");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(3)))
              .andExpect(jsonPath("$.content[0].applicantName", is("John Doe")))
              .andExpect(jsonPath("$.content[1].applicantName", is("Jane Smith")))
              .andExpect(jsonPath("$.content[2].applicantName", is("Bob Johnson")))
              .andExpect(jsonPath("$.page", is(0)))
              .andExpect(jsonPath("$.size", is(10)))
              .andExpect(jsonPath("$.totalElements", is(3)))
              .andExpect(jsonPath("$.totalPages", is(1)))
              .andExpect(jsonPath("$.first", is(true)))
              .andExpect(jsonPath("$.last", is(true)));
    }
    
    @Test
    @DisplayName("Test batch loan application evaluation endpoint with pagination")
    public void testEvaluateLoanApplicationBatchWithPagination() throws Exception {
        // Create a list of test requests
        List<LoanApprovalRequest> requests = new ArrayList<>();
        requests.add(createLoanRequest("John", "Doe", 720, 75000.0, 250000.0));
        requests.add(createLoanRequest("Jane", "Smith", 680, 60000.0, 200000.0));
        requests.add(createLoanRequest("Bob", "Johnson", 620, 50000.0, 150000.0));
        requests.add(createLoanRequest("Alice", "Brown", 700, 70000.0, 220000.0));
        requests.add(createLoanRequest("David", "Wilson", 650, 55000.0, 180000.0));
        
        // Execute the request with page=0, size=2
        ResultActions result = performPost("/api/v1/loans/batch", requests, "page", "0", "size", "2");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(2)))
              .andExpect(jsonPath("$.content[0].applicantName", is("John Doe")))
              .andExpect(jsonPath("$.content[1].applicantName", is("Jane Smith")))
              .andExpect(jsonPath("$.page", is(0)))
              .andExpect(jsonPath("$.size", is(2)))
              .andExpect(jsonPath("$.totalElements", is(5)))
              .andExpect(jsonPath("$.totalPages", is(3)))
              .andExpect(jsonPath("$.first", is(true)))
              .andExpect(jsonPath("$.last", is(false)));
        
        // Execute the request with page=1, size=2
        result = performPost("/api/v1/loans/batch", requests, "page", "1", "size", "2");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(2)))
              .andExpect(jsonPath("$.content[0].applicantName", is("Bob Johnson")))
              .andExpect(jsonPath("$.content[1].applicantName", is("Alice Brown")))
              .andExpect(jsonPath("$.page", is(1)))
              .andExpect(jsonPath("$.size", is(2)))
              .andExpect(jsonPath("$.totalElements", is(5)))
              .andExpect(jsonPath("$.totalPages", is(3)))
              .andExpect(jsonPath("$.first", is(false)))
              .andExpect(jsonPath("$.last", is(false)));
    }
    
    @Test
    @DisplayName("Test async loan application evaluation endpoint")
    public void testEvaluateLoanApplicationAsync() throws Exception {
        // Create a test request
        LoanApprovalRequest request = createLoanRequest("Async", "Applicant", 700, 65000.0, 210000.0);
        
        // Execute the request
        ResultActions result = performPost("/api/v1/loans/async", request);
        
        // Verify the response (should be 202 Accepted)
        result.andExpect(status().isAccepted());
    }
    
    @Test
    @DisplayName("Test get loan approval statistics endpoint")
    public void testGetLoanApprovalStatistics() throws Exception {
        // Execute the request
        ResultActions result = performGet("/api/v1/loans/statistics");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.totalEvaluations", notNullValue()))
              .andExpect(jsonPath("$.totalBatchEvaluations", notNullValue()))
              .andExpect(jsonPath("$.totalApprovals", notNullValue()))
              .andExpect(jsonPath("$.totalRejections", notNullValue()))
              .andExpect(jsonPath("$.approvalRate", notNullValue()));
    }
    
    /**
     * Helper method to create a loan request for testing.
     */
    private LoanApprovalRequest createLoanRequest(String firstName, String lastName, int creditScore, 
                                                 double income, double loanAmount) {
        LoanApprovalRequest request = new LoanApprovalRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setDateOfBirth(LocalDate.now().minusYears(35));
        request.setSsn("123-45-" + (1000 + creditScore % 9000));
        request.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");
        request.setAddress("123 Main St, Anytown, CA 12345");
        request.setEmploymentStatus("EMPLOYED");
        request.setEmployerName("Acme Corporation");
        request.setYearsAtEmployer(5.0);
        request.setAnnualIncome(income);
        request.setMonthlyDebtPayments(income / 20); // Monthly debt as a fraction of income
        request.setCreditScore(creditScore);
        request.setCreditBureau("EXPERIAN");
        request.setCreditInquiries(2);
        request.setHasBankruptcy(false);
        request.setHasForeclosure(false);
        request.setLoanType("MORTGAGE");
        request.setLoanPurpose("PURCHASE");
        request.setLoanAmount(loanAmount);
        request.setLoanTermMonths(360); // 30 years
        request.setInterestRate(4.5);
        request.setHasCoApplicant(false);
        
        return request;
    }
}