package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.exception.RuleExecutionException;
import com.example.springdroolsintegration.mapper.LoanMapper;
import com.example.springdroolsintegration.model.dto.LoanApprovalResponse;
import com.example.springdroolsintegration.model.entity.Applicant;
import com.example.springdroolsintegration.model.entity.CreditScore;
import com.example.springdroolsintegration.model.entity.LoanApplication;
import com.example.springdroolsintegration.model.request.LoanApprovalRequest;
import com.example.springdroolsintegration.service.LoanApprovalService;
import com.example.springdroolsintegration.service.RuleExecutionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementation of the LoanApprovalService interface.
 * This service handles the execution of loan approval rules for loan applications.
 */
@Service
public class LoanApprovalServiceImpl implements LoanApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(LoanApprovalServiceImpl.class);
    
    private final RuleExecutionService ruleExecutionService;
    private final LoanMapper loanMapper;
    
    // Statistics tracking
    private final ConcurrentHashMap<String, AtomicLong> approvalCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> rejectionCounts = new ConcurrentHashMap<>();
    private final AtomicLong totalEvaluations = new AtomicLong(0);
    private final AtomicLong totalBatchEvaluations = new AtomicLong(0);
    private final AtomicLong totalApprovals = new AtomicLong(0);
    private final AtomicLong totalRejections = new AtomicLong(0);
    
    /**
     * Constructor for LoanApprovalServiceImpl.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param ruleExecutionService The rule execution service
     * @param loanMapper The loan mapper
     */
    public LoanApprovalServiceImpl(RuleExecutionService ruleExecutionService, LoanMapper loanMapper) {
        this.ruleExecutionService = ruleExecutionService;
        this.loanMapper = loanMapper;
        
        logger.info("LoanApprovalService initialized");
    }
    
    @Override
    public LoanApprovalResponse evaluateLoanApplication(LoanApprovalRequest request) {
        if (request == null) {
            throw new RuleExecutionException("Cannot evaluate null loan application request");
        }
        
        String executionId = UUID.randomUUID().toString();
        MDC.put("executionId", executionId);
        
        logger.debug("Evaluating loan application for: {}, loan amount: {}, execution ID: {}", 
                request.getFullName(), request.getLoanAmount(), executionId);
        
        try {
            // Convert request to domain entities
            Applicant primaryApplicant = loanMapper.requestToApplicant(request);
            
            // Handle co-applicant if present
            Applicant coApplicant = null;
            if (request.isHasCoApplicant()) {
                coApplicant = loanMapper.requestToCoApplicant(request);
            }
            
            // Create credit score entity
            CreditScore creditScore = loanMapper.requestToCreditScore(request, primaryApplicant);
            
            // Create loan application entity
            LoanApplication loanApplication = loanMapper.requestToLoanApplication(request, primaryApplicant, coApplicant);
            
            // Create a facts list for rule execution
            List<Object> facts = new ArrayList<>();
            facts.add(primaryApplicant);
            if (coApplicant != null) {
                facts.add(coApplicant);
            }
            facts.add(creditScore);
            facts.add(loanApplication);
            
            // Execute rules on all facts
            for (Object fact : facts) {
                ruleExecutionService.executeRules(fact);
            }
            
            // Calculate debt-to-income ratio and update loan application
            loanApplication.calculateDebtToIncomeRatio();
            
            // Create response from the results
            LoanApprovalResponse response = loanMapper.loanApplicationToResponse(loanApplication);
            
            // Update statistics
            updateApprovalStatistics(loanApplication);
            totalEvaluations.incrementAndGet();
            
            logger.debug("Loan application evaluation completed. Approved: {}, Risk Category: {}, execution ID: {}", 
                    response.isApproved(), response.getRiskCategory(), executionId);
            
            return response;
        } catch (Exception e) {
            logger.error("Error evaluating loan application for: {}, execution ID: {}", 
                    request.getFullName(), executionId, e);
            throw new RuleExecutionException("Error evaluating loan application: " + e.getMessage(), e);
        } finally {
            MDC.remove("executionId");
        }
    }
    
    @Override
    public List<LoanApprovalResponse> evaluateLoanApplicationBatch(List<LoanApprovalRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new RuleExecutionException("Cannot evaluate null or empty loan application requests list");
        }
        
        logger.debug("Evaluating batch of {} loan applications", requests.size());
        
        List<LoanApprovalResponse> responses = requests.stream()
                .map(this::evaluateLoanApplication)
                .collect(Collectors.toList());
        
        totalBatchEvaluations.incrementAndGet();
        
        logger.debug("Batch loan application evaluation completed for {} requests", responses.size());
        
        return responses;
    }
    
    @Override
    public CompletableFuture<LoanApprovalResponse> evaluateLoanApplicationAsync(LoanApprovalRequest request) {
        return CompletableFuture.supplyAsync(() -> evaluateLoanApplication(request));
    }
    
    @Override
    public Map<String, Object> getLoanApprovalStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalEvaluations", totalEvaluations.get());
        statistics.put("totalBatchEvaluations", totalBatchEvaluations.get());
        statistics.put("totalApprovals", totalApprovals.get());
        statistics.put("totalRejections", totalRejections.get());
        statistics.put("approvalRate", calculateApprovalRate());
        
        Map<String, Long> approvalReasons = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : approvalCounts.entrySet()) {
            approvalReasons.put(entry.getKey(), entry.getValue().get());
        }
        statistics.put("approvalReasons", approvalReasons);
        
        Map<String, Long> rejectionReasons = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : rejectionCounts.entrySet()) {
            rejectionReasons.put(entry.getKey(), entry.getValue().get());
        }
        statistics.put("rejectionReasons", rejectionReasons);
        
        return statistics;
    }
    
    /**
     * Updates approval statistics based on the loan application results.
     * 
     * @param loanApplication The loan application with approval decision
     */
    private void updateApprovalStatistics(LoanApplication loanApplication) {
        if (loanApplication.isApproved()) {
            totalApprovals.incrementAndGet();
            if (loanApplication.getDecisionReason() != null && !loanApplication.getDecisionReason().isEmpty()) {
                approvalCounts.computeIfAbsent(loanApplication.getDecisionReason(), k -> new AtomicLong(0))
                        .incrementAndGet();
            }
        } else {
            totalRejections.incrementAndGet();
            if (loanApplication.getDecisionReason() != null && !loanApplication.getDecisionReason().isEmpty()) {
                rejectionCounts.computeIfAbsent(loanApplication.getDecisionReason(), k -> new AtomicLong(0))
                        .incrementAndGet();
            }
        }
    }
    
    /**
     * Calculates the approval rate as a percentage.
     * 
     * @return The approval rate as a percentage
     */
    private double calculateApprovalRate() {
        long total = totalApprovals.get() + totalRejections.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) totalApprovals.get() / total * 100.0;
    }
}