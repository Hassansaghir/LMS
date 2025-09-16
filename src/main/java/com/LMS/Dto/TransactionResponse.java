package com.LMS.Dto;

import lombok.Data;

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

