package com.femi.bankingportal.service;

import com.femi.bankingportal.dto.AccountRegistrationRequestDTO;
import com.femi.bankingportal.dto.AccountRegistrationResponseDTO;

import java.math.BigDecimal;

public interface AccountService {
    public AccountRegistrationResponseDTO createAccount(AccountRegistrationRequestDTO user);
    public boolean isPinCreated(String accountNumber, Long userId);
    public void createPin(String accountNumber, String password, String pin) ;
    public void updatePin(String accountNumber, String oldPIN, String password, String newPIN);
    public void cashDeposit(String accountNumber, String pin, BigDecimal amount);
    public void cashWithdrawal(String accountNumber, String pin, BigDecimal amount);
    public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, BigDecimal amount);
}
