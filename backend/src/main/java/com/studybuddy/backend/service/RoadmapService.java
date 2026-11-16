package com.studybuddy.backend.service;

import com.studybuddy.backend.dto.RoadmapStepResponse;
import com.studybuddy.backend.entity.RoadmapStep;
import com.studybuddy.backend.entity.UltimateGoal;
import com.studybuddy.backend.entity.User;
import com.studybuddy.backend.repository.RoadmapStepRepository;
import com.studybuddy.backend.repository.UltimateGoalRepository;
import com.studybuddy.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoadmapService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private RoadmapStepRepository stepRepository;

    @Autowired
    private UltimateGoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String ROADMAP_PROMPT = """
            You are a career and learning mentor. Create a step by step learning roadmap
            for a student whose ultimate goal is: "%s".

            Rules:
            - Give between 6 and 8 steps, ordered from complete beginner to reaching the goal.
            - Output ONLY lines in exactly this format: Step title | One short sentence describing the step
            - No numbering, no bullets, no markdown, no extra text before or after.
            """;

    private UltimateGoal getOwnedGoal(String phoneNumber, Long goalId) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UltimateGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This goal does not belong to you");
        }
        return goal;
    }

    private RoadmapStepResponse toResponse(RoadmapStep s) {
        return new RoadmapStepResponse(s.getId(), s.getStepOrder(),
                s.getTitle(), s.getDescription(), s.getStatus().name());
    }

    public List<RoadmapStepResponse> getRoadmap(String phoneNumber, Long goalId) {
        UltimateGoal goal = getOwnedGoal(phoneNumber, goalId);
        return stepRepository.findByGoalOrderByStepOrderAsc(goal)
                .stream().map(this::toResponse).toList();
    }

    public List<RoadmapStepResponse> generateRoadmap(String phoneNumber, Long goalId) {
        UltimateGoal goal = getOwnedGoal(phoneNumber, goalId);

        // roadmap is stored once, so it never changes on the student
        List<RoadmapStep> existing = stepRepository.findByGoalOrderByStepOrderAsc(goal);
        if (!existing.isEmpty()) {
            return existing.stream().map(this::toResponse).toList();
        }

        String reply = geminiService.generate(null,
                ROADMAP_PROMPT.formatted(goal.getTitle()));

        List<RoadmapStep> steps = new ArrayList<>();
        int order = 1;
        for (String line : reply.split("\\R")) {
            // clean up bullets or numbering in case the AI adds them anyway
            String cleaned = line.trim().replaceFirst("^([-*•]|\\d+[.)])\\s*", "");
            if (cleaned.isBlank() || !cleaned.contains("|")) {
                continue;
            }
            String[] parts = cleaned.split("\\|", 2);

            RoadmapStep step = new RoadmapStep();
            step.setGoal(goal);
            step.setStepOrder(order);
            step.setTitle(parts[0].trim().replace("**", ""));
            step.setDescription(parts[1].trim());
            step.setStatus(order == 1 ? RoadmapStep.Status.IN_PROGRESS
                                      : RoadmapStep.Status.NOT_STARTED);
            steps.add(step);
            order++;
        }

        if (steps.isEmpty()) {
            throw new RuntimeException("Could not generate roadmap, please try again");
        }

        return stepRepository.saveAll(steps).stream().map(this::toResponse).toList();
    }
}