// dto/BorrowingTransactionDTO.java
package com.LMS.library_management.Dto;

import com.LMS.library_management.Models.BorrowingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class BorrowingTransactionDTO {
    private String book_isbn;

    private UUID borrower_id;

    private LocalDate borrowDate;

    private LocalDate returnDate;

}
