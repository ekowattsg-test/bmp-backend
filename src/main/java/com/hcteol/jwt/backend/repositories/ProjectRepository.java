package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    List<Project> findByCustomerId(Long customerId);

    List<Project> findByStatus(String status);

    List<Project> findByBriefingId(Long briefingId);

    List<Project> findByCustomerIdAndStatus(Long customerId, String status);

    List<Project> findByCustomerIdAndBriefingId(Long customerId, Long briefingId);

    List<Project> findByStatusAndBriefingId(String status, Long briefingId);

    List<Project> findByCustomerIdAndStatusAndBriefingId(Long customerId, String status, Long briefingId);
}
