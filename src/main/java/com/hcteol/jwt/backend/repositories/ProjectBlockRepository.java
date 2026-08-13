package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectBlock;

@Repository
public interface ProjectBlockRepository extends JpaRepository<ProjectBlock, Long> {

    List<ProjectBlock> findByProjectCode(String projectCode);

    List<ProjectBlock> findByProjectCodeOrderByBlockNumberAsc(String projectCode);

    List<ProjectBlock> findByProjectCodeAndStatusOrderByBlockNumberAsc(String projectCode, String status);

    List<ProjectBlock> findByProjectCodeAndBlockNumber(String projectCode, Long blockNumber);
}
