package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.WorkSteps;

public interface WorkStepsRepository extends JpaRepository<WorkSteps, Long> {

}
