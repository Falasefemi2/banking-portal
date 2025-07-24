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
public class AccountBalanceDto {
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private boolean isActive;
    private LocalDateTime lastUpdated;
    private String accountHolderName;
}