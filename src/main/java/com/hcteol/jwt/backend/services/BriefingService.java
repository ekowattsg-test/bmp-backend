package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.Briefing;
import com.hcteol.jwt.backend.repositories.BriefingRepository;

@Service
public class BriefingService {

    @Autowired
    private BriefingRepository briefingRepository;

    public Briefing addBriefing(Briefing briefingData) {
        return briefingRepository.save(Objects.requireNonNull(briefingData, "briefingData cannot be null"));
    }

    public List<Briefing> getAllBriefings() {
        return briefingRepository.findAll();
    }

    public Optional<Briefing> getBriefingById(Long id) {
        return briefingRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    @Transactional
    public Briefing updateBriefing(Long id, Briefing details) {
        Briefing existing = briefingRepository.findById(Objects.requireNonNull(id, "id cannot be null")).orElse(null);
        if (existing == null) {
            return null;
        }

        Objects.requireNonNull(details, "details cannot be null");

        BeanUtils.copyProperties(details, existing, "briefingId");

        if (Integer.valueOf(1).equals(existing.getActive())) {
            List<Briefing> otherActiveBriefings = briefingRepository.findByActive(1);
            for (Briefing other : otherActiveBriefings) {
                if (!other.getBriefingId().equals(existing.getBriefingId())) {
                    other.setActive(0);
                    briefingRepository.save(other);
                }
            }
        }

        return briefingRepository.save(existing);
    }

    public void deleteBriefing(Long id) {
        briefingRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }
}
