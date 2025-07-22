# Customer Discount Rules - Decision Table Specification

## Overview

This document provides the specification for the CustomerDiscountRules.xlsx decision table that will be created in the `/src/main/resources/rules/decision-tables/` directory. The decision table will determine customer discounts based on various factors such as age, loyalty tier, and order details.

## Excel File Structure

The Excel file will contain two sheets:
1. **Documentation** - Contains information about the decision table, its purpose, and how to use it
2. **Rules** - Contains the actual decision table with conditions and actions

### Documentation Sheet

The Documentation sheet will include:

- **Title**: Customer Discount Rules
- **Description**: This decision table determines discount percentages for customers based on various factors including age, loyalty tier, order amount, and order quantity.
- **Last Updated**: 2025-07-22
- **Version**: 1.0

#### Column Descriptions:

| Column Name | Description | Possible Values |
|-------------|-------------|----------------|
| RuleSet | The name of the rule set | "CustomerDiscountRules" |
| RuleId | A unique identifier for each rule | Text string (e.g., "Senior Discount") |
| Salience | Priority of the rule (higher values have higher priority) | Integer (e.g., 100, 90, 80) |
| Customer Age | The age of the customer | "> 60", "< 18", "between(18, 25)" |
| Loyalty Tier | The customer's loyalty tier | "== \"GOLD\"", "== \"SILVER\"", "== \"BRONZE\"" |
| Order Amount | The total amount of the order | "> 100", "< 50", ">= 200" |
| Order Quantity | The number of items in the order | ">= 10", "< 5", ">= 20" |
| Discount Percentage | The percentage discount to apply | Integer (e.g., 10, 15, 20) |
| Applied Rule | The name of the rule that was applied | Text string (e.g., "\"Senior Discount\"") |

#### Usage Instructions:

1. Rules are evaluated in order of Salience (highest to lowest)
2. If multiple rules match, the rule with the highest Salience is applied
3. Empty cells in condition columns mean "don't care" (the condition is ignored)
4. Multiple conditions in a rule are combined with AND logic (all conditions must be true)

### Rules Sheet

The Rules sheet will contain the actual decision table with the following structure:

| RuleSet | RuleId | Salience | Customer Age | Loyalty Tier | Order Amount | Order Quantity | Discount Percentage | Applied Rule |
|---------|--------|----------|--------------|--------------|--------------|----------------|---------------------|-------------|
| CustomerDiscountRules | Premium Customer | 100 | > 60 | == "GOLD" |  |  | 20 | "Premium Customer Discount" |
| CustomerDiscountRules | Gold Tier | 90 |  | == "GOLD" |  |  | 15 | "Gold Tier Discount" |
| CustomerDiscountRules | Senior Discount | 85 | > 60 |  |  |  | 10 | "Senior Discount" |
| CustomerDiscountRules | Silver Tier | 80 |  | == "SILVER" |  |  | 10 | "Silver Tier Discount" |
| CustomerDiscountRules | Bronze Tier | 75 |  | == "BRONZE" |  |  | 5 | "Bronze Tier Discount" |
| CustomerDiscountRules | Large Order | 70 |  |  | >= 200 |  | 5 | "Large Order Discount" |
| CustomerDiscountRules | Bulk Order | 65 |  |  |  | >= 20 | 8 | "Bulk Order Discount" |
| CustomerDiscountRules | Student Discount | 60 | between(18, 25) |  |  |  | 5 | "Student Discount" |
| CustomerDiscountRules | Child Discount | 55 | < 18 |  |  |  | 5 | "Child Discount" |
| CustomerDiscountRules | Medium Order | 50 |  |  | between(100, 199.99) |  | 3 | "Medium Order Discount" |
| CustomerDiscountRules | Medium Quantity | 45 |  |  |  | between(10, 19) | 5 | "Medium Quantity Discount" |

## Edge Cases and Boundary Conditions

The decision table includes the following edge cases and boundary conditions:

1. **Age Boundaries**:
   - Children (< 18)
   - Students (18-25)
   - Seniors (> 60)

2. **Order Amount Boundaries**:
   - Medium orders ($100-$199.99)
   - Large orders ($200+)

3. **Order Quantity Boundaries**:
   - Medium quantity (10-19 items)
   - Bulk orders (20+ items)

4. **Loyalty Tier Combinations**:
   - Premium customers (Seniors with Gold tier)
   - Regular tier-based discounts (Gold, Silver, Bronze)

## Implementation Notes

1. The Excel file should be created with the name `CustomerDiscountRules.xlsx` in the `/src/main/resources/rules/decision-tables/` directory.
2. The Salience column is added to control rule priority, with higher values having higher priority.
3. The decision table includes a comprehensive set of rules covering various customer scenarios.
4. Documentation is included both in a separate sheet and within the table structure.