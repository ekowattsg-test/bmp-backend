package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.ProjectStock;
import com.hcteol.jwt.backend.repositories.ProjectStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectStockService {
    @Autowired
    private ProjectStockRepository projectStockRepository;

    public List<ProjectStock> getAllProjectStocks() {
        return projectStockRepository.findAll();
    }

    public Optional<ProjectStock> getProjectStockById(Long id) {
        return projectStockRepository.findById(id);
    }

    public List<ProjectStock> getProjectStocksByTaskId(Long projectTaskId) {
        return projectStockRepository.findByProjectTaskId(projectTaskId);
    }

    public ProjectStock createProjectStock(ProjectStock projectStock) {
        return projectStockRepository.save(projectStock);
    }

    public ProjectStock updateProjectStock(Long id, ProjectStock projectStockDetails) {
        return projectStockRepository.findById(id).map(projectStock -> {
            projectStock.setProjectTaskId(projectStockDetails.getProjectTaskId());
            projectStock.setProductId(projectStockDetails.getProductId());
            projectStock.setQuantity(projectStockDetails.getQuantity());
            return projectStockRepository.save(projectStock);
        }).orElseThrow(() -> new RuntimeException("ProjectStock not found with id " + id));
    }

    public void deleteProjectStock(Long id) {
        projectStockRepository.deleteById(id);
    }
}
