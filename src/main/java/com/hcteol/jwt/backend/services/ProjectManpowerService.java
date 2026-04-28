package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.ProjectManpower;
import com.hcteol.jwt.backend.repositories.ProjectManpowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectManpowerService {
    @Autowired
    private ProjectManpowerRepository projectManpowerRepository;

    public List<ProjectManpower> getAllProjectManpowers() {
        return projectManpowerRepository.findAll();
    }

    public Optional<ProjectManpower> getProjectManpowerById(Long id) {
        return projectManpowerRepository.findById(id);
    }

    public List<ProjectManpower> getProjectManpowersByTaskId(Long projectTaskId) {
        return projectManpowerRepository.findByProjectTaskId(projectTaskId);
    }

    public ProjectManpower createProjectManpower(ProjectManpower projectManpower) {
        return projectManpowerRepository.save(projectManpower);
    }

    public ProjectManpower updateProjectManpower(Long id, ProjectManpower projectManpowerDetails) {
        return projectManpowerRepository.findById(id).map(projectManpower -> {
            projectManpower.setProjectTaskId(projectManpowerDetails.getProjectTaskId());
            projectManpower.setMobileNumber(projectManpowerDetails.getMobileNumber());
            projectManpower.setRole(projectManpowerDetails.getRole());
            projectManpower.setLoading(projectManpowerDetails.getLoading());
            return projectManpowerRepository.save(projectManpower);
        }).orElseThrow(() -> new RuntimeException("ProjectManpower not found with id " + id));
    }

    public void deleteProjectManpower(Long id) {
        projectManpowerRepository.deleteById(id);
    }
}
