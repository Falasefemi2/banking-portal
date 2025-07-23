package com.femi.bankingportal.service;

import com.femi.bankingportal.config.JwtUtil;
import com.femi.bankingportal.dto.UserLoginRequestDTO;
import com.femi.bankingportal.dto.UserLoginResponseDTO;
import com.femi.bankingportal.dto.UserRegistrationRequestDTO;
import com.femi.bankingportal.dto.UserRegistrationResponseDTO;
import com.femi.bankingportal.model.Role;
import com.femi.bankingportal.model.User;
import com.femi.bankingportal.repository.RoleRepository;
import com.femi.bankingportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserRegistrationResponseDTO registerUser(UserRegistrationRequestDTO dto) {
        if(userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail()).isPresent()) {
            throw new RuntimeException("Username or email already exists");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .pin(passwordEncoder.encode(dto.getPin()))
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .build();

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
        user.setRoles(List.of(userRole));

        user = userRepository.save(user);

        return UserRegistrationResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserLoginResponseDTO loginUser(UserLoginRequestDTO dto) {
        User user = userRepository.findByUsernameOrEmail(dto.getUsernameOrEmail(), dto.getUsernameOrEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String token = jwtUtil.generateToken(user);
        return UserLoginResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}
