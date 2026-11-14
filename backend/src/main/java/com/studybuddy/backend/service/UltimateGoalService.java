package com.studybuddy.backend.service;

import com.studybuddy.backend.dto.GoalRequest;
import com.studybuddy.backend.dto.GoalResponse;
import com.studybuddy.backend.entity.UltimateGoal;
import com.studybuddy.backend.entity.User;
import com.studybuddy.backend.repository.UltimateGoalRepository;
import com.studybuddy.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UltimateGoalService {

    @Autowired
    private UltimateGoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    private User getUser(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private GoalResponse toResponse(UltimateGoal goal) {
        return new GoalResponse(goal.getId(), goal.getTitle(),
                goal.isActive(), goal.getCreatedAt());
    }

    @Transactional
    public GoalResponse setGoal(String phoneNumber, GoalRequest request) {
        User user = getUser(phoneNumber);

        // deactivate the old goal, if there is one
        goalRepository.findByUserAndActiveTrue(user).ifPresent(old -> {
            old.setActive(false);
            goalRepository.save(old);
        });

        UltimateGoal goal = new UltimateGoal();
        goal.setUser(user);
        goal.setTitle(request.getTitle());
        goal.setActive(true);

        return toResponse(goalRepository.save(goal));
    }

    public Optional<GoalResponse> getActiveGoal(String phoneNumber) {
        return goalRepository.findByUserAndActiveTrue(getUser(phoneNumber))
                .map(this::toResponse);
    }

    public List<GoalResponse> getHistory(String phoneNumber) {
        return goalRepository.findByUserOrderByCreatedAtDesc(getUser(phoneNumber))
                .stream()
                .map(this::toResponse)
                .toList();
    }
}