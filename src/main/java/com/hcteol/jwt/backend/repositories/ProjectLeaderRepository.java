package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.ProjectLeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectLeaderRepository extends JpaRepository<ProjectLeader, Long> {

    List<ProjectLeader> findByProjectCode(String projectCode);

    List<ProjectLeader> findByProjectLeaderStaffId(String projectLeaderStaffId);

    List<ProjectLeader> findByActive(Integer active);
}
