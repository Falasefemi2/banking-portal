package com.femi.bankingportal.service;

import com.femi.bankingportal.dto.*;

import java.math.BigDecimal;

public interface AccountService {
    public AccountRegistrationResponseDTO createAccount(AccountRegistrationRequestDTO user);
    public boolean isPinCreated(String accountNumber, Long userId);
    public void createPin(String accountNumber, String password, String pin) ;
    public void updatePin(String accountNumber, String oldPIN, String password, String newPIN);
    public TransactionResponse cashDepositWithResponse(String accountNumber, String pin, BigDecimal amount, Long userId);
    public TransactionResponse cashWithdrawal(String accountNumber, String pin, BigDecimal amount, Long userId);
<<<<<<< HEAD
   public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, BigDecimal amount);
=======
    public String fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, BigDecimal amount, Long userId);
    public AccountInfoDto getAccountInfo(String accountNumber);
    public BigDecimal getAccountBalance(String accountNumber, Long userId);
    public AccountBalanceDto getAccountBalanceDetails(String accountNumber, Long userId);
    public BigDecimal getAccountBalancex(String accountNumber);
>>>>>>> caa614e (feat: Tranfer between account and get account details)
}
