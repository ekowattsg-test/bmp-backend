package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectStack;

@Repository
public interface ProjectStackRepository extends JpaRepository<ProjectStack, Long> {

    List<ProjectStack> findByProjectBlockId(Long projectBlockId);

    List<ProjectStack> findByProjectBlockIdOrderByStackNumberAsc(Long projectBlockId);

    List<ProjectStack> findByProjectBlockIdAndStatusOrderByStackNumberAsc(Long projectBlockId, String status);

    List<ProjectStack> findByProjectBlockIdAndStackNumber(Long projectBlockId, Long stackNumber);
}
