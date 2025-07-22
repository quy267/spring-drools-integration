package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.dto.LoanApprovalResponse;
import com.example.springdroolsintegration.model.request.LoanApprovalRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for loan approval rule execution.
 * This service handles the execution of loan approval rules for loan applications.
 */
public interface LoanApprovalService {
    
    /**
     * Evaluates a loan application based on rules.
     * 
     * @param request The loan approval request
     * @return The loan approval response with approval decision
     */
    LoanApprovalResponse evaluateLoanApplication(LoanApprovalRequest request);
    
    /**
     * Evaluates multiple loan applications in batch.
     * 
     * @param requests The list of loan approval requests
     * @return The list of loan approval responses with approval decisions
     */
    List<LoanApprovalResponse> evaluateLoanApplicationBatch(List<LoanApprovalRequest> requests);
    
    /**
     * Asynchronously evaluates a loan application.
     * 
     * @param request The loan approval request
     * @return A CompletableFuture that will complete with the loan approval response
     */
    CompletableFuture<LoanApprovalResponse> evaluateLoanApplicationAsync(LoanApprovalRequest request);
    
    /**
     * Gets statistics about loan approval rule executions.
     * 
     * @return A map of statistics about loan approval rule executions
     */
    Map<String, Object> getLoanApprovalStatistics();
}