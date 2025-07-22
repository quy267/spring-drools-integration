package com.example.springdroolsintegration.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration class for Micrometer metrics.
 * This class configures the MeterRegistry and related beans for metrics collection.
 */
@Configuration
public class MetricsConfig {

    /**
     * Customizes the MeterRegistry with common tags.
     *
     * @param environment The Spring environment
     * @return A MeterRegistryCustomizer
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(Environment environment) {
        return registry -> registry.config()
                .commonTags("application", "spring-drools-integration")
                .commonTags("environment", environment.getActiveProfiles().length > 0 ? 
                        environment.getActiveProfiles()[0] : "default");
    }
    
    /**
     * Creates a TimedAspect bean for @Timed annotation support.
     *
     * @param registry The MeterRegistry
     * @return A TimedAspect
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}