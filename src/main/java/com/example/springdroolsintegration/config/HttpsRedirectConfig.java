package com.example.springdroolsintegration.config;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for HTTP to HTTPS redirection.
 * This class configures an embedded Tomcat server to listen on the HTTP port
 * and redirect all requests to HTTPS.
 */
@Configuration
@Profile("prod")
@ConditionalOnProperty(name = "server.ssl.enabled", havingValue = "true")
public class HttpsRedirectConfig {

    private static final Logger logger = LoggerFactory.getLogger(HttpsRedirectConfig.class);

    @Value("${server.http.port:8080}")
    private int httpPort;

    @Value("${server.http.interface:0.0.0.0}")
    private String httpInterface;

    @Value("${server.port:8443}")
    private int httpsPort;

    /**
     * Creates a servlet web server factory with HTTP to HTTPS redirection.
     *
     * @return The servlet web server factory
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        logger.info("Configuring HTTP to HTTPS redirect from port {} to port {}", httpPort, httpsPort);
        
        // Create Tomcat factory
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        
        // Add HTTP connector
        tomcat.addAdditionalTomcatConnectors(createHttpConnector());
        
        return tomcat;
    }

    /**
     * Creates an HTTP connector that redirects to HTTPS.
     *
     * @return The HTTP connector
     */
    private Connector createHttpConnector() {
        // Create connector
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setProperty("address", httpInterface);
        
        // Configure redirect
        connector.setRedirectPort(httpsPort);
        
        logger.info("Created HTTP connector on {}:{} redirecting to HTTPS port {}", 
                httpInterface, httpPort, httpsPort);
        
        return connector;
    }
}