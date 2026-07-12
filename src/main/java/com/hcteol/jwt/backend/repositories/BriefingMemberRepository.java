package com.hcteol.jwt.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.BriefingMember;

@Repository
public interface BriefingMemberRepository extends JpaRepository<BriefingMember, Long> {

    List<BriefingMember> findByBriefingSessionId(Long briefingSessionId);

    Optional<BriefingMember> findByBriefingSessionIdAndStaffId(Long briefingSessionId, String staffId);
}
