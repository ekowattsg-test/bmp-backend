package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> findByProjectStreamId(Long projectStreamId);
}
