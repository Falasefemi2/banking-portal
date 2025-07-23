package com.femi.bankingportal.controller;

import com.femi.bankingportal.dto.*;
import com.femi.bankingportal.model.User;
import com.femi.bankingportal.service.AccountServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl accountService;

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
}
