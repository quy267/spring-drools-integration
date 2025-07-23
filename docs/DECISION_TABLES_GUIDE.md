# Decision Tables Guide - Spring Boot Drools Integration

This comprehensive guide covers everything you need to know about decision tables in the Spring Boot Drools Integration application, including format specifications, usage instructions, best practices, and examples.

## 📋 Table of Contents

- [Overview](#overview)
- [Decision Table Fundamentals](#decision-table-fundamentals)
- [File Structure and Format](#file-structure-and-format)
- [Available Decision Tables](#available-decision-tables)
- [Creating Decision Tables](#creating-decision-tables)
- [Modifying Existing Tables](#modifying-existing-tables)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Examples and Templates](#examples-and-templates)
- [Integration with Application](#integration-with-application)

## 🔍 Overview

Decision tables are a powerful way to define business rules in a spreadsheet format that can be easily understood and modified by both technical and non-technical users. In this application, decision tables are stored as Excel files and automatically converted to Drools Rule Language (DRL) at runtime.

### Key Benefits

- **Business-Friendly**: Non-technical users can modify rules without coding
- **Visual Clarity**: Rules are presented in an easy-to-understand tabular format
- **Version Control**: Excel files can be versioned and tracked
- **Hot Reloading**: Rules can be updated without application restart (development mode)
- **Validation**: Built-in validation ensures rule consistency

## 🏗️ Decision Table Fundamentals

### What is a Decision Table?

A decision table is a tabular representation of business logic where:
- **Rows** represent individual rules
- **Columns** represent conditions (inputs) and actions (outputs)
- **Cells** contain the specific values or expressions for each rule

### Basic Structure

```
| RuleSet | RuleId | Priority | Condition1 | Condition2 | Action1 | Action2 |
|---------|--------|----------|------------|------------|---------|---------|
| MyRules | Rule1  | 100      | value1     | value2     | result1 | result2 |
| MyRules | Rule2  | 90       | value3     | value4     | result3 | result4 |
```

### Rule Execution Logic

1. **Evaluation Order**: Rules are evaluated based on priority (salience) - higher values first
2. **Condition Matching**: All conditions in a row must be true for the rule to fire
3. **Action Execution**: When a rule matches, its actions are executed
4. **Multiple Matches**: Multiple rules can fire for the same input (unless configured otherwise)

## 📁 File Structure and Format

### Directory Structure

```
src/main/resources/rules/decision-tables/
├── CustomerDiscountRules.xlsx
├── LoanApprovalRules.xlsx
├── ProductRecommendationRules.xlsx
├── DecisionTableGuide.md
├── CustomerDiscountRules.md
├── LoanApprovalRules.md
└── ProductRecommendationRules.md
```

### Excel File Format

Each decision table Excel file must contain at least two sheets:

#### 1. Documentation Sheet
- **Purpose**: Provides metadata and usage instructions
- **Contents**:
  - Table title and description
  - Column definitions
  - Usage instructions
  - Version information
  - Change history

#### 2. Rules Sheet
- **Purpose**: Contains the actual decision table
- **Structure**: Tabular format with headers and data rows

### Standard Headers

All decision tables include these mandatory headers:

| Header | Description | Example Values |
|--------|-------------|----------------|
| `RuleSet` | Name of the rule set | "CustomerDiscountRules" |
| `RuleId` | Unique identifier for each rule | "Senior_Discount", "Gold_Tier" |
| `Salience` | Rule priority (higher = higher priority) | 100, 90, 80 |

### Condition and Action Columns

- **Condition Columns**: Define when a rule should apply
- **Action Columns**: Define what happens when conditions are met
- **Column Names**: Must match the property names in your domain objects

## 📊 Available Decision Tables

### 1. Customer Discount Rules

**File**: `CustomerDiscountRules.xlsx`
**Purpose**: Calculate customer discounts based on various factors

**Conditions**:
- Customer Age
- Loyalty Tier
- Order Amount
- Order Quantity
- Membership Years

**Actions**:
- Discount Percentage
- Applied Rule Name

**Example Rule**:
```
| RuleSet | RuleId | Salience | Customer Age | Loyalty Tier | Order Amount | Discount Percentage |
|---------|--------|----------|--------------|--------------|--------------|---------------------|
| CustomerDiscountRules | Premium_Customer | 100 | > 60 | == "GOLD" | >= 200 | 20 |
```

### 2. Loan Approval Rules

**File**: `LoanApprovalRules.xlsx`
**Purpose**: Evaluate loan applications and determine approval status

**Conditions**:
- Credit Score
- Annual Income
- Loan Amount
- Employment Years
- Existing Debts
- Debt-to-Income Ratio

**Actions**:
- Approval Status
- Interest Rate
- Loan Term
- Conditions

**Example Rule**:
```
| RuleSet | RuleId | Salience | Credit Score | Annual Income | Approval Status | Interest Rate |
|---------|--------|----------|--------------|---------------|-----------------|---------------|
| LoanApprovalRules | High_Credit | 100 | >= 750 | >= 50000 | APPROVED | 3.5 |
```

### 3. Product Recommendation Rules

**File**: `ProductRecommendationRules.xlsx`
**Purpose**: Generate product recommendations based on customer profile

**Conditions**:
- Customer Age
- Preferences
- Purchase History
- Budget
- Location
- Season

**Actions**:
- Recommended Products
- Confidence Score
- Recommendation Reason

**Example Rule**:
```
| RuleSet | RuleId | Salience | Age Range | Preferences | Budget | Recommended Category |
|---------|--------|----------|-----------|-------------|--------|---------------------|
| ProductRecommendationRules | Tech_Enthusiast | 100 | 25-40 | ELECTRONICS | >= 500 | SMARTPHONES |
```

## 🛠️ Creating Decision Tables

### Step 1: Plan Your Rules

Before creating a decision table:

1. **Identify the Business Logic**: What decisions need to be made?
2. **Define Inputs**: What information is available for decision-making?
3. **Define Outputs**: What should be the result of the decision?
4. **List Scenarios**: What are all the possible combinations?

### Step 2: Create the Excel File

1. **Create a new Excel file** (.xlsx format recommended)
2. **Add Documentation sheet** with:
   ```
   Title: [Your Rule Set Name]
   Description: [What this rule set does]
   Version: 1.0
   Last Updated: [Date]
   
   Column Definitions:
   - [Column Name]: [Description]
   - [Column Name]: [Description]
   
   Usage Instructions:
   - [How to use these rules]
   - [Important notes]
   ```

3. **Add Rules sheet** with proper headers

### Step 3: Define the Table Structure

#### Headers Row
```excel
| RuleSet | RuleId | Salience | [Condition1] | [Condition2] | [Action1] | [Action2] |
```

#### Data Rows
```excel
| MyRules | Rule1 | 100 | value1 | value2 | result1 | result2 |
| MyRules | Rule2 | 90  | value3 | value4 | result3 | result4 |
```

### Step 4: Define Conditions and Actions

#### Condition Syntax

| Operator | Syntax | Example |
|----------|--------|---------|
| Equals | `== "value"` | `== "GOLD"` |
| Not Equals | `!= "value"` | `!= "BRONZE"` |
| Greater Than | `> value` | `> 60` |
| Less Than | `< value` | `< 18` |
| Greater or Equal | `>= value` | `>= 1000` |
| Less or Equal | `<= value` | `<= 100` |
| Between | `between(min, max)` | `between(18, 65)` |
| In List | `in ("val1", "val2")` | `in ("GOLD", "PLATINUM")` |
| Contains | `contains "text"` | `contains "PREMIUM"` |

#### Action Syntax

Actions typically assign values:
```excel
discountPercentage = 15
approvalStatus = "APPROVED"
interestRate = 3.5
recommendedProducts.add("LAPTOP")
```

### Step 5: Add Rules

1. **Start with highest priority rules** (highest salience values)
2. **Add specific rules first**, then general rules
3. **Use empty cells** for "don't care" conditions
4. **Test edge cases** and boundary conditions

### Example: Creating a Simple Discount Table

```excel
| RuleSet | RuleId | Salience | Customer Age | Loyalty Tier | Discount Percentage |
|---------|--------|----------|--------------|--------------|---------------------|
| DiscountRules | Senior_Gold | 100 | > 60 | == "GOLD" | 25 |
| DiscountRules | Senior | 90 | > 60 | | 15 |
| DiscountRules | Gold_Tier | 80 | | == "GOLD" | 20 |
| DiscountRules | Silver_Tier | 70 | | == "SILVER" | 10 |
| DiscountRules | Default | 10 | | | 5 |
```

## ✏️ Modifying Existing Tables

### Safe Modification Process

1. **Backup the Original**: Always create a backup before making changes
2. **Understand Current Logic**: Review existing rules and their interactions
3. **Plan Changes**: Document what you want to change and why
4. **Make Incremental Changes**: Change one rule at a time
5. **Test Thoroughly**: Verify changes work as expected

### Common Modifications

#### Adding a New Rule

1. **Determine Priority**: Where should this rule fit in the execution order?
2. **Add New Row**: Insert at the appropriate position based on salience
3. **Fill All Columns**: Ensure all required fields are populated
4. **Test Interactions**: Verify the new rule doesn't conflict with existing ones

#### Modifying Existing Rules

1. **Identify the Rule**: Find the rule by RuleId
2. **Update Conditions/Actions**: Modify the appropriate cells
3. **Check Dependencies**: Ensure changes don't break other rules
4. **Update Documentation**: Reflect changes in the documentation sheet

#### Removing Rules

1. **Identify Dependencies**: Check if other rules depend on this one
2. **Delete the Row**: Remove the entire rule row
3. **Test Coverage**: Ensure remaining rules cover all necessary scenarios

### Hot Reloading (Development Mode)

When hot reloading is enabled:

1. **Save the Excel File**: Changes are automatically detected
2. **Wait for Reload**: The application will reload rules automatically
3. **Check Logs**: Verify successful reload in application logs
4. **Test Changes**: Validate that changes work as expected

## 🎯 Best Practices

### Rule Design

1. **Keep Rules Simple**: Each rule should have a clear, single purpose
2. **Use Descriptive Names**: RuleId should clearly indicate what the rule does
3. **Order by Priority**: Higher priority rules should handle specific cases
4. **Avoid Overlaps**: Minimize conflicting rules unless intentional
5. **Handle Edge Cases**: Include rules for boundary conditions

### Table Organization

1. **Consistent Naming**: Use consistent naming conventions across tables
2. **Logical Grouping**: Group related rules together
3. **Clear Headers**: Use descriptive column names
4. **Documentation**: Always include comprehensive documentation

### Performance Optimization

1. **Rule Ordering**: Put most frequently matched rules first
2. **Condition Efficiency**: Order conditions from most to least selective
3. **Minimize Rules**: Combine similar rules where possible
4. **Use Salience Wisely**: Don't over-complicate priority schemes

### Maintenance

1. **Version Control**: Track changes to decision tables
2. **Regular Reviews**: Periodically review and optimize rules
3. **Testing**: Maintain comprehensive test cases
4. **Documentation Updates**: Keep documentation current

## 🔧 Troubleshooting

### Common Issues

#### 1. Rules Not Loading

**Symptoms**: Rules don't seem to be applied
**Possible Causes**:
- File not in correct directory
- Incorrect file format
- Syntax errors in conditions/actions

**Solutions**:
- Verify file location: `src/main/resources/rules/decision-tables/`
- Check file extension (.xlsx or .xls)
- Review application logs for error messages
- Validate Excel file structure

#### 2. Unexpected Rule Behavior

**Symptoms**: Rules fire when they shouldn't or don't fire when expected
**Possible Causes**:
- Incorrect condition syntax
- Wrong salience values
- Conflicting rules

**Solutions**:
- Review condition syntax
- Check rule priorities
- Enable debug logging
- Test with simplified scenarios

#### 3. Performance Issues

**Symptoms**: Slow rule execution
**Possible Causes**:
- Too many rules
- Inefficient conditions
- Complex expressions

**Solutions**:
- Optimize rule order
- Simplify conditions
- Reduce number of rules
- Use profiling tools

### Debugging Tips

1. **Enable Debug Logging**: Set logging level to DEBUG for rule execution
2. **Use Simple Test Cases**: Start with basic scenarios
3. **Check Rule Compilation**: Verify rules compile without errors
4. **Review Generated DRL**: Examine the generated Drools rules
5. **Use Rule Tracing**: Enable rule execution tracing

### Validation Checklist

Before deploying decision tables:

- [ ] All required headers are present
- [ ] RuleId values are unique
- [ ] Salience values are appropriate
- [ ] Condition syntax is correct
- [ ] Action syntax is valid
- [ ] Documentation is complete
- [ ] Test cases cover all scenarios
- [ ] Performance is acceptable

## 📝 Examples and Templates

### Template: Basic Decision Table

```excel
| RuleSet | RuleId | Salience | Condition1 | Condition2 | Action1 | Action2 |
|---------|--------|----------|------------|------------|---------|---------|
| MyRules | HighPriority | 100 | condition1 | condition2 | action1 | action2 |
| MyRules | MediumPriority | 50 | condition3 | | action3 | |
| MyRules | Default | 10 | | | defaultAction | |
```

### Example: Age-Based Pricing

```excel
| RuleSet | RuleId | Salience | Customer Age | Price Modifier | Reason |
|---------|--------|----------|--------------|----------------|--------|
| PricingRules | Senior | 100 | > 65 | 0.8 | "Senior Discount" |
| PricingRules | Adult | 50 | between(18, 65) | 1.0 | "Standard Price" |
| PricingRules | Youth | 75 | < 18 | 0.9 | "Youth Discount" |
```

### Example: Multi-Condition Rules

```excel
| RuleSet | RuleId | Salience | Credit Score | Income | Loan Amount | Approval | Rate |
|---------|--------|----------|--------------|--------|-------------|----------|------|
| LoanRules | Excellent | 100 | >= 800 | >= 100000 | <= 500000 | APPROVED | 2.5 |
| LoanRules | Good | 80 | >= 700 | >= 75000 | <= 400000 | APPROVED | 3.0 |
| LoanRules | Fair | 60 | >= 650 | >= 50000 | <= 300000 | CONDITIONAL | 4.0 |
| LoanRules | Poor | 40 | < 650 | | | REJECTED | |
```

## 🔗 Integration with Application

### Loading Process

1. **Application Startup**: Decision tables are loaded automatically
2. **File Scanning**: System scans the decision-tables directory
3. **Excel Processing**: Files are processed using Apache POI
4. **DRL Generation**: Excel tables are converted to Drools rules
5. **Rule Compilation**: Generated rules are compiled and loaded

### Service Integration

Decision tables are integrated with service classes:

```java
@Service
public class CustomerDiscountService {
    
    private final KieSession kieSession;
    
    public CustomerDiscountResponse calculateDiscount(CustomerDiscountRequest request) {
        // Insert facts into working memory
        kieSession.insert(request);
        
        // Fire rules
        kieSession.fireAllRules();
        
        // Extract results
        return extractResults();
    }
}
```

### Configuration

Key configuration properties:

```properties
# Decision table path
app.drools.decision-table-path=classpath:rules/decision-tables/

# Hot reload settings
app.drools.hot-reload.enabled=true
app.drools.hot-reload.watch-interval=5000

# Rule engine settings
app.drools.rule-engine.parallel-execution=true
app.drools.rule-engine.max-threads=4
```

### API Integration

Decision tables are exposed through REST APIs:

- **Rule Execution**: Execute rules with input data
- **Rule Management**: Upload, validate, and reload rules
- **Rule Status**: Check rule engine status and statistics

### Monitoring

Monitor decision table usage through:

- **Actuator Endpoints**: Health checks and metrics
- **Custom Metrics**: Rule execution times and hit rates
- **Logging**: Detailed rule execution logs
- **Performance Monitoring**: Rule performance statistics

## 📚 Additional Resources

### Documentation Files

- [DecisionTableGuide.md](../src/main/resources/rules/decision-tables/DecisionTableGuide.md) - Basic usage guide
- [CustomerDiscountRules.md](../src/main/resources/rules/decision-tables/CustomerDiscountRules.md) - Customer discount specification
- [LoanApprovalRules.md](../src/main/resources/rules/decision-tables/LoanApprovalRules.md) - Loan approval specification
- [ProductRecommendationRules.md](../src/main/resources/rules/decision-tables/ProductRecommendationRules.md) - Product recommendation specification

### External Resources

- [Drools Documentation](https://docs.drools.org/) - Official Drools documentation
- [Apache POI](https://poi.apache.org/) - Excel file processing library
- [Decision Tables in Drools](https://docs.drools.org/7.73.0.Final/drools-docs/html_single/#decision-tables-con_decision-tables) - Drools decision table guide

### Tools and Utilities

- **Excel/LibreOffice Calc**: For editing decision tables
- **Rule Validation Tools**: Built-in validation in the application
- **Testing Frameworks**: JUnit tests for rule validation
- **Performance Profilers**: For optimizing rule performance

---

**For additional support with decision tables, please refer to the main [README](../README.md) or create an issue in the repository.**