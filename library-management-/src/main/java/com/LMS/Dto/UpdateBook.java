package com.LMS.Dto;

import com.LMS.Models.Category;
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
