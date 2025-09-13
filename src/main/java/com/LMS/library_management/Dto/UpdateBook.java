package com.LMS.library_management.Dto;

import com.LMS.library_management.Models.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateBook {

    private String title;

    private BigDecimal price;

    private double extra_days_rental_price;

    private double insurance_fees;

    private Category category;

    private boolean available;
}
