package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.BriefingMember;

@Repository
public interface BriefingMemberRepository extends JpaRepository<BriefingMember, Long> {
}
