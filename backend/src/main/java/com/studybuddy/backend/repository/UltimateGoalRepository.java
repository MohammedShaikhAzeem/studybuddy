package com.studybuddy.backend.repository;

import com.studybuddy.backend.entity.UltimateGoal;
import com.studybuddy.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UltimateGoalRepository extends JpaRepository<UltimateGoal, Long> {

    Optional<UltimateGoal> findByUserAndActiveTrue(User user);

    List<UltimateGoal> findByUserOrderByCreatedAtDesc(User user);
}