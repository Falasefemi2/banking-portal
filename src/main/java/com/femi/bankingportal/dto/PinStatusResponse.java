package com.femi.bankingportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PinStatusResponse {
    private String accountNumber;
    private boolean pinCreated;
    private String message;
    private LocalDateTime checkedAt = LocalDateTime.now();
}
