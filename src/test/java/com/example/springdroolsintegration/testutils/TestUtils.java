package com.example.springdroolsintegration.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Utility class providing common functionality for unit and integration tests.
 * Contains helper methods for JSON serialization, random data generation, 
 * date/time utilities, and test assertions.
 */
public final class TestUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    private static final Random random = new Random();
    
    private TestUtils() {
        // Utility class - prevent instantiation
    }
    
    // JSON Utilities
    
    /**
     * Converts an object to JSON string.
     * @param object the object to serialize
     * @return JSON string representation
     * @throws RuntimeException if serialization fails
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }
    
    /**
     * Converts JSON string to object of specified type.
     * @param json the JSON string
     * @param clazz the target class type
     * @param <T> the type parameter
     * @return deserialized object
     * @throws RuntimeException if deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to object", e);
        }
    }
    
    /**
     * Extracts response content from MvcResult as string.
     * @param result the MvcResult from MockMvc
     * @return response content as string
     * @throws RuntimeException if content extraction fails
     */
    public static String getResponseContent(MvcResult result) {
        try {
            return result.getResponse().getContentAsString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to extract response content", e);
        }
    }
    
    /**
     * Extracts and deserializes response content from MvcResult.
     * @param result the MvcResult from MockMvc
     * @param clazz the target class type
     * @param <T> the type parameter
     * @return deserialized response object
     */
    public static <T> T getResponseObject(MvcResult result, Class<T> clazz) {
        String content = getResponseContent(result);
        return fromJson(content, clazz);
    }
    
    // Random Data Generation
    
    /**
     * Generates a random string of specified length.
     * @param length the desired length
     * @return random string
     */
    public static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * Generates a random email address.
     * @return random email address
     */
    public static String randomEmail() {
        return randomString(8).toLowerCase() + "@" + randomString(6).toLowerCase() + ".com";
    }
    
    /**
     * Generates a random phone number in format XXX-XXX-XXXX.
     * @return random phone number
     */
    public static String randomPhoneNumber() {
        return String.format("%03d-%03d-%04d", 
                random.nextInt(900) + 100,
                random.nextInt(900) + 100,
                random.nextInt(9000) + 1000);
    }
    
    /**
     * Generates a random SSN in format XXX-XX-XXXX.
     * @return random SSN
     */
    public static String randomSSN() {
        return String.format("%03d-%02d-%04d",
                random.nextInt(900) + 100,
                random.nextInt(90) + 10,
                random.nextInt(9000) + 1000);
    }
    
    /**
     * Generates a random integer between min and max (inclusive).
     * @param min minimum value
     * @param max maximum value
     * @return random integer
     */
    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    
    /**
     * Generates a random double between min and max.
     * @param min minimum value
     * @param max maximum value
     * @return random double
     */
    public static double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
    
    /**
     * Generates a random boolean value.
     * @return random boolean
     */
    public static boolean randomBoolean() {
        return random.nextBoolean();
    }
    
    /**
     * Selects a random element from the provided list.
     * @param items the list to select from
     * @param <T> the type parameter
     * @return random element from the list
     * @throws IllegalArgumentException if list is empty
     */
    public static <T> T randomElement(List<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot select from empty list");
        }
        return items.get(random.nextInt(items.size()));
    }
    
    /**
     * Selects a random element from the provided array.
     * @param items the array to select from
     * @param <T> the type parameter
     * @return random element from the array
     * @throws IllegalArgumentException if array is empty
     */
    @SafeVarargs
    public static <T> T randomElement(T... items) {
        if (items.length == 0) {
            throw new IllegalArgumentException("Cannot select from empty array");
        }
        return items[random.nextInt(items.length)];
    }
    
    // Date/Time Utilities
    
    /**
     * Generates a random date within the last year.
     * @return random LocalDate
     */
    public static LocalDate randomDateWithinLastYear() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = now.minusYears(1);
        long daysBetween = oneYearAgo.toEpochDay() - now.toEpochDay();
        long randomDays = random.nextLong() % Math.abs(daysBetween);
        return oneYearAgo.plusDays(randomDays);
    }
    
    /**
     * Generates a random date of birth for a person of specified age.
     * @param age the desired age
     * @return random date of birth
     */
    public static LocalDate randomDateOfBirth(int age) {
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = now.minusYears(age + 1);
        LocalDate endOfYear = now.minusYears(age);
        long daysBetween = endOfYear.toEpochDay() - startOfYear.toEpochDay();
        long randomDays = random.nextLong() % daysBetween;
        return startOfYear.plusDays(Math.abs(randomDays));
    }
    
    /**
     * Generates a random LocalDateTime within the last month.
     * @return random LocalDateTime
     */
    public static LocalDateTime randomDateTimeWithinLastMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        long secondsBetween = oneMonthAgo.toEpochSecond(java.time.ZoneOffset.UTC) - 
                             now.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomSeconds = random.nextLong() % Math.abs(secondsBetween);
        return oneMonthAgo.plusSeconds(randomSeconds);
    }
    
    /**
     * Formats LocalDate to string using ISO format.
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    /**
     * Formats LocalDateTime to string using ISO format.
     * @param dateTime the datetime to format
     * @return formatted datetime string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    // ID Generation
    
    /**
     * Generates a random UUID string.
     * @return random UUID string
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generates a random ID with specified prefix.
     * @param prefix the prefix for the ID
     * @return random ID with prefix
     */
    public static String randomIdWithPrefix(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Validation Utilities
    
    /**
     * Checks if a string is null or empty.
     * @param str the string to check
     * @return true if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Checks if a string is not null and not empty.
     * @param str the string to check
     * @return true if string is not null and not empty
     */
    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }
    
    /**
     * Validates that a value is within the specified range (inclusive).
     * @param value the value to check
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return true if value is within range
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validates that an integer value is within the specified range (inclusive).
     * @param value the value to check
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return true if value is within range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    // Test Data Cleanup
    
    /**
     * Sleeps for the specified number of milliseconds.
     * Useful for testing time-dependent functionality.
     * @param milliseconds the number of milliseconds to sleep
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
    }
    
    /**
     * Creates a deep copy of an object using JSON serialization/deserialization.
     * @param object the object to copy
     * @param clazz the class type
     * @param <T> the type parameter
     * @return deep copy of the object
     */
    public static <T> T deepCopy(T object, Class<T> clazz) {
        String json = toJson(object);
        return fromJson(json, clazz);
    }
    
    /**
     * Rounds a double value to specified number of decimal places.
     * @param value the value to round
     * @param decimalPlaces number of decimal places
     * @return rounded value
     */
    public static double round(double value, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(value * scale) / scale;
    }
}