package com.studybuddy.backend.controller;

import com.studybuddy.backend.dto.GoalRequest;
import com.studybuddy.backend.dto.GoalResponse;
import com.studybuddy.backend.service.UltimateGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ultimate-goals")
public class UltimateGoalController {

    @Autowired
    private UltimateGoalService goalService;

    private String currentPhone() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<GoalResponse> setGoal(@RequestBody GoalRequest request) {
        return ResponseEntity.ok(goalService.setGoal(currentPhone(), request));
    }

    @GetMapping("/active")
    public ResponseEntity<GoalResponse> getActiveGoal() {
        return goalService.getActiveGoal(currentPhone())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<List<GoalResponse>> getHistory() {
        return ResponseEntity.ok(goalService.getHistory(currentPhone()));
    }
}