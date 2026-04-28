package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.ProjectManpower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectManpowerRepository extends JpaRepository<ProjectManpower, Long> {
    List<ProjectManpower> findByProjectTaskId(Long projectTaskId);
}
