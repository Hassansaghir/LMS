package com.LMS.library_management.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class GetAuthorDto {
    @NotNull
    private UUID id;
    @NotBlank(message = "Author name is required")
    private String name;
    @NotNull
    private String biography;
}
