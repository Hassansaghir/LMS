package com.LMS.library_management.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AuthorDTO {
    @NotBlank(message = "Author name is required")
    private String name;
    @NotNull
    private String biography;


}
