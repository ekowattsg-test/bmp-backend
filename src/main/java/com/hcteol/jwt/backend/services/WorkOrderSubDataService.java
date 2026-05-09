package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.WorkOrderSubData;
import com.hcteol.jwt.backend.repositories.WorkOrderSubDataRepository;

@Service
public class WorkOrderSubDataService {

    @Autowired
    private WorkOrderSubDataRepository repository;

    public WorkOrderSubData create(WorkOrderSubData data) {
        return repository.save(data);
    }

    public List<WorkOrderSubData> findAll() {
        return repository.findAll();
    }

    public Optional<WorkOrderSubData> findById(Long id) {
        return repository.findById(id);
    }

    public WorkOrderSubData update(Long id, WorkOrderSubData data) {
        var existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setWorkOrderDataId(data.getWorkOrderDataId());
            existing.setProductId(data.getProductId());
            existing.setStockId(data.getStockId());
            existing.setSubQuantity(data.getSubQuantity());
            return repository.save(existing);
        }
        return null;
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
