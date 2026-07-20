package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.RequisitionCycle;
import com.hcteol.jwt.backend.repositories.RequisitionCycleRepository;

@Service
public class RequisitionCycleService {

    @Autowired
    private RequisitionCycleRepository requisitionCycleRepository;

    public RequisitionCycle addRequisitionCycle(RequisitionCycle requisitionCycle) {
        RequisitionCycle target = Objects.requireNonNull(requisitionCycle, "requisitionCycle cannot be null");
        if (target.getStatus() == null || target.getStatus().isBlank()) {
            target.setStatus("created");
        }
        return requisitionCycleRepository.save(target);
    }

    public List<RequisitionCycle> getAllRequisitionCycles() {
        return requisitionCycleRepository.findAll();
    }

    public List<RequisitionCycle> getRequisitionCyclesByStatus(String status) {
        return requisitionCycleRepository.findByStatus(Objects.requireNonNull(status, "status cannot be null"));
    }

    public Optional<RequisitionCycle> getRequisitionCycleById(Long id) {
        return requisitionCycleRepository.findById(Objects.requireNonNull(id, "id cannot be null"));
    }

    public RequisitionCycle updateRequisitionCycle(Long id, RequisitionCycle details) {
        RequisitionCycle existing = requisitionCycleRepository.findById(Objects.requireNonNull(id, "id cannot be null"))
                .orElse(null);
        if (existing == null) {
            return null;
        }

        BeanUtils.copyProperties(Objects.requireNonNull(details, "details cannot be null"), existing,
                "requisitionCycleId");
        return requisitionCycleRepository.save(existing);
    }

    public void deleteRequisitionCycle(Long id) {
        requisitionCycleRepository.deleteById(Objects.requireNonNull(id, "id cannot be null"));
    }
}
