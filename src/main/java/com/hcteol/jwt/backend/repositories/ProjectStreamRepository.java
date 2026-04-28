package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.ProjectStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectStreamRepository extends JpaRepository<ProjectStream, Long> {
    List<ProjectStream> findByProjectCode(String projectCode);
}
