package com.example.springdroolsintegration.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Index;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date;

/**
 * Entity to store rule execution history.
 * This entity is used to demonstrate database connection pooling.
 */
@Entity
@Table(name = "rule_executions", indexes = {
    @Index(name = "idx_rule_name", columnList = "ruleName"),
    @Index(name = "idx_execution_time", columnList = "executionTime")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ruleName;

    @Column(nullable = false)
    private String rulePackage;

    @Column(nullable = false)
    private String factType;

    @Column
    private String executionResult;

    @Column(nullable = false)
    private Long executionTimeMs;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionDate;

    @Column
    private String correlationId;

    @Column
    private Boolean successful;

    @Column
    private String errorMessage;
}