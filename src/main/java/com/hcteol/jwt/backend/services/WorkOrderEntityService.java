package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.WorkOrderEntity;
import com.hcteol.jwt.backend.repositories.WorkOrderEntityRepository;

@Service
public class WorkOrderEntityService {

    @Autowired
    private WorkOrderEntityRepository repository;

    public WorkOrderEntity create(WorkOrderEntity workOrder) {
        return repository.save(workOrder);
    }

    public List<WorkOrderEntity> findAll() {
        return repository.findAll();
    }

    public Optional<WorkOrderEntity> findById(String id) {
        return repository.findById(id);
    }

    public WorkOrderEntity update(String id, WorkOrderEntity workOrder) {
        var existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setDescription(workOrder.getDescription());
            return repository.save(existing);
        }
        return null;
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
