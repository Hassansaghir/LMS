package com.LMS.Dto;

import com.LMS.Models.Category;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CreateBook {
    private String title;

    private String isbn;

    private Category category;

    private BigDecimal price;

    private boolean available;

    private double extra_days_rental_price;

    private double insurance_fees;

}
