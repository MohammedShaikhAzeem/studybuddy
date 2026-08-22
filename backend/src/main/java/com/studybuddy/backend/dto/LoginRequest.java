package com.studybuddy.backend.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String phoneNumber;
    private String password;
}