# Product Recommendation Rules - Decision Table Specification

## Overview

This document provides the specification for the ProductRecommendationRules.xlsx decision table that will be created in the `/src/main/resources/rules/decision-tables/` directory. The decision table will determine product recommendations based on various factors such as customer demographics, purchase history, and product attributes.

## Excel File Structure

The Excel file will contain two sheets:
1. **Documentation** - Contains information about the decision table, its purpose, and how to use it
2. **Rules** - Contains the actual decision table with conditions and actions

### Documentation Sheet

The Documentation sheet will include:

- **Title**: Product Recommendation Rules
- **Description**: This decision table determines product recommendations based on various factors including customer demographics, purchase history, product attributes, and seasonal considerations.
- **Last Updated**: 2025-07-22
- **Version**: 1.0

#### Column Descriptions:

| Column Name | Description | Possible Values |
|-------------|-------------|----------------|
| RuleSet | The name of the rule set | "ProductRecommendationRules" |
| RuleId | A unique identifier for each rule | Text string (e.g., "New Customer Electronics") |
| Customer Age | The age of the customer | "between(18, 25)", "> 60" |
| Customer Gender | The gender of the customer | "== \"M\"", "== \"F\"" |
| Customer Category | The category of the customer | "== \"NEW\"", "== \"RETURNING\"" |
| Previous Purchases | Number of previous purchases | "> 5", "== 0" |
| Last Purchase Category | Category of the customer's last purchase | "== \"ELECTRONICS\"", "== \"CLOTHING\"" |
| Season | Current season | "== \"SUMMER\"", "== \"WINTER\"" |
| Inventory Level | Current inventory level of the product | "> 100", "< 20" |
| Recommended Category | The product category to recommend | "\"ELECTRONICS\"", "\"HOME_GOODS\"" |
| Recommended Product | The specific product to recommend | "\"Smartphone\"", "\"Laptop\"" |
| Discount Offer | Whether to offer a discount with the recommendation | "true", "false" |
| Recommendation Priority | The priority of the recommendation (higher values take precedence) | "1", "5", "10" |
| Recommendation Reason | The reason for the recommendation | "\"Based on purchase history\"", "\"Seasonal promotion\"" |

#### Usage Instructions:

1. Rules are evaluated and matched against customer data
2. If multiple rules match, the rule with the highest Recommendation Priority is applied
3. Empty cells in condition columns mean "don't care" (the condition is ignored)
4. Multiple conditions in a rule are combined with AND logic (all conditions must be true)

### Rules Sheet

The Rules sheet will contain the actual decision table with the following structure:

| RuleSet | RuleId | Customer Age | Customer Gender | Customer Category | Previous Purchases | Last Purchase Category | Season | Inventory Level | Recommended Category | Recommended Product | Discount Offer | Recommendation Priority | Recommendation Reason |
|---------|--------|--------------|-----------------|-------------------|-------------------|------------------------|--------|-----------------|----------------------|---------------------|---------------|-------------------------|----------------------|
| ProductRecommendationRules | New Young Male Tech | between(18, 30) | == "M" | == "NEW" |  |  |  |  | "ELECTRONICS" | "Gaming Console" | true | 100 | "Welcome offer for new young male customers" |
| ProductRecommendationRules | New Young Female Tech | between(18, 30) | == "F" | == "NEW" |  |  |  |  | "ELECTRONICS" | "Smartphone" | true | 100 | "Welcome offer for new young female customers" |
| ProductRecommendationRules | New Customer General | | | == "NEW" |  |  |  |  | "ELECTRONICS" | "Smart Speaker" | true | 90 | "Welcome offer for new customers" |
| ProductRecommendationRules | Returning Electronics Customer |  |  | == "RETURNING" | > 0 | == "ELECTRONICS" |  |  | "ELECTRONICS" | "Laptop" | false | 85 | "Based on purchase history" |
| ProductRecommendationRules | Returning Clothing Customer |  |  | == "RETURNING" | > 0 | == "CLOTHING" |  |  | "CLOTHING" | "Premium Collection" | false | 85 | "Based on purchase history" |
| ProductRecommendationRules | Returning Home Goods Customer |  |  | == "RETURNING" | > 0 | == "HOME_GOODS" |  |  | "HOME_GOODS" | "Kitchen Appliances" | false | 85 | "Based on purchase history" |
| ProductRecommendationRules | Frequent Buyer |  |  |  | > 10 |  |  |  | "PREMIUM" | "Loyalty Rewards" | true | 80 | "Reward for frequent customers" |
| ProductRecommendationRules | Summer Clothing Male |  | == "M" |  |  |  | == "SUMMER" | > 50 | "CLOTHING" | "Men's Summer Collection" | false | 75 | "Seasonal recommendation" |
| ProductRecommendationRules | Summer Clothing Female |  | == "F" |  |  |  | == "SUMMER" | > 50 | "CLOTHING" | "Women's Summer Collection" | false | 75 | "Seasonal recommendation" |
| ProductRecommendationRules | Winter Clothing Male |  | == "M" |  |  |  | == "WINTER" | > 50 | "CLOTHING" | "Men's Winter Collection" | false | 75 | "Seasonal recommendation" |
| ProductRecommendationRules | Winter Clothing Female |  | == "F" |  |  |  | == "WINTER" | > 50 | "CLOTHING" | "Women's Winter Collection" | false | 75 | "Seasonal recommendation" |
| ProductRecommendationRules | Young Adult General | between(18, 25) |  |  |  |  |  |  | "TRENDING" | "Trending Items" | false | 70 | "Age-based recommendation" |
| ProductRecommendationRules | Senior Discount | > 65 |  |  |  |  |  |  | "SPECIAL_OFFERS" | "Senior Discount Items" | true | 70 | "Age-based recommendation" |
| ProductRecommendationRules | High Inventory Clearance |  |  |  |  |  |  | > 200 | "CLEARANCE" | "Discounted Items" | true | 65 | "Inventory reduction" |
| ProductRecommendationRules | Low Inventory Premium |  |  |  |  |  |  | < 20 | "PREMIUM" | "Limited Stock Items" | false | 60 | "Scarcity-based recommendation" |

## Edge Cases and Boundary Conditions

The decision table includes the following edge cases and boundary conditions:

1. **Customer Age Boundaries**:
   - Young adults (18-30)
   - Seniors (> 65)

2. **Customer Gender Variations**:
   - Gender-specific recommendations for clothing
   - Gender-neutral recommendations for electronics

3. **Customer Category Distinctions**:
   - New customers with welcome offers
   - Returning customers with personalized recommendations based on purchase history

4. **Purchase History Boundaries**:
   - First-time buyers (Previous Purchases == 0)
   - Frequent buyers (Previous Purchases > 10)

5. **Seasonal Variations**:
   - Summer-specific recommendations
   - Winter-specific recommendations

6. **Inventory Level Boundaries**:
   - Low inventory items (< 20) promoted as premium/limited
   - High inventory items (> 200) promoted for clearance

7. **Recommendation Priority Levels**:
   - Highest priority (100) for targeted new customer recommendations
   - High priority (80-90) for returning customer and frequent buyer recommendations
   - Medium priority (65-75) for seasonal and demographic-based recommendations
   - Lower priority (60) for inventory-based recommendations

## Implementation Notes

1. The Excel file should be created with the name `ProductRecommendationRules.xlsx` in the `/src/main/resources/rules/decision-tables/` directory.
2. The Recommendation Priority column is used to control which recommendations take precedence when multiple rules match, with higher values having higher priority.
3. The decision table includes a comprehensive set of rules covering various customer scenarios and product recommendations.
4. Documentation is included both in a separate sheet and within the table structure.
5. The rules are designed to provide personalized recommendations based on customer attributes, purchase history, and current inventory levels.
6. Gender-specific recommendations are included to demonstrate demographic targeting.
7. Seasonal recommendations are included to demonstrate time-based targeting.
8. Inventory-based recommendations are included to demonstrate business-driven targeting (clearing high inventory, promoting low inventory as premium).