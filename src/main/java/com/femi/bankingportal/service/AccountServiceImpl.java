package com.femi.bankingportal.service;

import com.femi.bankingportal.dto.AccountRegistrationRequestDTO;
import com.femi.bankingportal.dto.AccountRegistrationResponseDTO;
import com.femi.bankingportal.dto.TransactionResponse;
import com.femi.bankingportal.model.*;
import com.femi.bankingportal.repository.AccountRepository;
import com.femi.bankingportal.repository.TransactionRepository;
import com.femi.bankingportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public AccountRegistrationResponseDTO createAccount(AccountRegistrationRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));

        Account account = Account.builder()
                .accountNumber(generateUniqueAccountNumber())
                .accountType(dto.getAccountType())
                .balance(dto.getInitialBalance() != null ? dto.getInitialBalance() : BigDecimal.ZERO)
                .user(user)
                .isActive(true)
                .transactions(new ArrayList<>())
                .build();

        if (dto.getInitialBalance() != null && dto.getInitialBalance().compareTo(BigDecimal.ZERO) > 0) {
            Transaction initialDeposit = Transaction.builder()
                    .account(account)
                    .type(TransactionType.DEPOSIT)
                    .amount(dto.getInitialBalance())
                    .timestamp(LocalDateTime.now())
                    .build();
            account.getTransactions().add(initialDeposit);
            account = accountRepository.save(account);
        }

        return AccountRegistrationResponseDTO.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .build();

    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "ACC-" + UUID.randomUUID().toString().substring(0, 10).replace("-", "");
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }

    @Override
    public boolean isPinCreated(String accountNumber, Long userId) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied: Account does not belong to user");
        }

        User user = account.getUser();
        return user.getPin() != null && !user.getPin().isEmpty();
    }

    @Override
    public void createPin(String accountNumber, String password, String pin) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        User user = account.getUser();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (user.getPin() != null && !user.getPin().isEmpty()) {
            throw new RuntimeException("PIN already exists. Use updatePin to change it.");
        }

        user.setPin(passwordEncoder.encode(pin));
        userRepository.save(user);
    }

    @Override
    public void updatePin(String accountNumber, String oldPIN, String password, String newPIN) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        User user = account.getUser();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (user.getPin() == null || !passwordEncoder.matches(oldPIN, user.getPin())) {
            throw new RuntimeException("Invalid current PIN");
        }

        user.setPin(passwordEncoder.encode(newPIN));
        userRepository.save(user);
    }

    @Override
    public TransactionResponse cashDepositWithResponse(String accountNumber, String pin, BigDecimal amount, Long userId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied: Account does not belong to user");
        }

        if (!account.isActive()) {
            throw new RuntimeException("Account is not active");
        }


        User user = account.getUser();

        if (user.getPin() == null || user.getPin().isEmpty()) {
            throw new RuntimeException("PIN not set for this account");
        }

        if (!passwordEncoder.matches(pin, user.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        account.setBalance(account.getBalance().add(amount));

        Transaction depositTransaction = Transaction.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();

        account.getTransactions().add(depositTransaction);
        accountRepository.save(account);

        return TransactionResponse.builder()
                .transactionId(depositTransaction.getId())
                .accountNumber(accountNumber)
                .type("DEPOSIT")
                .amount(amount)
                .newBalance(account.getBalance())
                .timestamp(depositTransaction.getTimestamp())
                .description("Cash Deposit")
                .build();
    }

    @Override
    public TransactionResponse cashWithdrawal(String accountNumber, String pin, BigDecimal amount, Long userId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied: Account does not belong to user");
        }

        if (!account.isActive()) {
            throw new RuntimeException("Account is not active");
        }

        User user = account.getUser();

        if (user.getPin() == null || user.getPin().isEmpty()) {
            throw new RuntimeException("PIN not set for this account");
        }

        if (!passwordEncoder.matches(pin, user.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));

        Transaction withdrawTransaction = Transaction.builder()
                .account(account)
                .type(TransactionType.WITHDRAW)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();

        account.getTransactions().add(withdrawTransaction);
        accountRepository.save(account);

        return TransactionResponse.builder()
                .transactionId(withdrawTransaction.getId())
                .accountNumber(accountNumber)
                .type("DEPOSIT")
                .amount(amount)
                .newBalance(account.getBalance())
                .timestamp(withdrawTransaction.getTimestamp())
                .description("Cash Deposit")
                .build();
    }

    public BigDecimal getAccountBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        return account.getBalance();
    }

    @Override
    @Transactional
    public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (sourceAccountNumber.equals(targetAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() -> new RuntimeException("Source account not found: " + sourceAccountNumber));

        if (!sourceAccount.isActive()) {
            throw new RuntimeException("Source account is not active");
        }

        Account targetAccount = accountRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new RuntimeException("Target account not found: " + targetAccountNumber));

        if (!targetAccount.isActive()) {
            throw new RuntimeException("Target account is not active");
        }

        User sourceUser = sourceAccount.getUser();
        if (sourceUser.getPin() == null || sourceUser.getPin().isEmpty()) {
            throw new RuntimeException("PIN not set for source account");
        }

        if (!passwordEncoder.matches(pin, sourceUser.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        targetAccount.setBalance(targetAccount.getBalance().add(amount));

//        String transferReference = generateTransferReference();

        Transaction debitTransaction = Transaction.builder()
                .account(sourceAccount)
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .timestamp(LocalDateTime.now())
//                .description("Transfer to " + targetAccountNumber)
//                .referenceNumber(transferReference)
                .build();

        Transaction creditTransaction = Transaction.builder()
                .account(targetAccount)
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .timestamp(LocalDateTime.now())
//                .description("Transfer from " + sourceAccountNumber)
//                .referenceNumber(transferReference)
                .build();

        sourceAccount.getTransactions().add(debitTransaction);
        targetAccount.getTransactions().add(creditTransaction);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
    }

    private String generateTransferReference() {
        return "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}
