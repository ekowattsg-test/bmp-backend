package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.BriefingSession;
import com.hcteol.jwt.backend.repositories.BriefingSessionRepository;

@Service
public class BriefingSessionService {

    @Autowired
    private BriefingSessionRepository briefingSessionRepository;

    public BriefingSession addBriefingSession(BriefingSession briefingSession) {
        return briefingSessionRepository.save(Objects.requireNonNull(briefingSession, "briefingSession cannot be null"));
    }

    public List<BriefingSession> getAllBriefingSessions() {
        return briefingSessionRepository.findAll();
    }

    public Optional<BriefingSession> getBriefingSessionById(Long id) {
        return briefingSessionRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    public BriefingSession updateBriefingSession(Long id, BriefingSession details) {
        BriefingSession existing = briefingSessionRepository.findById(Objects.requireNonNull(id, "id cannot be null")).orElse(null);
        if (existing == null) {
            return null;
        }

        BeanUtils.copyProperties(Objects.requireNonNull(details, "details cannot be null"), existing, "briefingSessionId");
        return briefingSessionRepository.save(existing);
    }

    public void deleteBriefingSession(Long id) {
        briefingSessionRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }
}
