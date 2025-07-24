package com.femi.bankingportal.dto;

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
public class FundTransferResponse {
    private String sourceAccount;
    private String targetAccount;
    private BigDecimal transferAmount;
    private BigDecimal sourceAccountPreviousBalance;
    private BigDecimal sourceAccountNewBalance;
    private BigDecimal targetAccountPreviousBalance;
    private BigDecimal targetAccountNewBalance;
    private LocalDateTime transferTime;
    private String message;
    private String status;
    private String description;
}