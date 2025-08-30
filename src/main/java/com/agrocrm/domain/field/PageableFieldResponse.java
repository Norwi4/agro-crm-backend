package com.agrocrm.domain.field;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Пагинированный ответ со списком полей")
public class PageableFieldResponse {
    
    @Schema(description = "Список полей на текущей странице")
    private List<Field> content;
    
    @Schema(description = "Мета-информация о пагинации")
    private PageMetadata metadata;
    
    public PageableFieldResponse(List<Field> content, PageMetadata metadata) {
        this.content = content;
        this.metadata = metadata;
    }
    
    public List<Field> getContent() {
        return content;
    }
    
    public void setContent(List<Field> content) {
        this.content = content;
    }
    
    public PageMetadata getMetadata() {
        return metadata;
    }
    
    public void setMetadata(PageMetadata metadata) {
        this.metadata = metadata;
    }
    
    @Schema(description = "Мета-информация о пагинации")
    public static class PageMetadata {
        
        @Schema(description = "Общее количество элементов", example = "150")
        private long totalElements;
        
        @Schema(description = "Общее количество страниц", example = "15")
        private int totalPages;
        
        @Schema(description = "Номер текущей страницы (начиная с 0)", example = "0")
        private int currentPage;
        
        @Schema(description = "Размер страницы", example = "10")
        private int pageSize;
        
        @Schema(description = "Смещение от начала", example = "0")
        private long offset;
        
        @Schema(description = "Количество элементов на текущей странице", example = "10")
        private int numberOfElements;
        
        @Schema(description = "Ссылки навигации")
        private NavigationLinks navigationLinks;
        
        public PageMetadata(long totalElements, int totalPages, int currentPage, int pageSize, 
                          long offset, int numberOfElements, NavigationLinks navigationLinks) {
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.offset = offset;
            this.numberOfElements = numberOfElements;
            this.navigationLinks = navigationLinks;
        }
        
        // Getters and setters
        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
        
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
        
        public int getCurrentPage() { return currentPage; }
        public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
        
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
        
        public long getOffset() { return offset; }
        public void setOffset(long offset) { this.offset = offset; }
        
        public int getNumberOfElements() { return numberOfElements; }
        public void setNumberOfElements(int numberOfElements) { this.numberOfElements = numberOfElements; }
        
        public NavigationLinks getNavigationLinks() { return navigationLinks; }
        public void setNavigationLinks(NavigationLinks navigationLinks) { this.navigationLinks = navigationLinks; }
    }
    
    @Schema(description = "Ссылки навигации по страницам")
    public static class NavigationLinks {
        
        @Schema(description = "Ссылка на первую страницу", example = "/api/fields?page=0&size=10")
        private String first;
        
        @Schema(description = "Ссылка на предыдущую страницу", example = "/api/fields?page=0&size=10")
        private String previous;
        
        @Schema(description = "Ссылка на текущую страницу", example = "/api/fields?page=1&size=10")
        private String current;
        
        @Schema(description = "Ссылка на следующую страницу", example = "/api/fields?page=2&size=10")
        private String next;
        
        @Schema(description = "Ссылка на последнюю страницу", example = "/api/fields?page=14&size=10")
        private String last;
        
        public NavigationLinks(String first, String previous, String current, String next, String last) {
            this.first = first;
            this.previous = previous;
            this.current = current;
            this.next = next;
            this.last = last;
        }
        
        // Getters and setters
        public String getFirst() { return first; }
        public void setFirst(String first) { this.first = first; }
        
        public String getPrevious() { return previous; }
        public void setPrevious(String previous) { this.previous = previous; }
        
        public String getCurrent() { return current; }
        public void setCurrent(String current) { this.current = current; }
        
        public String getNext() { return next; }
        public void setNext(String next) { this.next = next; }
        
        public String getLast() { return last; }
        public void setLast(String last) { this.last = last; }
    }
}
