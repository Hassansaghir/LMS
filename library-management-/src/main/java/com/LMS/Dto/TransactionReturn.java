package com.LMS.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Data
public class TransactionReturn {
    private String book_isbn;

    private UUID borrower_id;

    private LocalDate borrowDate;

    private LocalDate returnDate;

    private BigDecimal bookPrice;
}
