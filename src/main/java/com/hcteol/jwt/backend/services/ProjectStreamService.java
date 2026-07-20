package com.hcteol.jwt.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcteol.jwt.backend.entities.Project;
import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.repositories.ProjectRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectStreamService {

    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectTaskDateCalculationService projectTaskDateCalculationService;

    @Autowired
    private ProjectTaskRecalculationService projectTaskRecalculationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ProjectStream> getAllProjectStreams(String projectCode, String streamType) {
        if (projectCode != null && !projectCode.isBlank() && streamType != null && !streamType.isBlank()) {
            return projectStreamRepository.findByProjectCodeAndStreamType(projectCode, streamType);
        }
        if (projectCode != null && !projectCode.isBlank()) {
            return projectStreamRepository.findByProjectCode(projectCode);
        }
        if (streamType != null && !streamType.isBlank()) {
            return projectStreamRepository.findByStreamType(streamType);
        }
        return projectStreamRepository.findAll();
    }

    public Optional<ProjectStream> getProjectStreamById(Long id) {
        return projectStreamRepository.findById(id);
    }

    public List<ProjectStream> getProjectStreamsByProjectCode(String projectCode) {
        return projectStreamRepository.findByProjectCode(projectCode);
    }

    public ProjectStream createProjectStream(ProjectStream projectStream) {
        ProjectStream savedStream = projectStreamRepository.save(projectStream);
        if (savedStream.getProjectCode() != null && "S".equalsIgnoreCase(savedStream.getStreamType())) {
            createStreamTasks(savedStream);
        }
        return savedStream;
    }

    public ProjectStream updateProjectStream(Long id, ProjectStream projectStreamDetails) {
        return projectStreamRepository.findById(id).map(projectStream -> {
            projectStream.setProjectCode(projectStreamDetails.getProjectCode());
            projectStream.setStreamType(projectStreamDetails.getStreamType());
            projectStream.setStreamNumber(projectStreamDetails.getStreamNumber());
            projectStream.setStreamName(projectStreamDetails.getStreamName());
            projectStream.setStreamDescription(projectStreamDetails.getStreamDescription());
            projectStream.setStreamStartDate(projectStreamDetails.getStreamStartDate());
            projectStream.setStreamEndDate(projectStreamDetails.getStreamEndDate());
            return projectStreamRepository.save(projectStream);
        }).orElseThrow(() -> new RuntimeException("ProjectStream not found with id " + id));
    }

    public void deleteProjectStream(Long id) {
        projectStreamRepository.deleteById(id);
    }

    private void createStreamTasks(ProjectStream stream) {
        Optional<Project> projectOptional = projectRepository.findById(stream.getProjectCode());
        if (projectOptional.isEmpty()) {
            return;
        }

        Project project = projectOptional.get();
        List<ProjectTask> templateTasks = loadProjectStreamTaskTemplate();
        if (templateTasks.isEmpty()) {
            return;
        }

        List<ProjectTask> tasksToCreate = new java.util.ArrayList<>();
        for (ProjectTask template : templateTasks) {
            ProjectTask task = new ProjectTask();
            task.setProjectStreamId(stream.getProjectStreamId());
            task.setTaskType(template.getTaskType());
            task.setTaskName(template.getTaskName());
            task.setStaffId(template.getStaffId());
            task.setParentTaskId(template.getParentTaskId());
            task.setMilestoneTaskId(template.getMilestoneTaskId());
            task.setTaskDuration(template.getTaskDuration());
            task.setTaskStartDate(project.getStartDate());
            task.setTaskEndDate(template.getTaskEndDate());
            task.setTaskStatus(template.getTaskStatus() != null ? template.getTaskStatus() : "Not Started");
            task.setProgress(template.getProgress());
            task.setActualStartDate(template.getActualStartDate());
            task.setActualEndDate(template.getActualEndDate());
            task.setRemarks(template.getRemarks());

            ProjectTask calculatedTask = projectTaskDateCalculationService.calculateTaskDates(task);
            tasksToCreate.add(calculatedTask);
        }

        List<ProjectTask> savedTasks = projectTaskRepository.saveAll(tasksToCreate);
        for (ProjectTask savedTask : savedTasks) {
            if (savedTask.getProjectTaskId() != null) {
                projectTaskRecalculationService.recalculateAfterTaskChange(savedTask.getProjectTaskId());
            }
        }
    }

    private List<ProjectTask> loadProjectStreamTaskTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("template/projectstreamtaskbase.json");
            if (!resource.exists()) {
                return List.of();
            }

            try (InputStream is = resource.getInputStream()) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                if (json.isBlank()) {
                    return List.of();
                }

                if (json.startsWith("[")) {
                    return objectMapper.readValue(json, new TypeReference<List<ProjectTask>>() {
                    });
                }

                ProjectTask singleTemplate = objectMapper.readValue(json, ProjectTask.class);
                return List.of(singleTemplate);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load template/projectstreamtaskbase.json", e);
        }
    }
}
