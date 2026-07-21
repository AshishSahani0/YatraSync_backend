package com.example.backend.destinations.getDestination;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Stable, serialization-safe wrapper for paginated responses.
 * <p>
 * Spring's {@code PageImpl} warns that its JSON structure is unstable.
 * This DTO provides a guaranteed, explicit contract that matches the
 * frontend's {@code PageResponse<T>} TypeScript interface exactly.
 * </p>
 *
 * @param <T> the type of content items
 */
public class PageResponse<T> {

    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;           // current page index (0-based)
    private boolean first;
    private boolean last;
    private boolean empty;
    private int numberOfElements; // items on current page

    // ─── Constructors ─────────────────────────────────────────────────────────

    public PageResponse() {}

    public PageResponse(
            List<T> content,
            int totalPages,
            long totalElements,
            int size,
            int number,
            boolean first,
            boolean last,
            boolean empty,
            int numberOfElements) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
        this.number = number;
        this.first = first;
        this.last = last;
        this.empty = empty;
        this.numberOfElements = numberOfElements;
    }

    // ─── Factory method ───────────────────────────────────────────────────────

    /**
     * Converts a Spring Data {@link Page} into this stable DTO.
     * Eliminates the unstable PageImpl serialization warning.
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty(),
                page.getNumberOfElements()
        );
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }

    public boolean isEmpty() { return empty; }
    public void setEmpty(boolean empty) { this.empty = empty; }

    public int getNumberOfElements() { return numberOfElements; }
    public void setNumberOfElements(int numberOfElements) { this.numberOfElements = numberOfElements; }
}
