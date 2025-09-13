package com.LMS.library_management.Dto;

import com.LMS.library_management.Models.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class BookResponse {
    private Map<String, BookInfo> books; // ISBN as key

    @Data
    public static class BookInfo {
        private String title;
        private List<AuthorInfo> authors;
    }

    @Data
    public static class AuthorInfo {
        private String name;
        private String url;
    }
}