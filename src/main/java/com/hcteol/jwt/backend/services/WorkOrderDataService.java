package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.WorkOrderData;
import com.hcteol.jwt.backend.repositories.WorkOrderDataRepository;

@Service
public class WorkOrderDataService {

    @Autowired
    private WorkOrderDataRepository repository;

    public WorkOrderData create(WorkOrderData data) {
        return repository.save(data);
    }

    public List<WorkOrderData> findAll() {
        return repository.findAll();
    }

    public Optional<WorkOrderData> findById(Long id) {
        return repository.findById(id);
    }

    public WorkOrderData update(Long id, WorkOrderData data) {
        var existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setWorkOrderId(data.getWorkOrderId());
            existing.setProductId(data.getProductId());
            existing.setQuantity(data.getQuantity());
            existing.setStaffId(data.getStaffId());
            return repository.save(existing);
        }
        return null;
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
