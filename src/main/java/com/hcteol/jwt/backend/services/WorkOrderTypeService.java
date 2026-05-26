package com.hcteol.jwt.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.WorkOrderType;
import com.hcteol.jwt.backend.repositories.WorkOrderTypeRepository;

@Service
public class WorkOrderTypeService {

    @Autowired
    private WorkOrderTypeRepository workOrderTypeRepository;

    public WorkOrderType addWorkOrderType(WorkOrderType type) {
        return workOrderTypeRepository.save(type);
    }

    public List<WorkOrderType> getAllWorkOrderTypes() {
        return workOrderTypeRepository.findAll();
    }

    public java.util.Optional<WorkOrderType> getWorkOrderTypeById(String id) {
        return workOrderTypeRepository.findById(id);
    }

    public WorkOrderType updateWorkOrderType(String id, WorkOrderType details) {
        var existing = workOrderTypeRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setWorkOrderDescription(details.getWorkOrderDescription());
            existing.setContentType(details.getContentType());
            existing.setNumberOfSteps(details.getNumberOfSteps());
            existing.setNeedDetails(details.getNeedDetails());
            existing.setRoleName(details.getRoleName());
            existing.setActive(details.getActive());
            return workOrderTypeRepository.save(existing);
        }
        return null;
    }

    public void deleteWorkOrderType(String id) {
        workOrderTypeRepository.deleteById(id);
    }
}
