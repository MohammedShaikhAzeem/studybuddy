package com.studybuddy.backend.repository;

import com.studybuddy.backend.entity.RoadmapStep;
import com.studybuddy.backend.entity.UltimateGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, Long> {

    List<RoadmapStep> findByGoalOrderByStepOrderAsc(UltimateGoal goal);
}