package com.hcteol.jwt.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.WorkOrder;
import com.hcteol.jwt.backend.repositories.WorkOrderRepository;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    public WorkOrder addWorkOrder(WorkOrder workOrder) {
        return workOrderRepository.save(workOrder);
    }

    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    public java.util.Optional<WorkOrder> getWorkOrderById(Long id) {
        return workOrderRepository.findById(id);
    }

    public WorkOrder updateWorkOrder(Long id, WorkOrder details) {
        var existing = workOrderRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setWorkOrderType(details.getWorkOrderType());
            existing.setWorkDescription(details.getWorkDescription());
            existing.setIssuedBy(details.getIssuedBy());
            existing.setWorkOrderDate(details.getWorkOrderDate());
            existing.setWorkBy(details.getWorkBy());
            existing.setWorkOrderStatus(details.getWorkOrderStatus());
            return workOrderRepository.save(existing);
        }
        return null;
    }

    public void deleteWorkOrder(Long id) {
        workOrderRepository.deleteById(id);
    }
}
