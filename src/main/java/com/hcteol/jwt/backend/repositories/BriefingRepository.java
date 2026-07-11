package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.Briefing;

@Repository
public interface BriefingRepository extends JpaRepository<Briefing, Long> {

    List<Briefing> findByActive(Integer active);
}
