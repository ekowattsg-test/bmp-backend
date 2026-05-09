package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.WorkStepsType;
import com.hcteol.jwt.backend.repositories.WorkStepsTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkStepsTypeService {

    private final WorkStepsTypeRepository repository;

    @Autowired
    public WorkStepsTypeService(WorkStepsTypeRepository repository) {
        this.repository = repository;
    }

    public WorkStepsType create(WorkStepsType workStepsType) {
        return repository.save(workStepsType);
    }

    public List<WorkStepsType> findAll() {
        return repository.findAll();
    }

    public Optional<WorkStepsType> findById(Long id) {
        return repository.findById(id);
    }

    public WorkStepsType update(Long id, WorkStepsType workStepsType) {
        WorkStepsType existing = repository.findById(id).orElseThrow(() -> new RuntimeException("WorkStepsType not found"));
        existing.setWorkOrderType(workStepsType.getWorkOrderType());
        existing.setStepNumber(workStepsType.getStepNumber());
        existing.setStepDescription(workStepsType.getStepDescription());
        existing.setFromEntity(workStepsType.getFromEntity());
        existing.setToEntity(workStepsType.getToEntity());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
