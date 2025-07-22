# Loan Approval Rules - Decision Table Template

## Overview

This template provides a structure for creating decision tables that determine loan approval based on various factors such as credit score, income, loan amount, and employment history.

## Decision Table Structure

### Required Headers

The decision table must include the following headers in the first row:

| Header | Description |
|--------|-------------|
| RuleSet | The name of the rule set (e.g., "LoanApprovalRules") |
| RuleId | A unique identifier for each rule (e.g., "High Credit Score Approval", "Low Income Rejection") |
| Condition | Conditions that must be met for the rule to apply |
| Action | The action to take when the conditions are met |

### Condition Columns

Condition columns define when a rule should be applied. Common condition columns include:

| Column Name | Description | Example Values |
|-------------|-------------|----------------|
| Credit Score | The applicant's credit score | "> 750", "< 600", "between(600, 750)" |
| Annual Income | The applicant's annual income in dollars | "> 50000", "< 30000" |
| Loan Amount | The requested loan amount | "< 100000", "> 500000" |
| Debt-to-Income Ratio | The ratio of monthly debt payments to monthly income | "< 0.36", "> 0.43" |
| Employment Years | Years at current employer | ">= 2", "< 1" |
| Loan Purpose | The purpose of the loan | "== \"HOME\"", "== \"AUTO\"" |

### Action Columns

Action columns define what happens when a rule's conditions are met. Common action columns include:

| Column Name | Description | Example Values |
|-------------|-------------|----------------|
| Approval Status | Whether the loan is approved, rejected, or needs review | "\"APPROVED\"", "\"REJECTED\"", "\"REVIEW\"" |
| Interest Rate | The interest rate to apply to the loan | "3.5", "4.25", "5.0" |
| Required Down Payment | The required down payment percentage | "10", "20", "30" |
| Max Loan Term | The maximum term of the loan in years | "15", "30" |
| Risk Level | The risk level assigned to the loan | "\"LOW\"", "\"MEDIUM\"", "\"HIGH\"" |

## Example Rules

Here's an example of how rules might be structured in your decision table:

| RuleSet | RuleId | Credit Score | Annual Income | Loan Amount | Debt-to-Income Ratio | Employment Years | Approval Status | Interest Rate | Risk Level |
|---------|--------|--------------|---------------|-------------|----------------------|------------------|-----------------|---------------|------------|
| LoanApprovalRules | Excellent Credit Approval | > 750 | > 75000 | < 300000 | < 0.36 | >= 2 | "APPROVED" | 3.5 | "LOW" |
| LoanApprovalRules | Good Credit Approval | between(680, 750) | > 60000 | < 250000 | < 0.40 | >= 2 | "APPROVED" | 4.0 | "LOW" |
| LoanApprovalRules | Fair Credit Review | between(620, 680) | > 50000 | < 200000 | < 0.43 | >= 1 | "REVIEW" | 4.5 | "MEDIUM" |
| LoanApprovalRules | Poor Credit Rejection | < 620 |  |  |  |  | "REJECTED" |  | "HIGH" |
| LoanApprovalRules | High Debt Ratio Rejection |  |  |  | >= 0.43 |  | "REJECTED" |  | "HIGH" |
| LoanApprovalRules | Low Income Rejection |  | < 30000 |  |  |  | "REJECTED" |  | "HIGH" |
| LoanApprovalRules | Jumbo Loan Review |  |  | >= 500000 |  |  | "REVIEW" |  | "MEDIUM" |

## How to Use This Template

1. Create a new Excel file (.xls or .xlsx)
2. Add a sheet for your loan approval rules
3. Copy the headers and example rules from this template
4. Modify the conditions and actions to match your business requirements
5. Save the file and upload it to the application

## Best Practices

1. **Rule Priority**: Rules are executed in the order they appear in the table. Place more specific rules before more general rules.
2. **Rule Naming**: Use clear, descriptive names for your rules in the RuleId column.
3. **Documentation**: Add comments or a documentation sheet to explain complex rules.
4. **Testing**: Test your rules with various scenarios to ensure they produce the expected results.
5. **Compliance**: Ensure your rules comply with relevant lending regulations and fair lending practices.
6. **Maintenance**: Regularly review and update your rules as business requirements and market conditions change.

## Example Scenarios

### Scenario 1: Prime Borrower

An applicant with:
- Credit score: 780
- Annual income: $90,000
- Loan amount: $250,000
- Debt-to-income ratio: 0.32
- Employment years: 5

Applied Rules:
- Excellent Credit Approval

Result:
- Approval Status: APPROVED
- Interest Rate: 3.5%
- Risk Level: LOW

### Scenario 2: Borderline Applicant

An applicant with:
- Credit score: 650
- Annual income: $55,000
- Loan amount: $180,000
- Debt-to-income ratio: 0.41
- Employment years: 1.5

Applied Rules:
- Fair Credit Review

Result:
- Approval Status: REVIEW
- Interest Rate: 4.5%
- Risk Level: MEDIUM

## Notes

- Empty cells in condition columns mean "don't care" (the condition is ignored)
- Multiple conditions in a rule are combined with AND logic (all conditions must be true)
- If multiple rules match, the first matching rule in the table is applied
- Consider adding a "Salience" or "Priority" column for more complex rule ordering