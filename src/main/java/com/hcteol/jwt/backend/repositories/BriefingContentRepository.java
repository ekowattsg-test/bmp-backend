package com.hcteol.jwt.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.BriefingContent;

@Repository
public interface BriefingContentRepository extends JpaRepository<BriefingContent, Long> {

    List<BriefingContent> findByBriefingId(Long briefingId);

    Optional<BriefingContent> findByBriefingIdAndSequenceNumber(Long briefingId, String sequenceNumber);
}
