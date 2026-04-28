package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectStreamService {
    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    public List<ProjectStream> getAllProjectStreams() {
        return projectStreamRepository.findAll();
    }

    public Optional<ProjectStream> getProjectStreamById(Long id) {
        return projectStreamRepository.findById(id);
    }

    public List<ProjectStream> getProjectStreamsByProjectCode(String projectCode) {
        return projectStreamRepository.findByProjectCode(projectCode);
    }

    public ProjectStream createProjectStream(ProjectStream projectStream) {
        return projectStreamRepository.save(projectStream);
    }

    public ProjectStream updateProjectStream(Long id, ProjectStream projectStreamDetails) {
        return projectStreamRepository.findById(id).map(projectStream -> {
            projectStream.setProjectCode(projectStreamDetails.getProjectCode());
            projectStream.setStreamNumber(projectStreamDetails.getStreamNumber());
            projectStream.setStreamName(projectStreamDetails.getStreamName());
            projectStream.setStreamDescription(projectStreamDetails.getStreamDescription());
            projectStream.setMobileNumber(projectStreamDetails.getMobileNumber());
            projectStream.setActive(projectStreamDetails.getActive());
            return projectStreamRepository.save(projectStream);
        }).orElseThrow(() -> new RuntimeException("ProjectStream not found with id " + id));
    }

    public void deleteProjectStream(Long id) {
        projectStreamRepository.deleteById(id);
    }
}
