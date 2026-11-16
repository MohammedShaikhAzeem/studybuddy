package com.studybuddy.backend.controller;

import com.studybuddy.backend.dto.RoadmapStepResponse;
import com.studybuddy.backend.service.RoadmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadmap-steps")
public class RoadmapController {

    @Autowired
    private RoadmapService roadmapService;

    private String currentPhone() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/generate/{goalId}")
    public ResponseEntity<List<RoadmapStepResponse>> generate(@PathVariable Long goalId) {
        return ResponseEntity.ok(roadmapService.generateRoadmap(currentPhone(), goalId));
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<List<RoadmapStepResponse>> get(@PathVariable Long goalId) {
        return ResponseEntity.ok(roadmapService.getRoadmap(currentPhone(), goalId));
    }
}