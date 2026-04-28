package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.ProjectStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectStockRepository extends JpaRepository<ProjectStock, Long> {
    List<ProjectStock> findByProjectTaskId(Long projectTaskId);
}
