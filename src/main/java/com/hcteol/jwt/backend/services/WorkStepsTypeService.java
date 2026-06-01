package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.WorkStepsType;
import com.hcteol.jwt.backend.repositories.WorkStepsTypeRepository;

@Service
public class WorkStepsTypeService {

    private final WorkStepsTypeRepository repository;

    @Autowired
    public WorkStepsTypeService(WorkStepsTypeRepository repository) {
        this.repository = repository;
    }

    public WorkStepsType create(WorkStepsType workStepsType) {
        return repository.save(Objects.requireNonNull(workStepsType, "workStepsType must not be null"));
    }

    public List<WorkStepsType> findAll() {
        return repository.findAll();
    }

    public Optional<WorkStepsType> findById(Long id) {
        return repository.findById(Objects.requireNonNull(id, "id must not be null"));
    }

    public WorkStepsType update(Long id, WorkStepsType workStepsType) {
        WorkStepsType existing = repository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new RuntimeException("WorkStepsType not found"));
        existing.setWorkOrderType(workStepsType.getWorkOrderType());
        existing.setStepNumber(workStepsType.getStepNumber());
        existing.setStepDescription(workStepsType.getStepDescription());
        existing.setFromEntity(workStepsType.getFromEntity());
        existing.setToEntity(workStepsType.getToEntity());
        existing.setStartAction(workStepsType.getStartAction());
        existing.setScanData(workStepsType.getScanData());
        existing.setCheckQuantity(workStepsType.getCheckQuantity());
        existing.setNewStock(workStepsType.getNewStock());
        existing.setTakePhoto(workStepsType.getTakePhoto());
        existing.setEndAction(workStepsType.getEndAction());
        existing.setNoConfirm(workStepsType.getNoConfirm());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(Objects.requireNonNull(id, "id must not be null"));
    }
}
