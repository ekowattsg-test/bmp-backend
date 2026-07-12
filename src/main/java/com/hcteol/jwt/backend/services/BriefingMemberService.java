package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.BriefingMember;
import com.hcteol.jwt.backend.repositories.BriefingMemberRepository;

@Service
public class BriefingMemberService {

    @Autowired
    private BriefingMemberRepository briefingMemberRepository;

    public BriefingMember addBriefingMember(BriefingMember briefingMember) {
        return briefingMemberRepository.save(Objects.requireNonNull(briefingMember, "briefingMember cannot be null"));
    }

    public List<BriefingMember> getAllBriefingMembers() {
        return briefingMemberRepository.findAll();
    }

    public Optional<BriefingMember> getBriefingMemberById(Long id) {
        return briefingMemberRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    public Optional<BriefingMember> getBriefingMemberBySessionIdAndStaffId(Long briefingSessionId, String staffId) {
        return briefingMemberRepository.findByBriefingSessionIdAndStaffId(
                Objects.requireNonNull(briefingSessionId, "briefingSessionId cannot be null"),
                Objects.requireNonNull(staffId, "staffId cannot be null"));
    }

    public List<BriefingMember> getBriefingMembersBySessionId(Long briefingSessionId) {
        return briefingMemberRepository.findByBriefingSessionId(
                Objects.requireNonNull(briefingSessionId, "briefingSessionId cannot be null"));
    }

    public BriefingMember updateBriefingMember(Long id, BriefingMember details) {
        BriefingMember existing = briefingMemberRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElse(null);
        if (existing == null) {
            return null;
        }

        BeanUtils.copyProperties(Objects.requireNonNull(details, "details cannot be null"), existing, "briefingMemberId");
        return briefingMemberRepository.save(existing);
    }

    public void deleteBriefingMember(Long id) {
        briefingMemberRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }
}
