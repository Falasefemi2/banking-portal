package com.femi.bankingportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePinRequest {
    private String accountNumber;
    private String oldPIN;
    private String newPIN;
    private String password;
}
