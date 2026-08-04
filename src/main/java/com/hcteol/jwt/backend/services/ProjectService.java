package com.hcteol.jwt.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcteol.jwt.backend.entities.Project;
import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.repositories.ProjectRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private static final double EARTH_RADIUS_METERS = 6_371_000.0d;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectStreamDateRecalculationService projectStreamDateRecalculationService;

    @Autowired
    private ProjectTaskRecalculationService projectTaskRecalculationService;

    @Autowired
    private ProjectTaskDateCalculationService projectTaskDateCalculationService;

    @Value("${project.gps.nearby-radius-meters}")
    private Double projectGpsNearbyRadiusMeters;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Project> getAllProjects(Long customerId, String status, Long briefingId) {
        if (customerId != null && status != null && !status.isBlank() && briefingId != null) {
            return projectRepository.findByCustomerIdAndStatusAndBriefingId(customerId, status, briefingId);
        }
        if (customerId != null && status != null && !status.isBlank()) {
            return projectRepository.findByCustomerIdAndStatus(customerId, status);
        }
        if (customerId != null && briefingId != null) {
            return projectRepository.findByCustomerIdAndBriefingId(customerId, briefingId);
        }
        if (status != null && !status.isBlank() && briefingId != null) {
            return projectRepository.findByStatusAndBriefingId(status, briefingId);
        }
        if (customerId != null) {
            return projectRepository.findByCustomerId(customerId);
        }
        if (status != null && !status.isBlank()) {
            return projectRepository.findByStatus(status);
        }
        if (briefingId != null) {
            return projectRepository.findByBriefingId(briefingId);
        }
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectByCode(String projectCode) {
        return projectRepository.findById(projectCode);
    }

    public List<Project> getProjectsNearCoordinate(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("latitude and longitude are required");
        }

        List<Project> candidates = projectRepository.findAll();
        List<Project> matchedProjects = new ArrayList<>();

        for (Project candidate : candidates) {
            Double candidateLatitude = parseCoordinate(candidate.getLatitude());
            Double candidateLongitude = parseCoordinate(candidate.getLongitude());

            if (candidateLatitude == null || candidateLongitude == null) {
                continue;
            }

            double distanceMeters = calculateDistanceMeters(
                    latitude,
                    longitude,
                    candidateLatitude,
                    candidateLongitude);

            if (distanceMeters <= projectGpsNearbyRadiusMeters) {
                matchedProjects.add(candidate);
            }
        }

        return matchedProjects;
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

        List<ProjectStream> streamsToCreate = new ArrayList<>();
        for (ProjectStream template : templateStreams) {
            ProjectStream stream = new ProjectStream();
            stream.setProjectCode(project.getProjectCode());
            stream.setStreamType(template.getStreamType());
            stream.setStreamNumber(template.getStreamNumber());
            stream.setStreamName(template.getStreamName());
            stream.setStreamDescription(template.getStreamDescription());
            stream.setStreamStartDate(template.getStreamStartDate() != null ? template.getStreamStartDate() : project.getStartDate());
            stream.setStreamEndDate(template.getStreamEndDate() != null ? template.getStreamEndDate() : project.getEndDate());
            streamsToCreate.add(stream);
        }

        List<ProjectStream> savedStreams = projectStreamRepository.saveAll(streamsToCreate);
        for (ProjectStream savedStream : savedStreams) {
            // Only auto-create baseline tasks for auto-generated baseline project streams.
            if ("P".equalsIgnoreCase(savedStream.getStreamType())) {
                createBaselineProjectTasks(project, savedStream);
                projectStreamDateRecalculationService.recalculateStreamDatesFromTasks(savedStream.getProjectStreamId());
            }
        }
    }

    private void createBaselineProjectTasks(Project project, ProjectStream stream) {
        List<ProjectTask> templateTasks = loadProjectTaskTemplate();
        if (templateTasks.isEmpty()) {
            return;
        }

        String projectStartDate = project.getStartDate();
        String projectEndDate = project.getEndDate();

        List<ProjectTask> tasksToCreate = new ArrayList<>();
        for (ProjectTask template : templateTasks) {
            ProjectTask task = new ProjectTask();
            task.setProjectStreamId(stream.getProjectStreamId());
            task.setTaskType(template.getTaskType());
            task.setTaskName(template.getTaskName());
            task.setStaffId(template.getStaffId());
            task.setParentTaskId(template.getParentTaskId());
            task.setMilestoneTaskId(template.getMilestoneTaskId());

            task.setTaskStartDate(template.getTaskStartDate());
            task.setTaskEndDate(template.getTaskEndDate());

            String taskType = template.getTaskType();
            if ("B".equalsIgnoreCase(taskType)) {
                task.setTaskStartDate(projectStartDate);
                task.setTaskEndDate(plusDays(projectStartDate, 7));
            } else if ("M".equalsIgnoreCase(taskType)) {
                task.setTaskStartDate(projectEndDate);
                task.setTaskEndDate(projectEndDate);
            }

            task.setTaskStatus(template.getTaskStatus() != null ? template.getTaskStatus() : "Not Started");
            task.setProgress(template.getProgress());
            task.setActualStartDate(template.getActualStartDate());
            task.setActualEndDate(template.getActualEndDate());
            task.setRemarks(template.getRemarks());
            tasksToCreate.add(task);
        }

        projectTaskRepository.saveAll(tasksToCreate);
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

    private List<ProjectTask> loadProjectTaskTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("template/projecttaskbase.json");
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
            throw new RuntimeException("Failed to load template/projecttaskbase.json", e);
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

    private String plusDays(String startDate, int daysToAdd) {
        Date parsedDate = parseProjectDate(startDate);
        if (parsedDate == null) {
            return startDate;
        }

        LocalDate start;
        if (parsedDate instanceof java.sql.Date sqlDate) {
            start = sqlDate.toLocalDate();
        } else {
            start = parsedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return start.plusDays(daysToAdd).toString();
    }

    public Project updateProject(String projectCode, Project projectDetails) {
        return projectRepository.findById(projectCode).map(project -> {
            project.setProjectName(projectDetails.getProjectName());
            project.setProjectDescription(projectDetails.getProjectDescription());
            project.setCustomerId(projectDetails.getCustomerId());
            project.setStartDate(projectDetails.getStartDate());
            project.setEndDate(projectDetails.getEndDate());
            project.setProjectLocation(projectDetails.getProjectLocation());
            project.setLatitude(projectDetails.getLatitude());
            project.setLongitude(projectDetails.getLongitude());
            project.setStatus(projectDetails.getStatus());
            project.setStreamCount(projectDetails.getStreamCount());
            project.setBriefingId(projectDetails.getBriefingId());
            Project savedProject = projectRepository.save(project);

            // Update baselined tasks and milestones to match new project dates
            updateBaselineTasksAndMilestones(savedProject);

            return savedProject;
        }).orElseThrow(() -> new RuntimeException("Project not found with projectCode " + projectCode));
    }

    private void updateBaselineTasksAndMilestones(Project project) {
        // Get all baseline (P) streams for the project
        List<ProjectStream> baselineStreams = projectStreamRepository.findByProjectCodeAndStreamType(project.getProjectCode(), "P");

        if (baselineStreams.isEmpty()) {
            return;
        }

        // For each baseline stream, update baselined tasks and first milestone
        for (ProjectStream stream : baselineStreams) {
            List<ProjectTask> streamTasks = projectTaskRepository.findByProjectStreamId(stream.getProjectStreamId());

            if (streamTasks.isEmpty()) {
                continue;
            }

            // Update baseline tasks from new project start date and let calculation service recompute dates.
            for (ProjectTask task : streamTasks) {
                if ("B".equalsIgnoreCase(task.getTaskType())) {
                    task.setTaskStartDate(project.getStartDate());
                    ProjectTask calculatedTask = projectTaskDateCalculationService.calculateTaskDates(task);
                    ProjectTask savedTask = projectTaskRepository.save(calculatedTask);
                    // Trigger cascading recalculation for this baseline task and all its dependents
                    projectTaskRecalculationService.recalculateAfterTaskChange(savedTask.getProjectTaskId());
                }
            }

            // Find first milestone task (type "M") and update its start and end date to project end date
            ProjectTask firstMilestone = streamTasks.stream()
                    .filter(t -> "M".equalsIgnoreCase(t.getTaskType()))
                    .findFirst()
                    .orElse(null);

            if (firstMilestone != null) {
                firstMilestone.setTaskStartDate(project.getEndDate());
                firstMilestone.setTaskEndDate(project.getEndDate());
                projectTaskRepository.save(firstMilestone);
                // Trigger cascading recalculation for the milestone
                projectTaskRecalculationService.recalculateAfterTaskChange(firstMilestone.getProjectTaskId());
            }
        }
    }

    public void deleteProject(String projectCode) {
        projectRepository.deleteById(projectCode);
    }

    private Double parseCoordinate(String coordinateValue) {
        if (coordinateValue == null || coordinateValue.isBlank()) {
            return null;
        }

        try {
            return Double.valueOf(coordinateValue);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private double calculateDistanceMeters(
            double sourceLatitude,
            double sourceLongitude,
            double targetLatitude,
            double targetLongitude) {

        double sourceLatitudeRadians = Math.toRadians(sourceLatitude);
        double sourceLongitudeRadians = Math.toRadians(sourceLongitude);
        double targetLatitudeRadians = Math.toRadians(targetLatitude);
        double targetLongitudeRadians = Math.toRadians(targetLongitude);

        double deltaLatitude = targetLatitudeRadians - sourceLatitudeRadians;
        double deltaLongitude = targetLongitudeRadians - sourceLongitudeRadians;

        double haversine = Math.sin(deltaLatitude / 2.0d) * Math.sin(deltaLatitude / 2.0d)
                + Math.cos(sourceLatitudeRadians)
                * Math.cos(targetLatitudeRadians)
                * Math.sin(deltaLongitude / 2.0d)
                * Math.sin(deltaLongitude / 2.0d);

        double arc = 2.0d * Math.atan2(Math.sqrt(haversine), Math.sqrt(1.0d - haversine));
        return EARTH_RADIUS_METERS * arc;
    }
}
