package com.example.springdroolsintegration.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for RuleManagementController.
 * These tests verify that the rule management endpoints work correctly,
 * with a focus on file upload security.
 */
public class RuleManagementControllerTest extends BaseApiIntegrationTest {

    private static final String UPLOAD_ENDPOINT = "/api/v1/rules/upload";
    private static final String VALIDATE_ENDPOINT = "/api/v1/rules/validate";
    private static final String STATUS_ENDPOINT = "/api/v1/rules/status";
    private static final String RELOAD_ENDPOINT = "/api/v1/rules/reload";

    /**
     * Performs a multipart file upload request.
     *
     * @param url The URL to send the request to
     * @param file The file to upload
     * @param params Additional request parameters
     * @return The result actions for further assertions
     * @throws Exception If an error occurs during the request
     */
    protected ResultActions performFileUpload(String url, MockMultipartFile file, String... params) throws Exception {
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(url)
                .file(file);

        for (int i = 0; i < params.length; i += 2) {
            if (i + 1 < params.length) {
                requestBuilder.param(params[i], params[i + 1]);
            }
        }

        return mockMvc.perform(requestBuilder).andDo(print());
    }

    @Test
    @DisplayName("Test rule upload endpoint with a valid DRL file")
    public void testUploadValidDrlFile() throws Exception {
        // Create a valid DRL file
        String drlContent = "package com.example.rules;\n\n" +
                "rule \"Test Rule\"\n" +
                "when\n" +
                "    $customer : Customer(age > 60)\n" +
                "then\n" +
                "    $customer.setDiscount(10);\n" +
                "end";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-rule.drl",
                MediaType.TEXT_PLAIN_VALUE,
                drlContent.getBytes(StandardCharsets.UTF_8)
        );

        // Perform the upload
        performFileUpload(UPLOAD_ENDPOINT, file, "version", "1.0.0")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", is("test-rule.drl")))
                .andExpect(jsonPath("$.version", is("1.0.0")));
    }

    @Test
    @DisplayName("Test rule upload endpoint with an invalid file type")
    public void testUploadInvalidFileType() throws Exception {
        // Create a file with an unsupported extension
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a text file, not a rule file.".getBytes(StandardCharsets.UTF_8)
        );

        // Perform the upload
        performFileUpload(UPLOAD_ENDPOINT, file)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid", is(false)))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Unsupported file extension")));
    }

    @Test
    @DisplayName("Test rule upload endpoint with an empty file")
    public void testUploadEmptyFile() throws Exception {
        // Create an empty file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty-rule.drl",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        // Perform the upload
        performFileUpload(UPLOAD_ENDPOINT, file)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid", is(false)))
                .andExpect(jsonPath("$.message", is("File is empty")));
    }

    @Test
    @DisplayName("Test rule upload endpoint with a large file")
    public void testUploadLargeFile() throws Exception {
        // Create a large file (6MB, which exceeds the default 5MB limit)
        byte[] largeContent = new byte[6 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large-rule.drl",
                MediaType.TEXT_PLAIN_VALUE,
                largeContent
        );

        // Perform the upload - should fail with 413 Payload Too Large
        performFileUpload(UPLOAD_ENDPOINT, file)
                .andExpect(status().isPayloadTooLarge());
    }

    @Test
    @DisplayName("Test rule validation endpoint with a valid DRL file")
    public void testValidateValidDrlFile() throws Exception {
        // Create a valid DRL file
        String drlContent = "package com.example.rules;\n\n" +
                "rule \"Test Rule\"\n" +
                "when\n" +
                "    $customer : Customer(age > 60)\n" +
                "then\n" +
                "    $customer.setDiscount(10);\n" +
                "end";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-rule.drl",
                MediaType.TEXT_PLAIN_VALUE,
                drlContent.getBytes(StandardCharsets.UTF_8)
        );

        // Perform the validation
        performFileUpload(VALIDATE_ENDPOINT, file)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid", is(true)))
                .andExpect(jsonPath("$.message", is("Rule file is valid")));
    }

    @Test
    @DisplayName("Test rule validation endpoint with an invalid DRL file")
    public void testValidateInvalidDrlFile() throws Exception {
        // Create an invalid DRL file with syntax errors
        String drlContent = "package com.example.rules;\n\n" +
                "rule \"Test Rule\"\n" +
                "when\n" +
                "    $customer : Customer(age > 60\n" + // Missing closing parenthesis
                "then\n" +
                "    $customer.setDiscount(10);\n" +
                "end";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid-rule.drl",
                MediaType.TEXT_PLAIN_VALUE,
                drlContent.getBytes(StandardCharsets.UTF_8)
        );

        // Perform the validation
        performFileUpload(VALIDATE_ENDPOINT, file)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid", is(false)))
                .andExpect(jsonPath("$.message", is("Rule compilation failed")));
    }

    @Test
    @DisplayName("Test rule validation endpoint with an invalid Excel file")
    public void testValidateInvalidExcelFile() throws Exception {
        // Create a mock Excel file that's not a valid decision table
        byte[] excelContent = new byte[1024]; // Just some random bytes, not a real Excel file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid-table.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelContent
        );

        // Perform the validation
        performFileUpload(VALIDATE_ENDPOINT, file)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid", is(false)));
    }

    @Test
    @DisplayName("Test rule status endpoint")
    public void testGetRuleStatus() throws Exception {
        performGet(STATUS_ENDPOINT)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kieBaseName").exists())
                .andExpect(jsonPath("$.kieSessionName").exists())
                .andExpect(jsonPath("$.rulePath").exists())
                .andExpect(jsonPath("$.decisionTablePath").exists());
    }

    @Test
    @DisplayName("Test rule reload endpoint")
    public void testReloadRules() throws Exception {
        performPost(RELOAD_ENDPOINT, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.message").exists());
    }
}