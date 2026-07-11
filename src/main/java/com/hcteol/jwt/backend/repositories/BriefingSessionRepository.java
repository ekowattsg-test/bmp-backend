package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.BriefingSession;

@Repository
public interface BriefingSessionRepository extends JpaRepository<BriefingSession, Long> {
}
