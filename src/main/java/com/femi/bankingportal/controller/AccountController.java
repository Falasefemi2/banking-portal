package com.femi.bankingportal.controller;

import com.femi.bankingportal.dto.*;
import com.femi.bankingportal.model.TransactionType;
import com.femi.bankingportal.model.User;
import com.femi.bankingportal.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/register")
    public ResponseEntity<AccountRegistrationResponseDTO> createAccount(@Valid @RequestBody AccountRegistrationRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        if (!user.getId().equals(dto.getUserId())) {
            throw new AccessDeniedException("Cannot create account for another user");
        }
        return ResponseEntity.ok(accountService.createAccount(dto));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/cash-deposit")
    public ResponseEntity<CashDepositResponse> cashDeposit(@Valid @RequestBody CashDepositDto dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) auth.getPrincipal();

        TransactionResponse transactionResponse = accountService.cashDepositWithResponse(
                dto.getAccountNumber(),
                dto.getPin(),
                dto.getAmount(),
                authenticatedUser.getId()
        );

        CashDepositResponse response = CashDepositResponse.builder()
                .accountNumber(dto.getAccountNumber())
                .transactionAmount(dto.getAmount())
                .newBalance(transactionResponse.getNewBalance())
                .transactionTime(LocalDateTime.now())
                .message("Cash deposit successful")
                .transactionType(TransactionType.DEPOSIT)
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/cash-withdraw")
    public ResponseEntity<CashDepositResponse> cashWithdraw(@Valid @RequestBody CashDepositDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) auth.getPrincipal();

        TransactionResponse transactionResponse = accountService.cashWithdrawal(
                dto.getAccountNumber(),
                dto.getPin(),
                dto.getAmount(),
                authenticatedUser.getId()
        );

        CashDepositResponse response = CashDepositResponse.builder()
                .accountNumber(dto.getAccountNumber())
                .transactionAmount(dto.getAmount())
                .newBalance(transactionResponse.getNewBalance())
                .transactionTime(LocalDateTime.now())
                .message("Cash withdraw successful")
                .transactionType(TransactionType.WITHDRAW)
                .build();

        return ResponseEntity.ok(response);

    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-pin")
    public ResponseEntity<Map<String, String>> createPin(@Valid @RequestBody CreatePinRequest dto) {
        accountService.createPin(dto.getAccountNumber(), dto.getPassword(), dto.getPin());
        return ResponseEntity.ok(Map.of("message", "PIN created successfully"));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update-pin")
    public ResponseEntity<Map<String, String>> updatePin(@Valid @RequestBody UpdatePinRequest dto) {
        accountService.updatePin(
                dto.getAccountNumber(),
                dto.getOldPIN(),
                dto.getPassword(),
                dto.getNewPIN()
        );

        return ResponseEntity.ok(Map.of(
                "message", "PIN updated successfully",
                "accountNumber", dto.getAccountNumber()
        ));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/pin-status/{accountNumber}")
    public ResponseEntity<PinStatusResponse> checkPinStatus(@PathVariable String accountNumber) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) auth.getPrincipal();

        boolean pinExists = accountService.isPinCreated(accountNumber, authenticatedUser.getId());

        PinStatusResponse response = PinStatusResponse.builder()
                .accountNumber(accountNumber)
                .pinCreated(pinExists)
                .message(pinExists ? "PIN is already set" : "PIN needs to be created")
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/fund-transfer")
    public ResponseEntity<FundTransferResponse> fundTransfer(@Valid @RequestBody TransferMoneyDto dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) auth.getPrincipal();

        BigDecimal sourceAccountPreviousBalance = accountService.getAccountBalance(dto.getSourceAccountNumber(), authenticatedUser.getId());
        BigDecimal targetAccountPreviousBalance = accountService.getAccountBalancex(dto.getTargetAccountNumber());

        String transferReference = accountService.fundTransfer(
                dto.getSourceAccountNumber(),
                dto.getTargetAccountNumber(),
                dto.getPin(),
                dto.getAmount(),
                authenticatedUser.getId()
        );

        BigDecimal sourceAccountNewBalance = accountService.getAccountBalance(dto.getSourceAccountNumber(), authenticatedUser.getId());
        BigDecimal targetAccountNewBalance = accountService.getAccountBalancex(dto.getTargetAccountNumber());

        FundTransferResponse response = FundTransferResponse.builder()
                .sourceAccount(dto.getSourceAccountNumber())
                .targetAccount(dto.getTargetAccountNumber())
                .transferAmount(dto.getAmount())
                .sourceAccountPreviousBalance(sourceAccountPreviousBalance)
                .sourceAccountNewBalance(sourceAccountNewBalance)
                .targetAccountPreviousBalance(targetAccountPreviousBalance)
                .targetAccountNewBalance(targetAccountNewBalance)
                .transferTime(LocalDateTime.now())
                .message("Fund transfer successful")
                .status("SUCCESS")
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/account-info/{accountNumber}")
    public ResponseEntity<AccountInfoDto> getAccountInfo(@PathVariable String accountNumber) {
        AccountInfoDto accountInfo = accountService.getAccountInfo(accountNumber);
        return ResponseEntity.ok(accountInfo);
    }

}
