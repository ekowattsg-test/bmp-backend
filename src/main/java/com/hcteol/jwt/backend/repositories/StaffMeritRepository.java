package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.StaffMerit;

@Repository
public interface StaffMeritRepository extends JpaRepository<StaffMerit, Long> {

    List<StaffMerit> findByMeritCategory(String meritCategory);
}
