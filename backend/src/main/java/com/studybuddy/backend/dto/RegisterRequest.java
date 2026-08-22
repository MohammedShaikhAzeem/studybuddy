package com.studybuddy.backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String fullName;
    private String phoneNumber;
    private String password;
}