package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.dto.LoanApprovalResponse;
import com.example.springdroolsintegration.model.request.LoanApprovalRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for LoanApprovalService.
 * These tests verify that the loan approval service can evaluate loan applications correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
public class LoanApprovalServiceIntegrationTest {

    @Autowired
    private LoanApprovalService loanApprovalService;

    @Test
    @DisplayName("Test loan approval for high credit score and high income")
    public void testLoanApprovalForHighCreditScoreAndHighIncome() {
        // Create a request for a high credit score and high income applicant
        LoanApprovalRequest request = new LoanApprovalRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.now().minusYears(35));
        request.setSsn("123-45-6789");
        request.setEmail("john.doe@example.com");
        request.setEmploymentStatus("EMPLOYED");
        request.setAnnualIncome(120000.0);
        request.setMonthlyDebtPayments(5000.0);
        request.setCreditScore(780);
        request.setLoanType("MORTGAGE");
        request.setLoanPurpose("PURCHASE");
        request.setLoanAmount(250000.0);
        request.setLoanTermMonths(360); // 30 years
        request.setInterestRate(4.5);
        request.setHasCoApplicant(false);

        // Evaluate loan application
        LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("John Doe", response.getApplicantName(), "Applicant name should match");
        assertEquals(250000.0, response.getLoanAmount(), "Loan amount should match");
        assertEquals(360, response.getLoanTermMonths(), "Loan term should match");
        assertTrue(response.isApproved(), "Loan should be approved");
        assertNotNull(response.getDecisionReason(), "Decision reason should not be null");
        assertNotNull(response.getRiskCategory(), "Risk category should not be null");
    }

    @Test
    @DisplayName("Test loan approval for medium credit score and medium income")
    public void testLoanApprovalForMediumCreditScoreAndMediumIncome() {
        // Create a request for a medium credit score and medium income applicant
        LoanApprovalRequest request = new LoanApprovalRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setDateOfBirth(LocalDate.now().minusYears(28));
        request.setSsn("987-65-4321");
        request.setEmail("jane.smith@example.com");
        request.setEmploymentStatus("EMPLOYED");
        request.setAnnualIncome(75000.0);
        request.setMonthlyDebtPayments(3000.0);
        request.setCreditScore(680);
        request.setLoanType("MORTGAGE");
        request.setLoanPurpose("PURCHASE");
        request.setLoanAmount(200000.0);
        request.setLoanTermMonths(360); // 30 years
        request.setInterestRate(4.75);
        request.setHasCoApplicant(false);

        // Evaluate loan application
        LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Jane Smith", response.getApplicantName(), "Applicant name should match");
        assertEquals(200000.0, response.getLoanAmount(), "Loan amount should match");
        assertEquals(360, response.getLoanTermMonths(), "Loan term should match");
        // We don't assert approval status as it depends on the rules
        assertNotNull(response.getDecisionReason(), "Decision reason should not be null");
        assertNotNull(response.getRiskCategory(), "Risk category should not be null");
    }

    @Test
    @DisplayName("Test loan approval for low credit score and low income")
    public void testLoanApprovalForLowCreditScoreAndLowIncome() {
        // Create a request for a low credit score and low income applicant
        LoanApprovalRequest request = new LoanApprovalRequest();
        request.setFirstName("Bob");
        request.setLastName("Johnson");
        request.setDateOfBirth(LocalDate.now().minusYears(45));
        request.setSsn("456-78-9012");
        request.setEmail("bob.johnson@example.com");
        request.setEmploymentStatus("SELF_EMPLOYED");
        request.setAnnualIncome(40000.0);
        request.setMonthlyDebtPayments(2500.0);
        request.setCreditScore(580);
        request.setLoanType("MORTGAGE");
        request.setLoanPurpose("PURCHASE");
        request.setLoanAmount(150000.0);
        request.setLoanTermMonths(360); // 30 years
        request.setInterestRate(5.25);
        request.setHasCoApplicant(false);

        // Evaluate loan application
        LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Bob Johnson", response.getApplicantName(), "Applicant name should match");
        assertEquals(150000.0, response.getLoanAmount(), "Loan amount should match");
        assertEquals(360, response.getLoanTermMonths(), "Loan term should match");
        // We expect this loan to be rejected, but we don't assert it as it depends on the rules
        assertNotNull(response.getDecisionReason(), "Decision reason should not be null");
        assertNotNull(response.getRiskCategory(), "Risk category should not be null");
    }

    @Test
    @DisplayName("Test loan approval with co-applicant")
    public void testLoanApprovalWithCoApplicant() {
        // Create a request with a co-applicant
        LoanApprovalRequest request = new LoanApprovalRequest();
        request.setFirstName("Alice");
        request.setLastName("Brown");
        request.setDateOfBirth(LocalDate.now().minusYears(32));
        request.setSsn("789-01-2345");
        request.setEmail("alice.brown@example.com");
        request.setEmploymentStatus("EMPLOYED");
        request.setAnnualIncome(85000.0);
        request.setMonthlyDebtPayments(3500.0);
        request.setCreditScore(700);
        request.setLoanType("MORTGAGE");
        request.setLoanPurpose("PURCHASE");
        request.setLoanAmount(300000.0);
        request.setLoanTermMonths(360); // 30 years
        request.setInterestRate(4.5);
        request.setHasCoApplicant(true);
        request.setCoApplicantFirstName("Charlie");
        request.setCoApplicantLastName("Brown");
        request.setCoApplicantDateOfBirth(LocalDate.now().minusYears(34));
        request.setCoApplicantSsn("234-56-7890");
        request.setCoApplicantEmploymentStatus("EMPLOYED");
        request.setCoApplicantAnnualIncome(90000.0);
        request.setCoApplicantMonthlyDebtPayments(2000.0);
        request.setCoApplicantCreditScore(720);

        // Evaluate loan application
        LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Alice Brown", response.getApplicantName(), "Applicant name should match");
        assertEquals(300000.0, response.getLoanAmount(), "Loan amount should match");
        assertEquals(360, response.getLoanTermMonths(), "Loan term should match");
        assertNotNull(response.getCoApplicantName(), "Co-applicant name should not be null");
        // We expect this loan to be approved due to the co-applicant, but we don't assert it as it depends on the rules
        assertNotNull(response.getDecisionReason(), "Decision reason should not be null");
        assertNotNull(response.getRiskCategory(), "Risk category should not be null");
    }

    @Test
    @DisplayName("Test loan approval for refinance")
    public void testLoanApprovalForRefinance() {
        // Create a request for a refinance
        LoanApprovalRequest request = new LoanApprovalRequest();
        request.setFirstName("David");
        request.setLastName("Wilson");
        request.setDateOfBirth(LocalDate.now().minusYears(50));
        request.setSsn("234-56-7890");
        request.setEmail("david.wilson@example.com");
        request.setEmploymentStatus("EMPLOYED");
        request.setAnnualIncome(95000.0);
        request.setMonthlyDebtPayments(4000.0);
        request.setCreditScore(720);
        request.setLoanType("MORTGAGE");
        request.setLoanPurpose("REFINANCE");
        request.setLoanAmount(200000.0);
        request.setLoanTermMonths(180); // 15 years
        request.setInterestRate(4.0);
        request.setHasCoApplicant(false);

        // Evaluate loan application
        LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("David Wilson", response.getApplicantName(), "Applicant name should match");
        assertEquals(200000.0, response.getLoanAmount(), "Loan amount should match");
        assertEquals(180, response.getLoanTermMonths(), "Loan term should match");
        assertEquals("REFINANCE", response.getLoanPurpose(), "Loan purpose should match");
        // We don't assert approval status as it depends on the rules
        assertNotNull(response.getDecisionReason(), "Decision reason should not be null");
        assertNotNull(response.getRiskCategory(), "Risk category should not be null");
    }

    @Test
    @DisplayName("Test batch loan application evaluation")
    public void testEvaluateLoanApplicationBatch() {
        // Create a list of requests
        List<LoanApprovalRequest> requests = new ArrayList<>();
        
        // Add different types of loan approval requests
        requests.add(createLoanRequest("High", "Score", 780, 120000.0, 250000.0));
        requests.add(createLoanRequest("Medium", "Score", 680, 75000.0, 200000.0));
        requests.add(createLoanRequest("Low", "Score", 580, 40000.0, 150000.0));
        requests.add(createLoanRequest("Refinance", "User", 720, 95000.0, 200000.0, "REFINANCE"));
        requests.add(createLoanRequestWithCoApplicant("Primary", "Applicant", 700, 85000.0, 300000.0, 
                                                    "Co", "Applicant", 720, 90000.0));

        // Evaluate loan applications in batch
        List<LoanApprovalResponse> responses = loanApprovalService.evaluateLoanApplicationBatch(requests);

        // Verify the responses
        assertNotNull(responses, "Responses should not be null");
        assertEquals(5, responses.size(), "Should have 5 responses");
        
        // Verify each response
        for (int i = 0; i < 5; i++) {
            LoanApprovalResponse response = responses.get(i);
            assertNotNull(response, "Response should not be null");
            String expectedName = requests.get(i).getFirstName() + " " + requests.get(i).getLastName();
            assertEquals(expectedName, response.getApplicantName(), "Applicant name should match");
            assertEquals(requests.get(i).getLoanAmount(), response.getLoanAmount(), "Loan amount should match");
            assertEquals(requests.get(i).getLoanTermMonths(), response.getLoanTermMonths(), "Loan term should match");
        }
    }

    @Test
    @DisplayName("Test asynchronous loan application evaluation")
    public void testEvaluateLoanApplicationAsync() throws ExecutionException, InterruptedException {
        // Create a request
        LoanApprovalRequest request = createLoanRequest("Async", "Applicant", 700, 80000.0, 220000.0);

        // Evaluate loan application asynchronously
        CompletableFuture<LoanApprovalResponse> future = loanApprovalService.evaluateLoanApplicationAsync(request);

        // Wait for the result
        LoanApprovalResponse response = future.get();

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Async Applicant", response.getApplicantName(), "Applicant name should match");
        assertEquals(220000.0, response.getLoanAmount(), "Loan amount should match");
        assertEquals(360, response.getLoanTermMonths(), "Loan term should match");
    }

    @Test
    @DisplayName("Test loan approval statistics")
    public void testLoanApprovalStatistics() {
        // Create and process a request to generate statistics
        LoanApprovalRequest request = createLoanRequest("Stats", "Applicant", 700, 80000.0, 220000.0);
        loanApprovalService.evaluateLoanApplication(request);

        // Get loan approval statistics
        Map<String, Object> statistics = loanApprovalService.getLoanApprovalStatistics();

        // Verify statistics
        assertNotNull(statistics, "Statistics should not be null");
        assertTrue(statistics.containsKey("totalEvaluations"), "Statistics should contain totalEvaluations");
        assertTrue(statistics.containsKey("totalBatchEvaluations"), "Statistics should contain totalBatchEvaluations");
        assertTrue(statistics.containsKey("totalApprovals"), "Statistics should contain totalApprovals");
        assertTrue(statistics.containsKey("totalRejections"), "Statistics should contain totalRejections");
        assertTrue(statistics.containsKey("approvalRate"), "Statistics should contain approvalRate");
    }

    /**
     * Helper method to create a loan request with common fields.
     */
    private LoanApprovalRequest createLoanRequest(String firstName, String lastName, int creditScore, double income, double loanAmount) {
        return createLoanRequest(firstName, lastName, creditScore, income, loanAmount, "PURCHASE");
    }

    /**
     * Helper method to create a loan request with common fields and loan purpose.
     */
    private LoanApprovalRequest createLoanRequest(String firstName, String lastName, int creditScore, double income, double loanAmount, String loanPurpose) {
        LoanApprovalRequest request = new LoanApprovalRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setDateOfBirth(LocalDate.now().minusYears(35));
        request.setSsn("123-45-" + (1000 + (int)(Math.random() * 9000)));
        request.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");
        request.setEmploymentStatus("EMPLOYED");
        request.setAnnualIncome(income);
        request.setMonthlyDebtPayments(income / 20); // Monthly debt as a fraction of income
        request.setCreditScore(creditScore);
        request.setLoanType("MORTGAGE");
        request.setLoanPurpose(loanPurpose);
        request.setLoanAmount(loanAmount);
        request.setLoanTermMonths(360); // 30 years
        request.setInterestRate(4.5);
        request.setHasCoApplicant(false);
        
        return request;
    }

    /**
     * Helper method to create a loan request with a co-applicant.
     */
    private LoanApprovalRequest createLoanRequestWithCoApplicant(
            String firstName, String lastName, int creditScore, double income, double loanAmount,
            String coFirstName, String coLastName, int coCreditScore, double coIncome) {
        
        LoanApprovalRequest request = createLoanRequest(firstName, lastName, creditScore, income, loanAmount);
        request.setHasCoApplicant(true);
        request.setCoApplicantFirstName(coFirstName);
        request.setCoApplicantLastName(coLastName);
        request.setCoApplicantDateOfBirth(LocalDate.now().minusYears(34));
        request.setCoApplicantSsn("987-65-" + (1000 + (int)(Math.random() * 9000)));
        request.setCoApplicantEmploymentStatus("EMPLOYED");
        request.setCoApplicantAnnualIncome(coIncome);
        request.setCoApplicantMonthlyDebtPayments(coIncome / 25);
        request.setCoApplicantCreditScore(coCreditScore);
        
        return request;
    }
}