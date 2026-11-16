package com.studybuddy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoadmapStepResponse {

    private Long id;
    private int stepOrder;
    private String title;
    private String description;
    private String status;
}