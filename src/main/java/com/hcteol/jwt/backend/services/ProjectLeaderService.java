package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.ProjectLeader;
import com.hcteol.jwt.backend.repositories.ProjectLeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectLeaderService {

    @Autowired
    private ProjectLeaderRepository projectLeaderRepository;

    public List<ProjectLeader> getAllProjectLeaders() {
        return projectLeaderRepository.findAll();
    }

    public Optional<ProjectLeader> getProjectLeaderById(Long id) {
        return projectLeaderRepository.findById(id);
    }

    public List<ProjectLeader> getProjectLeadersByProjectCode(String projectCode) {
        return projectLeaderRepository.findByProjectCode(projectCode);
    }

    public List<ProjectLeader> getProjectLeadersByStaffId(String projectLeaderStaffId) {
        return projectLeaderRepository.findByProjectLeaderStaffId(projectLeaderStaffId);
    }

    public List<ProjectLeader> getProjectLeadersByActive(Integer active) {
        return projectLeaderRepository.findByActive(active);
    }

    public ProjectLeader createProjectLeader(ProjectLeader projectLeader) {
        return projectLeaderRepository.save(projectLeader);
    }

    public ProjectLeader updateProjectLeader(Long id, ProjectLeader projectLeaderDetails) {
        return projectLeaderRepository.findById(id).map(projectLeader -> {
            projectLeader.setProjectCode(projectLeaderDetails.getProjectCode());
            projectLeader.setProjectLeaderStaffId(projectLeaderDetails.getProjectLeaderStaffId());
            projectLeader.setProjectRole(projectLeaderDetails.getProjectRole());
            projectLeader.setRoleStartDate(projectLeaderDetails.getRoleStartDate());
            projectLeader.setRoleEndDate(projectLeaderDetails.getRoleEndDate());
            projectLeader.setActive(projectLeaderDetails.getActive());
            return projectLeaderRepository.save(projectLeader);
        }).orElseThrow(() -> new RuntimeException("ProjectLeader not found with id " + id));
    }

    public void deleteProjectLeader(Long id) {
        projectLeaderRepository.deleteById(id);
    }
}
