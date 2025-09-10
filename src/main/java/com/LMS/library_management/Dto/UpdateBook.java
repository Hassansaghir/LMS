package com.LMS.library_management.Dto;

import com.LMS.library_management.Models.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBook {

    private String title;


    private Category category;

    private boolean available;
}
