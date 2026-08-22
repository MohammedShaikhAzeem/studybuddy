package com.studybuddy.backend.service;

import com.studybuddy.backend.dto.AuthResponse;
import com.studybuddy.backend.dto.LoginRequest;
import com.studybuddy.backend.dto.RegisterRequest;
import com.studybuddy.backend.entity.User;
import com.studybuddy.backend.repository.UserRepository;
import com.studybuddy.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }

        // Create new user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Save to database
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getPhoneNumber());

        return new AuthResponse(token, user.getFullName(), user.getPhoneNumber());
    }

    public AuthResponse login(LoginRequest request) {
        // Find user by phone number
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getPhoneNumber());

        return new AuthResponse(token, user.getFullName(), user.getPhoneNumber());
    }
}