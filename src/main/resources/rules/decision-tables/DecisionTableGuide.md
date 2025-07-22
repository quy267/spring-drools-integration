# Decision Tables Usage Guide

## Overview

This guide provides comprehensive documentation on how to use, modify, and extend the decision tables in the Spring Drools Integration application. Decision tables are a powerful way to define business rules in a spreadsheet format, making them accessible to both technical and non-technical users.

## Available Decision Tables

The application includes the following decision tables:

1. **CustomerDiscountRules.xlsx** - Determines discount percentages for customers based on age, loyalty tier, and order details
2. **LoanApprovalRules.xlsx** - Determines loan approval decisions based on credit score, income, and other financial factors
3. **ProductRecommendationRules.xlsx** - Determines product recommendations based on customer demographics and purchase history

## Decision Table Structure

All decision tables follow a standardized structure:

### Excel File Format

- Decision tables are stored as Excel files (.xlsx or .xls)
- Each file contains at least two sheets:
  - **Documentation** - Contains information about the table, column descriptions, and usage instructions
  - **Rules** - Contains the actual decision table with conditions and actions

### Common Headers

All decision tables include these standard headers:

- **RuleSet** - The name of the rule set (e.g., "CustomerDiscountRules")
- **RuleId** - A unique identifier for each rule
- **Salience/Priority** - Controls rule execution order (higher values have higher priority)

### Condition and Action Columns

- **Condition Columns** - Define when a rule should be applied (e.g., Customer Age, Credit Score)
- **Action Columns** - Define what happens when a rule's conditions are met (e.g., Discount Percentage, Approval Status)

## How to Use Decision Tables

### Loading Decision Tables

The application automatically loads decision tables from the following location:
```
/src/main/resources/rules/decision-tables/
```

When the application starts, it scans this directory for Excel files and loads them into the Drools rule engine.

### Executing Rules

Rules are executed through the appropriate service classes:

- **CustomerDiscountService** - For customer discount rules
- **LoanApprovalService** - For loan approval rules
- **ProductRecommendationService** - For product recommendation rules

Example API call:
```java
// Example: Execute customer discount rules
CustomerDiscountRequest request = new CustomerDiscountRequest(
    "John Doe",
    35,
    "GOLD",
    150.0,
    3
);
CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);
```

## How to Modify Decision Tables

### Editing Existing Rules

1. Open the Excel file in a spreadsheet application (e.g., Microsoft Excel, LibreOffice Calc)
2. Navigate to the "Rules" sheet
3. Modify the condition or action values as needed
4. Save the file in the same location with the same name
5. Restart the application or trigger a rule reload (if hot-reload is enabled)

### Adding New Rules

1. Open the Excel file in a spreadsheet application
2. Navigate to the "Rules" sheet
3. Add a new row at the appropriate position (rules are evaluated in order of Salience/Priority)
4. Fill in all required columns (RuleSet, RuleId, conditions, and actions)
5. Save the file
6. Restart the application or trigger a rule reload

### Best Practices for Rule Modification

1. **Backup First** - Always create a backup of the original file before making changes
2. **Test Changes** - Test your changes with various scenarios to ensure they produce the expected results
3. **Document Changes** - Update the Documentation sheet with any significant changes
4. **Maintain Rule Order** - Keep rules in order of Salience/Priority (highest to lowest)
5. **Avoid Conflicts** - Ensure rules don't conflict with each other in unexpected ways

## How to Create New Decision Tables

### Creating a New Decision Table

1. Create a new Excel file (.xlsx or .xls)
2. Add two sheets: "Documentation" and "Rules"
3. In the Documentation sheet, include:
   - Title and description
   - Column descriptions
   - Usage instructions
4. In the Rules sheet, add the following headers:
   - RuleSet - The name of your rule set
   - RuleId - Unique identifiers for each rule
   - Salience/Priority - Rule priority values
   - Condition columns - Define when rules should apply
   - Action columns - Define what happens when rules match
5. Add rules as rows under the headers
6. Save the file in the `/src/main/resources/rules/decision-tables/` directory
7. Update the application configuration to recognize the new decision table (if necessary)

### Decision Table Syntax

#### Condition Syntax

Conditions use the following syntax:

- **Comparison Operators**: `==`, `!=`, `>`, `<`, `>=`, `<=`
- **String Values**: Must be quoted (e.g., `== "GOLD"`)
- **Numeric Values**: No quotes (e.g., `> 100`)
- **Range Check**: Use `between(min, max)` (e.g., `between(18, 25)`)
- **Empty Cell**: Means "don't care" (condition is ignored)

Examples:
- `> 60` - Value must be greater than 60
- `== "GOLD"` - Value must equal "GOLD"
- `between(600, 750)` - Value must be between 600 and 750 (inclusive)

#### Action Syntax

Actions use the following syntax:

- **String Values**: Must be quoted (e.g., `"APPROVED"`)
- **Numeric Values**: No quotes (e.g., `10`)
- **Boolean Values**: `true` or `false` (no quotes)

Examples:
- `"Senior Discount"` - Sets a string value
- `10` - Sets a numeric value
- `true` - Sets a boolean value

## Integration with Spring Boot Application

### Technical Implementation

The decision tables are integrated with the Spring Boot application through the following components:

1. **DroolsConfig** - Configures the Drools rule engine and loads decision tables
2. **DecisionTableProcessor** - Processes Excel files and converts them to Drools rules
3. **RuleExecutionService** - Provides methods for executing rules against input data
4. **Domain-specific Services** - Handle rule execution for specific domains (e.g., CustomerDiscountService)

### Configuration Properties

The following properties in `application.properties` control decision table behavior:

```properties
# Decision Table Configuration
app.drools.decision-table-path=classpath:rules/decision-tables/
app.drools.file-extensions=.drl,.xls,.xlsx
app.drools.hot-reload=true
app.drools.hot-reload-interval=30000
```

### Hot Reloading

If hot-reloading is enabled (`app.drools.hot-reload=true`), the application will automatically detect changes to decision tables and reload them without requiring a restart. The check interval is controlled by `app.drools.hot-reload-interval` (in milliseconds).

## Troubleshooting

### Common Issues

1. **Rule Not Firing**
   - Check that the conditions match your input data exactly
   - Verify that the rule has a high enough Salience/Priority
   - Ensure the rule is in the correct RuleSet

2. **Excel File Not Loading**
   - Verify the file is in the correct directory
   - Check that the file extension is supported (.xls or .xlsx)
   - Ensure the file is not corrupted or password-protected

3. **Unexpected Rule Results**
   - Check for conflicting rules with higher Salience/Priority
   - Verify that all conditions are correctly formatted
   - Ensure that action values are of the correct type

### Debugging

1. Enable debug logging by adding the following to `application.properties`:
   ```properties
   logging.level.org.drools=DEBUG
   logging.level.com.example.springdroolsintegration=DEBUG
   ```

2. Use the rule execution tracing feature:
   ```properties
   app.drools.trace-enabled=true
   ```

## Examples

### Customer Discount Example

```java
// Create a request
CustomerDiscountRequest request = new CustomerDiscountRequest();
request.setCustomerName("John Doe");
request.setCustomerAge(65);
request.setLoyaltyTier("GOLD");
request.setOrderAmount(250.0);
request.setOrderQuantity(3);

// Execute rules
CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

// Check results
System.out.println("Discount: " + response.getDiscountPercentage() + "%");
System.out.println("Applied Rule: " + response.getAppliedRule());
```

Expected result:
- Discount: 20%
- Applied Rule: Premium Customer Discount

### Loan Approval Example

```java
// Create a request
LoanApprovalRequest request = new LoanApprovalRequest();
request.setFirstName("Jane");
request.setLastName("Smith");
request.setDateOfBirth(LocalDate.of(1980, 5, 15));
request.setCreditScore(720);
request.setAnnualIncome(85000.0);
request.setLoanAmount(250000.0);
request.setLoanTermMonths(360);
request.setDebtToIncomeRatio(0.35);
request.setEmploymentYears(4);
request.setLoanPurpose("HOME");

// Execute rules
LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);

// Check results
System.out.println("Approval Status: " + response.getApprovalStatus());
System.out.println("Interest Rate: " + response.getInterestRate() + "%");
System.out.println("Risk Level: " + response.getRiskLevel());
```

Expected result:
- Approval Status: APPROVED
- Interest Rate: 3.85%
- Risk Level: LOW

### Product Recommendation Example

```java
// Create a request
ProductRecommendationRequest request = new ProductRecommendationRequest();
request.setCustomerId(1001L);
request.setFirstName("Alice");
request.setLastName("Johnson");
request.setAge(28);
request.setGender("F");
request.setCustomerCategory("NEW");
request.setMaxRecommendations(5);

// Execute rules
ProductRecommendationResponse response = productRecommendationService.getRecommendations(request);

// Check results
System.out.println("Recommended Category: " + response.getRecommendations().get(0).getCategory());
System.out.println("Recommended Product: " + response.getRecommendations().get(0).getName());
System.out.println("Discount Offer: " + response.getRecommendations().get(0).isDiscountOffer());
```

Expected result:
- Recommended Category: ELECTRONICS
- Recommended Product: Smartphone
- Discount Offer: true

## Conclusion

Decision tables provide a powerful and flexible way to define business rules in the Spring Drools Integration application. By following this guide, you can effectively use, modify, and extend the decision tables to meet your specific business requirements.

For more information, refer to:
- The Drools documentation: https://docs.drools.org/
- The specific decision table specifications in the `/src/main/resources/rules/decision-tables/` directory