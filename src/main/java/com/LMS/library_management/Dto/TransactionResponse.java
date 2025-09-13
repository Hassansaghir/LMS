package com.LMS.library_management.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class TransactionResponse {
    private UUID id;
    private BigDecimal transactionAmount;
    private Type transactionType;
    private String cardNumber;
    private LocalDateTime createdAt;
    private BigDecimal balance;
}

