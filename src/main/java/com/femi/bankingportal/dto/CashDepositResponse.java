package com.femi.bankingportal.dto;

import com.femi.bankingportal.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashDepositResponse {
    private String accountNumber;
    private BigDecimal transactionAmount;
    private BigDecimal newBalance;
    private LocalDateTime transactionTime;
    private String message;
    private String transactionReference;
    private TransactionType transactionType;
}