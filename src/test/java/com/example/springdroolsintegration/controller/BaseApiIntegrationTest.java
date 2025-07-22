package com.example.springdroolsintegration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Base class for API integration tests.
 * This class provides common setup and utility methods for testing REST controllers.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseApiIntegrationTest {
    
    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    @BeforeEach
    public void setUp() {
        // Common setup for all API tests
    }
    
    /**
     * Performs a GET request to the specified URL.
     *
     * @param url The URL to send the GET request to
     * @return The result actions for further assertions
     * @throws Exception If an error occurs during the request
     */
    protected ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
    
    /**
     * Performs a GET request to the specified URL with query parameters.
     *
     * @param url The URL to send the GET request to
     * @param params The query parameters as key-value pairs
     * @return The result actions for further assertions
     * @throws Exception If an error occurs during the request
     */
    protected ResultActions performGet(String url, String... params) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(url)
                .contentType(MediaType.APPLICATION_JSON);
        
        for (int i = 0; i < params.length; i += 2) {
            if (i + 1 < params.length) {
                requestBuilder.param(params[i], params[i + 1]);
            }
        }
        
        return mockMvc.perform(requestBuilder).andDo(print());
    }
    
    /**
     * Performs a POST request to the specified URL with the given content.
     *
     * @param url The URL to send the POST request to
     * @param content The content to send in the request body
     * @return The result actions for further assertions
     * @throws Exception If an error occurs during the request
     */
    protected ResultActions performPost(String url, Object content) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)))
                .andDo(print());
    }
    
    /**
     * Performs a POST request to the specified URL with the given content and query parameters.
     *
     * @param url The URL to send the POST request to
     * @param content The content to send in the request body
     * @param params The query parameters as key-value pairs
     * @return The result actions for further assertions
     * @throws Exception If an error occurs during the request
     */
    protected ResultActions performPost(String url, Object content, String... params) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content));
        
        for (int i = 0; i < params.length; i += 2) {
            if (i + 1 < params.length) {
                requestBuilder.param(params[i], params[i + 1]);
            }
        }
        
        return mockMvc.perform(requestBuilder).andDo(print());
    }
}