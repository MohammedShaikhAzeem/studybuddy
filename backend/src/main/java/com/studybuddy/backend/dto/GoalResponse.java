package com.studybuddy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GoalResponse {

    private Long id;
    private String title;
    private boolean active;
    private LocalDateTime createdAt;
}