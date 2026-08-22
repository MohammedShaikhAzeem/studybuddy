package com.studybuddy.backend.controller;

import com.studybuddy.backend.dto.UserResponse;
import com.studybuddy.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile() {
        String phoneNumber = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        UserResponse response = userService.getProfile(phoneNumber);
        return ResponseEntity.ok(response);
    }
}