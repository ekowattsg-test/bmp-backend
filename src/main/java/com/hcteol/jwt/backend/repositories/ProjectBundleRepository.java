package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.ProjectBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectBundleRepository extends JpaRepository<ProjectBundle, Long> {
    List<ProjectBundle> findByProjectTaskId(Long projectTaskId);
}
