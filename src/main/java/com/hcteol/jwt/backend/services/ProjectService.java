package com.hcteol.jwt.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcteol.jwt.backend.entities.Project;
import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.repositories.ProjectRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Project> getAllProjects(Long customerId, String status) {
        if (customerId != null && status != null && !status.isBlank()) {
            return projectRepository.findByCustomerIdAndStatus(customerId, status);
        }
        if (customerId != null) {
            return projectRepository.findByCustomerId(customerId);
        }
        if (status != null && !status.isBlank()) {
            return projectRepository.findByStatus(status);
        }
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectByCode(String projectCode) {
        return projectRepository.findById(projectCode);
    }

    @Transactional
    public Project createProject(Project project) {
        Project savedProject = projectRepository.save(project);
        createBaselineProjectStreams(savedProject);
        return savedProject;
    }

    private void createBaselineProjectStreams(Project project) {
        List<ProjectStream> templateStreams = loadProjectStreamTemplate();
        if (templateStreams.isEmpty()) {
            return;
        }

        Date projectStartDate = parseProjectDate(project.getStartDate());
        Date projectEndDate = parseProjectDate(project.getEndDate());

        List<ProjectStream> streamsToCreate = new ArrayList<>();
        for (ProjectStream template : templateStreams) {
            ProjectStream stream = new ProjectStream();
            stream.setProjectCode(project.getProjectCode());
            stream.setStreamType(template.getStreamType());
            stream.setStreamNumber(template.getStreamNumber());
            stream.setStreamName(template.getStreamName());
            stream.setStreamDescription(template.getStreamDescription());
            stream.setStreamStartDate(template.getStreamStartDate() != null ? template.getStreamStartDate() : projectStartDate);
            stream.setStreamEndDate(template.getStreamEndDate() != null ? template.getStreamEndDate() : projectEndDate);
            streamsToCreate.add(stream);
        }

        projectStreamRepository.saveAll(streamsToCreate);
    }

    private List<ProjectStream> loadProjectStreamTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("template/projectstream.json");
            if (!resource.exists()) {
                return List.of();
            }

            try (InputStream is = resource.getInputStream()) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                if (json.isBlank()) {
                    return List.of();
                }

                if (json.startsWith("[")) {
                    return objectMapper.readValue(json, new TypeReference<List<ProjectStream>>() {
                    });
                }

                ProjectStream singleTemplate = objectMapper.readValue(json, ProjectStream.class);
                return List.of(singleTemplate);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load template/projectstream.json", e);
        }
    }

    private Date parseProjectDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String v = value.trim();
        try {
            return Date.from(Instant.parse(v));
        } catch (DateTimeParseException ignored) {
            try {
                LocalDate localDate = LocalDate.parse(v);
                return java.sql.Date.valueOf(localDate);
            } catch (DateTimeParseException ignoredDateOnly) {
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(v);
                    return java.sql.Timestamp.valueOf(localDateTime);
                } catch (DateTimeParseException ignoredDateTime) {
                    return null;
                }
            }
        }
    }

    public Project updateProject(String projectCode, Project projectDetails) {
        return projectRepository.findById(projectCode).map(project -> {
            project.setProjectName(projectDetails.getProjectName());
            project.setProjectDescription(projectDetails.getProjectDescription());
            project.setCustomerId(projectDetails.getCustomerId());
            project.setStartDate(projectDetails.getStartDate());
            project.setEndDate(projectDetails.getEndDate());
            project.setProjectLocation(projectDetails.getProjectLocation());
            project.setStatus(projectDetails.getStatus());
            project.setStreamCount(projectDetails.getStreamCount());
            return projectRepository.save(project);
        }).orElseThrow(() -> new RuntimeException("Project not found with projectCode " + projectCode));
    }

    public void deleteProject(String projectCode) {
        projectRepository.deleteById(projectCode);
    }
}
