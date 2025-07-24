package com.femi.bankingportal.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMoneyDto {
    @NotBlank(message = "Source account number is required")
    private String sourceAccountNumber;

    @NotBlank(message = "Target account number is required")
    private String targetAccountNumber;

    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "\\d{4,6}", message = "PIN must be 4-6 digits")
    private String pin;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @DecimalMax(value = "999999.99", message = "Amount exceeds maximum transfer limit")
    @Digits(integer = 6, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    @Size(max = 100, message = "Description cannot exceed 100 characters")
    private String description; // Optional field for transfer description

    // Custom validation to ensure accounts are different
    @AssertTrue(message = "Source and target accounts must be different")
    public boolean isDifferentAccounts() {
        return sourceAccountNumber == null || targetAccountNumber == null ||
                !sourceAccountNumber.equals(targetAccountNumber);
    }
}
