package com.hcteol.jwt.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.WorkSteps;
import com.hcteol.jwt.backend.repositories.WorkStepsRepository;

@Service
public class WorkStepsService {

    @Autowired
    private WorkStepsRepository workStepsRepository;

    public WorkSteps addWorkStep(WorkSteps step) {
        return workStepsRepository.save(step);
    }

    public List<WorkSteps> getAllWorkSteps() {
        return workStepsRepository.findAll();
    }

    public java.util.Optional<WorkSteps> getWorkStepById(Long id) {
        return workStepsRepository.findById(id);
    }

    public WorkSteps updateWorkStep(Long id, WorkSteps details) {
        var existing = workStepsRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setWorkOrderId(details.getWorkOrderId());
            existing.setStepNumber(details.getStepNumber());
            existing.setFromLocation(details.getFromLocation());
            existing.setToLocation(details.getToLocation());
            existing.setStepStatus(details.getStepStatus());
            return workStepsRepository.save(existing);
        }
        return null;
    }

    public void deleteWorkStep(Long id) {
        workStepsRepository.deleteById(id);
    }
}
