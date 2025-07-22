package com.example.springdroolsintegration.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the application.
 * This class configures CORS, CSRF, and other security settings.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${app.security.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${app.security.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${app.security.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${app.security.cors.exposed-headers:Content-Disposition,X-Correlation-ID}")
    private String exposedHeaders;

    @Value("${app.security.cors.max-age:3600}")
    private long maxAge;

    /**
     * Configures the security filter chain.
     * This method sets up CORS, CSRF, and other security settings.
     *
     * @param http The HttpSecurity to configure
     * @return The configured SecurityFilterChain
     * @throws Exception if there is an error configuring security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain with CORS settings");
        
        return http
                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Disable CSRF for REST APIs
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configure session management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Configure authorization
                .authorizeHttpRequests(auth -> auth
                        // Allow access to API endpoints
                        .requestMatchers("/api/**").permitAll()
                        
                        // Allow access to Swagger UI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        
                        // Allow access to Actuator endpoints
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        
                        // Require authentication for all other requests
                        .anyRequest().authenticated()
                )
                .build();
    }

    /**
     * Configures CORS settings.
     * This method sets up allowed origins, methods, headers, and other CORS settings.
     *
     * @return The CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        if ("*".equals(allowedOrigins)) {
            configuration.setAllowedOriginPatterns(List.of("*"));
        } else {
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        }
        
        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        
        // Set allowed headers
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        
        // Set exposed headers
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders.split(",")));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Set max age
        configuration.setMaxAge(maxAge);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        logger.info("CORS configuration: allowedOrigins={}, allowedMethods={}", 
                allowedOrigins, allowedMethods);
        
        return source;
    }
}