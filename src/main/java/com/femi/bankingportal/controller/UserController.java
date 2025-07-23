package com.femi.bankingportal.controller;

import com.femi.bankingportal.dto.UserLoginRequestDTO;
import com.femi.bankingportal.dto.UserLoginResponseDTO;
import com.femi.bankingportal.dto.UserRegistrationRequestDTO;
import com.femi.bankingportal.dto.UserRegistrationResponseDTO;
import com.femi.bankingportal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDTO> register(@Valid @RequestBody UserRegistrationRequestDTO dto) {
        return ResponseEntity.ok(userService.registerUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO dto) {
        return ResponseEntity.ok(userService.loginUser(dto));
    }

}
