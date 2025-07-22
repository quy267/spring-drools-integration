package com.example.springdroolsintegration.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Generic paged response for batch operations.
 * This class provides a standard structure for paginated responses.
 *
 * @param <T> The type of items in the page
 */
@Schema(description = "Paginated response for batch operations")
public class PagedResponse<T> {
    
    /**
     * The content of the current page
     */
    @Schema(description = "Content of the current page")
    private List<T> content;
    
    /**
     * The current page number (0-based)
     */
    @Schema(description = "Current page number (0-based)", example = "0")
    private int page;
    
    /**
     * The size of the page
     */
    @Schema(description = "Size of the page", example = "20")
    private int size;
    
    /**
     * The total number of elements across all pages
     */
    @Schema(description = "Total number of elements across all pages", example = "100")
    private long totalElements;
    
    /**
     * The total number of pages
     */
    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;
    
    /**
     * Whether this is the first page
     */
    @Schema(description = "Whether this is the first page", example = "true")
    private boolean first;
    
    /**
     * Whether this is the last page
     */
    @Schema(description = "Whether this is the last page", example = "false")
    private boolean last;
    
    /**
     * Default constructor
     */
    public PagedResponse() {
    }
    
    /**
     * Constructor with all fields
     *
     * @param content The content of the current page
     * @param page The current page number (0-based)
     * @param size The size of the page
     * @param totalElements The total number of elements across all pages
     */
    public PagedResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        this.first = page == 0;
        this.last = page >= totalPages - 1;
    }
    
    // Getters and setters
    
    public List<T> getContent() {
        return content;
    }
    
    public void setContent(List<T> content) {
        this.content = content;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
        this.first = page == 0;
        this.last = totalPages > 0 && page >= totalPages - 1;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        this.last = totalPages > 0 && page >= totalPages - 1;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        this.last = totalPages > 0 && page >= totalPages - 1;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public boolean isFirst() {
        return first;
    }
    
    public boolean isLast() {
        return last;
    }
    
    /**
     * Creates a paged response from a list of items and pagination parameters.
     *
     * @param <T> The type of items in the page
     * @param items The complete list of items
     * @param page The requested page number (0-based)
     * @param size The requested page size
     * @return A paged response containing the requested page of items
     */
    public static <T> PagedResponse<T> of(List<T> items, int page, int size) {
        // Validate pagination parameters
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10; // Default page size
        }
        
        // Calculate total elements
        long totalElements = items.size();
        
        // Calculate start and end indices for the requested page
        int start = page * size;
        int end = Math.min(start + size, items.size());
        
        // Get the sublist for the requested page
        List<T> pageContent = start < items.size() ? items.subList(start, end) : List.of();
        
        // Create and return the paged response
        return new PagedResponse<>(pageContent, page, size, totalElements);
    }
}