package com.femi.bankingportal.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CashWithdrawalResponseDTO {

    private String accountNumber;
    private BigDecimal withdrawnAmount;
    private BigDecimal newBalance;
    private LocalDateTime transactionTime;
    private String message;
}
