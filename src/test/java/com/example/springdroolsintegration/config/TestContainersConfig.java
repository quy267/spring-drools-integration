package com.example.springdroolsintegration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

/**
 * TestContainers configuration for integration tests.
 * This class sets up a PostgreSQL container for database integration tests.
 */
@TestConfiguration
public class TestContainersConfig {

    /**
     * PostgreSQL container for integration tests.
     * This container is shared among all tests that use this configuration.
     */
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = 
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    /**
     * Start the PostgreSQL container before any tests run.
     */
    static {
        postgreSQLContainer.start();
    }

    /**
     * Configure Spring to use the PostgreSQL container for database operations.
     * This method is called by Spring to set the dynamic properties for the test context.
     *
     * @param registry The dynamic property registry
     */
    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        
        // Configure Hibernate to create the schema automatically
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    /**
     * Provides the PostgreSQL container as a bean.
     *
     * @return The PostgreSQL container
     */
    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return postgreSQLContainer;
    }
}