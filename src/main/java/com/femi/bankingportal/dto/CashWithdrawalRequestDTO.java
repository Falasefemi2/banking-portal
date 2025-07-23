package com.femi.bankingportal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashWithdrawalRequestDTO {

    @NotEmpty(message = "Account number cannot be empty")
    private String accountNumber;

    @NotEmpty(message = "PIN cannot be empty")
    private String pin;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;
}
