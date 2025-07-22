# Product Recommendation Rules - Decision Table Template

## Overview

This template provides a structure for creating decision tables that determine product recommendations based on various factors such as customer demographics, purchase history, and product attributes.

## Decision Table Structure

### Required Headers

The decision table must include the following headers in the first row:

| Header | Description |
|--------|-------------|
| RuleSet | The name of the rule set (e.g., "ProductRecommendationRules") |
| RuleId | A unique identifier for each rule (e.g., "New Customer Electronics", "Repeat Buyer Home Goods") |
| Condition | Conditions that must be met for the rule to apply |
| Action | The action to take when the conditions are met |

### Condition Columns

Condition columns define when a rule should be applied. Common condition columns include:

| Column Name | Description | Example Values |
|-------------|-------------|----------------|
| Customer Age | The age of the customer | "between(18, 25)", "> 60" |
| Customer Gender | The gender of the customer | "== \"M\"", "== \"F\"" |
| Customer Category | The category of the customer | "== \"NEW\"", "== \"RETURNING\"" |
| Previous Purchases | Number of previous purchases | "> 5", "== 0" |
| Last Purchase Category | Category of the customer's last purchase | "== \"ELECTRONICS\"", "== \"CLOTHING\"" |
| Season | Current season | "== \"SUMMER\"", "== \"WINTER\"" |
| Inventory Level | Current inventory level of the product | "> 100", "< 20" |

### Action Columns

Action columns define what happens when a rule's conditions are met. Common action columns include:

| Column Name | Description | Example Values |
|-------------|-------------|----------------|
| Recommended Category | The product category to recommend | "\"ELECTRONICS\"", "\"HOME_GOODS\"" |
| Recommended Product | The specific product to recommend | "\"Smartphone\"", "\"Laptop\"" |
| Discount Offer | Whether to offer a discount with the recommendation | "true", "false" |
| Recommendation Priority | The priority of the recommendation (higher values take precedence) | "1", "5", "10" |
| Recommendation Reason | The reason for the recommendation | "\"Based on purchase history\"", "\"Seasonal promotion\"" |

## Example Rules

Here's an example of how rules might be structured in your decision table:

| RuleSet | RuleId | Customer Age | Customer Category | Previous Purchases | Last Purchase Category | Season | Inventory Level | Recommended Category | Recommended Product | Discount Offer | Recommendation Priority | Recommendation Reason |
|---------|--------|--------------|-------------------|-------------------|------------------------|--------|-----------------|----------------------|---------------------|---------------|-------------------------|----------------------|
| ProductRecommendationRules | New Customer Electronics | between(18, 30) | == "NEW" |  |  |  |  | "ELECTRONICS" | "Smartphone" | true | 10 | "Welcome offer for new customers" |
| ProductRecommendationRules | Returning Electronics Customer |  | == "RETURNING" | > 0 | == "ELECTRONICS" |  |  | "ELECTRONICS" | "Laptop" | false | 8 | "Based on purchase history" |
| ProductRecommendationRules | Summer Clothing |  |  |  |  | == "SUMMER" | > 50 | "CLOTHING" | "Summer Collection" | false | 5 | "Seasonal recommendation" |
| ProductRecommendationRules | Winter Clothing |  |  |  |  | == "WINTER" | > 50 | "CLOTHING" | "Winter Collection" | false | 5 | "Seasonal recommendation" |
| ProductRecommendationRules | Clearance Items |  |  |  |  |  | > 200 | "CLEARANCE" | "Discounted Items" | true | 3 | "Inventory reduction" |
| ProductRecommendationRules | Senior Discount |  > 65 |  |  |  |  |  | "SPECIAL_OFFERS" | "Senior Discount Items" | true | 7 | "Age-based recommendation" |

## How to Use This Template

1. Create a new Excel file (.xls or .xlsx)
2. Add a sheet for your product recommendation rules
3. Copy the headers and example rules from this template
4. Modify the conditions and actions to match your business requirements
5. Save the file and upload it to the application

## Best Practices

1. **Rule Priority**: Use the Recommendation Priority column to control which recommendations take precedence when multiple rules match.
2. **Rule Naming**: Use clear, descriptive names for your rules in the RuleId column.
3. **Documentation**: Add comments or a documentation sheet to explain complex rules.
4. **Testing**: Test your rules with various customer profiles to ensure they produce the expected recommendations.
5. **Personalization**: Balance general recommendations with personalized ones based on customer attributes and history.
6. **Maintenance**: Regularly review and update your rules as product inventory, seasons, and marketing strategies change.

## Example Scenarios

### Scenario 1: New Young Customer

A new customer who is 25 years old with no purchase history.

Applied Rules:
- New Customer Electronics

Result:
- Recommended Category: ELECTRONICS
- Recommended Product: Smartphone
- Discount Offer: true
- Recommendation Priority: 10
- Recommendation Reason: "Welcome offer for new customers"

### Scenario 2: Returning Electronics Customer in Summer

A returning customer who previously purchased electronics items during the summer season.

Applied Rules:
- Returning Electronics Customer (Priority 8)
- Summer Clothing (Priority 5)

Result (highest priority rule applies):
- Recommended Category: ELECTRONICS
- Recommended Product: Laptop
- Discount Offer: false
- Recommendation Priority: 8
- Recommendation Reason: "Based on purchase history"

## Notes

- Empty cells in condition columns mean "don't care" (the condition is ignored)
- Multiple conditions in a rule are combined with AND logic (all conditions must be true)
- If multiple rules match, the rule with the highest Recommendation Priority is applied
- Consider adding additional columns for more specific product attributes or customer segments
- You can create multiple recommendation rules for the same customer by using different rule sheets or by implementing a rule flow