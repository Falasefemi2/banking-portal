package com.femi.bankingportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePinRequest {
    private String accountNumber;
    private String pin;
    private String password;
}
