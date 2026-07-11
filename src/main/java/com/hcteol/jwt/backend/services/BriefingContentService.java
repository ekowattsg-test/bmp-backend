package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.BriefingContent;
import com.hcteol.jwt.backend.repositories.BriefingContentRepository;

@Service
public class BriefingContentService {

    @Autowired
    private BriefingContentRepository briefingContentRepository;

    public BriefingContent addBriefingContent(BriefingContent content) {
        return briefingContentRepository.save(Objects.requireNonNull(content, "content cannot be null"));
    }

    public List<BriefingContent> getAllBriefingContents() {
        return briefingContentRepository.findAll();
    }

    public Optional<BriefingContent> getBriefingContentById(Long id) {
        return briefingContentRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    public List<BriefingContent> getByBriefingId(Long briefingId) {
        return briefingContentRepository.findByBriefingId(Objects.requireNonNull(briefingId, "briefingId cannot be null"));
    }

    public BriefingContent updateBriefingContent(Long id, BriefingContent details) {
        BriefingContent existing = briefingContentRepository.findById(Objects.requireNonNull(id, "id cannot be null")).orElse(null);
        if (existing == null) {
            return null;
        }

        Objects.requireNonNull(details, "details cannot be null");

        BeanUtils.copyProperties(details, existing, "briefingContentId");

        return briefingContentRepository.save(existing);
    }

    public void deleteBriefingContent(Long id) {
        briefingContentRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }
}
