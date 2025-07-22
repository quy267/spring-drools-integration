# Customer Discount Rules - Decision Table Template

## Overview

This template provides a structure for creating decision tables that determine customer discounts based on various factors such as age, loyalty tier, and order details.

## Decision Table Structure

### Required Headers

The decision table must include the following headers in the first row:

| Header | Description |
|--------|-------------|
| RuleSet | The name of the rule set (e.g., "CustomerDiscountRules") |
| RuleId | A unique identifier for each rule (e.g., "Age Discount", "Loyalty Discount") |
| Condition | Conditions that must be met for the rule to apply |
| Action | The action to take when the conditions are met |

### Condition Columns

Condition columns define when a rule should be applied. Common condition columns include:

| Column Name | Description | Example Values |
|-------------|-------------|----------------|
| Customer Age | The age of the customer | "== 65", "> 60", "< 18" |
| Loyalty Tier | The customer's loyalty tier | "== \"GOLD\"", "== \"SILVER\"" |
| Order Amount | The total amount of the order | "> 100", "< 50" |
| Order Quantity | The number of items in the order | ">= 10", "< 5" |

### Action Columns

Action columns define what happens when a rule's conditions are met. Common action columns include:

| Column Name | Description | Example Values |
|-------------|-------------|----------------|
| Discount Percentage | The percentage discount to apply | "10", "15", "20" |
| Applied Rule | The name of the rule that was applied | "\"Senior Discount\"", "\"Bulk Order Discount\"" |

## Example Rules

Here's an example of how rules might be structured in your decision table:

| RuleSet | RuleId | Customer Age | Loyalty Tier | Order Amount | Order Quantity | Discount Percentage | Applied Rule |
|---------|--------|--------------|--------------|--------------|----------------|---------------------|-------------|
| CustomerDiscountRules | Senior Discount | > 60 |  |  |  | 10 | "Senior Discount" |
| CustomerDiscountRules | Gold Tier Discount |  | == "GOLD" |  |  | 15 | "Gold Tier Discount" |
| CustomerDiscountRules | Silver Tier Discount |  | == "SILVER" |  |  | 10 | "Silver Tier Discount" |
| CustomerDiscountRules | Large Order Discount |  |  | >= 200 |  | 5 | "Large Order Discount" |
| CustomerDiscountRules | Bulk Order Discount |  |  |  | >= 20 | 8 | "Bulk Order Discount" |
| CustomerDiscountRules | Premium Customer Discount | > 60 | == "GOLD" |  |  | 20 | "Premium Customer Discount" |

## How to Use This Template

1. Create a new Excel file (.xls or .xlsx)
2. Add a sheet for your customer discount rules
3. Copy the headers and example rules from this template
4. Modify the conditions and actions to match your business requirements
5. Save the file and upload it to the application

## Best Practices

1. **Rule Priority**: Rules are executed in the order they appear in the table. Place more specific rules before more general rules.
2. **Rule Naming**: Use clear, descriptive names for your rules in the RuleId column.
3. **Documentation**: Add comments or a documentation sheet to explain complex rules.
4. **Testing**: Test your rules with various scenarios to ensure they produce the expected results.
5. **Maintenance**: Regularly review and update your rules as business requirements change.

## Example Scenarios

### Scenario 1: Senior Gold Customer with Large Order

A 65-year-old customer with Gold loyalty status places an order for $250.

Applied Rules:
- Senior Discount: 10%
- Gold Tier Discount: 15%
- Large Order Discount: 5%
- Premium Customer Discount: 20%

The highest discount (20%) would be applied.

### Scenario 2: Regular Customer with Bulk Order

A 35-year-old customer with no loyalty status places an order for 25 items totaling $150.

Applied Rules:
- Bulk Order Discount: 8%

The 8% discount would be applied.

## Notes

- Empty cells in condition columns mean "don't care" (the condition is ignored)
- Multiple conditions in a rule are combined with AND logic (all conditions must be true)
- If multiple rules match, the rule with the highest discount percentage is applied