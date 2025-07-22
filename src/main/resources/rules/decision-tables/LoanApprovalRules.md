# Loan Approval Rules - Decision Table Specification

## Overview

This document provides the specification for the LoanApprovalRules.xlsx decision table that will be created in the `/src/main/resources/rules/decision-tables/` directory. The decision table will determine loan approval decisions based on various factors such as credit score, income, debt-to-income ratio, and employment history.

## Excel File Structure

The Excel file will contain two sheets:
1. **Documentation** - Contains information about the decision table, its purpose, and how to use it
2. **Rules** - Contains the actual decision table with conditions and actions

### Documentation Sheet

The Documentation sheet will include:

- **Title**: Loan Approval Rules
- **Description**: This decision table determines loan approval decisions based on various factors including credit score, income, loan amount, debt-to-income ratio, and employment history.
- **Last Updated**: 2025-07-22
- **Version**: 1.0

#### Column Descriptions:

| Column Name | Description | Possible Values |
|-------------|-------------|----------------|
| RuleSet | The name of the rule set | "LoanApprovalRules" |
| RuleId | A unique identifier for each rule | Text string (e.g., "Excellent Credit Approval") |
| Salience | Priority of the rule (higher values have higher priority) | Integer (e.g., 100, 90, 80) |
| Credit Score | The applicant's credit score | "> 750", "< 600", "between(600, 750)" |
| Annual Income | The applicant's annual income in dollars | "> 50000", "< 30000" |
| Loan Amount | The requested loan amount | "< 100000", "> 500000" |
| Debt-to-Income Ratio | The ratio of monthly debt payments to monthly income | "< 0.36", "> 0.43" |
| Employment Years | Years at current employer | ">= 2", "< 1" |
| Loan Purpose | The purpose of the loan | "== \"HOME\"", "== \"AUTO\"" |
| Approval Status | Whether the loan is approved, rejected, or needs review | "\"APPROVED\"", "\"REJECTED\"", "\"REVIEW\"" |
| Interest Rate | The interest rate to apply to the loan | "3.5", "4.25", "5.0" |
| Risk Level | The risk level assigned to the loan | "\"LOW\"", "\"MEDIUM\"", "\"HIGH\"" |

#### Usage Instructions:

1. Rules are evaluated in order of Salience (highest to lowest)
2. If multiple rules match, the rule with the highest Salience is applied
3. Empty cells in condition columns mean "don't care" (the condition is ignored)
4. Multiple conditions in a rule are combined with AND logic (all conditions must be true)

### Rules Sheet

The Rules sheet will contain the actual decision table with the following structure:

| RuleSet | RuleId | Salience | Credit Score | Annual Income | Loan Amount | Debt-to-Income Ratio | Employment Years | Loan Purpose | Approval Status | Interest Rate | Risk Level |
|---------|--------|----------|--------------|---------------|-------------|----------------------|------------------|--------------|-----------------|---------------|------------|
| LoanApprovalRules | Excellent Credit Large Income | 100 | > 750 | > 100000 | < 500000 | < 0.36 | >= 2 |  | "APPROVED" | 3.25 | "LOW" |
| LoanApprovalRules | Excellent Credit | 95 | > 750 | > 75000 | < 300000 | < 0.36 | >= 2 |  | "APPROVED" | 3.5 | "LOW" |
| LoanApprovalRules | Good Credit High Income | 90 | between(700, 750) | > 80000 | < 300000 | < 0.38 | >= 2 |  | "APPROVED" | 3.75 | "LOW" |
| LoanApprovalRules | Good Credit | 85 | between(680, 750) | > 60000 | < 250000 | < 0.40 | >= 2 |  | "APPROVED" | 4.0 | "LOW" |
| LoanApprovalRules | Fair Credit Stable Employment | 80 | between(620, 680) | > 60000 | < 200000 | < 0.42 | >= 3 |  | "APPROVED" | 4.25 | "MEDIUM" |
| LoanApprovalRules | Fair Credit Review | 75 | between(620, 680) | > 50000 | < 200000 | < 0.43 | >= 1 |  | "REVIEW" | 4.5 | "MEDIUM" |
| LoanApprovalRules | Borderline Credit Review | 70 | between(600, 620) | > 60000 | < 150000 | < 0.40 | >= 2 |  | "REVIEW" | 4.75 | "MEDIUM" |
| LoanApprovalRules | Home Loan Special | 65 | > 680 | > 70000 | < 350000 | < 0.40 | >= 2 | == "HOME" | "APPROVED" | 3.85 | "LOW" |
| LoanApprovalRules | Auto Loan Special | 60 | > 680 | > 60000 | < 50000 | < 0.42 | >= 1 | == "AUTO" | "APPROVED" | 3.9 | "LOW" |
| LoanApprovalRules | Poor Credit Rejection | 55 | < 600 |  |  |  |  |  | "REJECTED" |  | "HIGH" |
| LoanApprovalRules | High Debt Ratio Rejection | 50 |  |  |  | >= 0.43 |  |  | "REJECTED" |  | "HIGH" |
| LoanApprovalRules | Low Income Rejection | 45 |  | < 30000 |  |  |  |  | "REJECTED" |  | "HIGH" |
| LoanApprovalRules | Unstable Employment Rejection | 40 |  |  |  |  | < 1 |  | "REJECTED" |  | "HIGH" |
| LoanApprovalRules | Jumbo Loan Review | 35 |  |  | >= 500000 |  |  |  | "REVIEW" |  | "MEDIUM" |
| LoanApprovalRules | High Debt Borderline Review | 30 |  |  |  | between(0.40, 0.43) |  |  | "REVIEW" |  | "MEDIUM" |

## Edge Cases and Boundary Conditions

The decision table includes the following edge cases and boundary conditions:

1. **Credit Score Boundaries**:
   - Excellent credit (> 750)
   - Good credit (680-750)
   - Fair credit (620-680)
   - Borderline credit (600-620)
   - Poor credit (< 600)

2. **Income Boundaries**:
   - Low income (< $30,000)
   - Moderate income ($50,000-$75,000)
   - High income (> $80,000)
   - Very high income (> $100,000)

3. **Debt-to-Income Ratio Boundaries**:
   - Low DTI (< 0.36)
   - Moderate DTI (0.36-0.40)
   - High DTI (0.40-0.43)
   - Excessive DTI (>= 0.43)

4. **Employment Stability Boundaries**:
   - Unstable employment (< 1 year)
   - Stable employment (>= 2 years)
   - Very stable employment (>= 3 years)

5. **Loan Amount Boundaries**:
   - Small loans (< $50,000)
   - Medium loans ($50,000-$200,000)
   - Large loans ($200,000-$500,000)
   - Jumbo loans (>= $500,000)

6. **Special Loan Types**:
   - Home loans with special rates
   - Auto loans with special rates

## Implementation Notes

1. The Excel file should be created with the name `LoanApprovalRules.xlsx` in the `/src/main/resources/rules/decision-tables/` directory.
2. The Salience column is added to control rule priority, with higher values having higher priority.
3. The decision table includes a comprehensive set of rules covering various loan application scenarios.
4. Documentation is included both in a separate sheet and within the table structure.
5. The rules are designed to handle a wide range of applicant profiles, from excellent credit with high income to poor credit with low income.
6. Special rules are included for specific loan purposes (HOME, AUTO) to demonstrate purpose-specific approval criteria.