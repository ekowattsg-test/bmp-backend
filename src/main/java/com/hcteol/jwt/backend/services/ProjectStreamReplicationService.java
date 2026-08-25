package com.hcteol.jwt.backend.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.dtos.ProjectStreamReplicationRequest;
import com.hcteol.jwt.backend.entities.ProjectAsset;
import com.hcteol.jwt.backend.entities.ProjectBundle;
import com.hcteol.jwt.backend.entities.ProjectManpower;
import com.hcteol.jwt.backend.entities.ProjectSkill;
import com.hcteol.jwt.backend.entities.ProjectStock;
import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.entities.ProjectStreamAsset;
import com.hcteol.jwt.backend.entities.ProjectStreamBundle;
import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.repositories.ProjectAssetRepository;
import com.hcteol.jwt.backend.repositories.ProjectBundleRepository;
import com.hcteol.jwt.backend.repositories.ProjectManpowerRepository;
import com.hcteol.jwt.backend.repositories.ProjectSkillRepository;
import com.hcteol.jwt.backend.repositories.ProjectStockRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamAssetRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamBundleRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;

@Service
public class ProjectStreamReplicationService {

    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectStreamAssetRepository projectStreamAssetRepository;

    @Autowired
    private ProjectStreamBundleRepository projectStreamBundleRepository;

    @Autowired
    private ProjectStockRepository projectStockRepository;

    @Autowired
    private ProjectAssetRepository projectAssetRepository;

    @Autowired
    private ProjectBundleRepository projectBundleRepository;

    @Autowired
    private ProjectSkillRepository projectSkillRepository;

    @Autowired
    private ProjectManpowerRepository projectManpowerRepository;

    @Autowired
    private ProjectTaskDateCalculationService projectTaskDateCalculationService;

    @Autowired
    private ProjectTaskRecalculationService projectTaskRecalculationService;

    @Autowired
    private ProjectStreamDateRecalculationService projectStreamDateRecalculationService;

    @Autowired
    private ProjectStreamService projectStreamService;

    @Transactional
    public ProjectStream replicateStream(Long sourceStreamId, ProjectStreamReplicationRequest request) {
        if (request == null || request.getStreamName() == null || request.getStreamName().isBlank()) {
            throw new IllegalArgumentException("streamName is required");
        }

        ProjectStream sourceStream = projectStreamRepository.findById(sourceStreamId)
                .orElseThrow(() -> new IllegalArgumentException("Source stream not found with id " + sourceStreamId));

        LocalDate sourceStart = parseToLocalDate(sourceStream.getStreamStartDate(), "sourceStream.streamStartDate");
        LocalDate sourceEnd = parseToLocalDate(sourceStream.getStreamEndDate(), "sourceStream.streamEndDate");
        long streamDurationDays = Math.max(ChronoUnit.DAYS.between(sourceStart, sourceEnd), 0L);

        LocalDate anchorStartDate = sourceEnd.plusDays(1);
        String replicatedStreamName = request.getStreamName().trim();

        ProjectStream newStream = new ProjectStream();
        newStream.setProjectCode(sourceStream.getProjectCode());
        newStream.setStreamType(sourceStream.getStreamType());
        newStream.setStreamNumber(projectStreamService.resolveNextStreamNumber(sourceStream.getProjectCode()));
        newStream.setParentStreamNumber(sourceStream.getStreamNumber());
        newStream.setStreamName(replicatedStreamName);
        newStream.setStreamDescription(sourceStream.getStreamDescription());
        newStream.setStreamStartDate(anchorStartDate.toString());
        newStream.setStreamEndDate(anchorStartDate.plusDays(streamDurationDays).toString());
        ProjectStream savedStream = projectStreamRepository.save(newStream);

        ProjectTask anchorTask = new ProjectTask();
        anchorTask.setProjectStreamId(savedStream.getProjectStreamId());
        anchorTask.setTaskType("A");
        anchorTask.setTaskName("Anchor - " + replicatedStreamName);
        anchorTask.setTaskDuration(1L);
        anchorTask.setTaskStartDate(anchorStartDate.toString());
        anchorTask.setTaskStatus("Not Started");
        anchorTask.setProgress(0);
        anchorTask.setActualStartDate(null);
        anchorTask.setActualEndDate(null);
        ProjectTask savedAnchorTask = projectTaskRepository.save(projectTaskDateCalculationService.calculateTaskDates(anchorTask));

        replicateStreamResources(sourceStream.getProjectStreamId(), savedStream.getProjectStreamId());

        List<ProjectTask> sourceTasks = projectTaskRepository.findByProjectStreamId(sourceStream.getProjectStreamId());
        if (!sourceTasks.isEmpty()) {
            replicateTasksAndTaskResources(sourceTasks, savedStream.getProjectStreamId(), savedAnchorTask.getProjectTaskId());
        }

        projectTaskRecalculationService.recalculateAfterTaskChange(savedAnchorTask.getProjectTaskId());
        projectStreamDateRecalculationService.recalculateStreamDatesForProject(sourceStream.getProjectCode());

        return savedStream;
    }

    private void replicateStreamResources(Long sourceStreamId, Long newStreamId) {
        List<ProjectStreamAsset> sourceAssets = projectStreamAssetRepository.findByProjectStreamId(sourceStreamId);
        if (!sourceAssets.isEmpty()) {
            List<ProjectStreamAsset> clonedAssets = new ArrayList<>();
            for (ProjectStreamAsset sourceAsset : sourceAssets) {
                ProjectStreamAsset cloned = new ProjectStreamAsset();
                cloned.setProjectStreamId(newStreamId);
                cloned.setProductId(sourceAsset.getProductId());
                cloned.setQuantity(sourceAsset.getQuantity());
                clonedAssets.add(cloned);
            }
            projectStreamAssetRepository.saveAll(clonedAssets);
        }

        List<ProjectStreamBundle> sourceBundles = projectStreamBundleRepository.findByProjectStreamId(sourceStreamId);
        if (!sourceBundles.isEmpty()) {
            List<ProjectStreamBundle> clonedBundles = new ArrayList<>();
            for (ProjectStreamBundle sourceBundle : sourceBundles) {
                ProjectStreamBundle cloned = new ProjectStreamBundle();
                cloned.setProjectStreamId(newStreamId);
                cloned.setBundleId(sourceBundle.getBundleId());
                cloned.setQuantity(sourceBundle.getQuantity());
                clonedBundles.add(cloned);
            }
            projectStreamBundleRepository.saveAll(clonedBundles);
        }
    }

    private void replicateTasksAndTaskResources(List<ProjectTask> sourceTasks, Long newStreamId, Long anchorTaskId) {
        List<ProjectTask> orderedSourceTasks = sourceTasks.stream()
                .sorted(Comparator
                        .comparing((ProjectTask task) -> safeDateForSort(task.getTaskStartDate()))
                        .thenComparing(task -> task.getProjectTaskId() == null ? Long.MAX_VALUE : task.getProjectTaskId()))
                .toList();

        Long sourceAnchorTaskId = orderedSourceTasks.stream()
                .filter(task -> "A".equalsIgnoreCase(task.getTaskType()))
                .map(ProjectTask::getProjectTaskId)
                .findFirst()
                .orElse(null);

        List<ProjectTask> sourceTasksToReplicate = orderedSourceTasks.stream()
                .filter(task -> !"A".equalsIgnoreCase(task.getTaskType()))
                .toList();

        if (sourceTasksToReplicate.isEmpty()) {
            return;
        }

        Long firstReplicatedTaskSourceId = sourceTasksToReplicate.get(0).getProjectTaskId();

        Map<Long, ProjectTask> sourceById = new HashMap<>();
        for (ProjectTask sourceTask : sourceTasksToReplicate) {
            if (sourceTask.getProjectTaskId() != null) {
                sourceById.put(sourceTask.getProjectTaskId(), sourceTask);
            }
        }

        List<ProjectTask> firstPass = new ArrayList<>();
        Map<Long, Long> sourceToNewTaskId = new HashMap<>();

        for (ProjectTask sourceTask : sourceTasksToReplicate) {
            ProjectTask clonedTask = new ProjectTask();
            clonedTask.setProjectStreamId(newStreamId);
            clonedTask.setTaskType(sourceTask.getTaskType());
            clonedTask.setTaskName(sourceTask.getTaskName());
            clonedTask.setStaffId(sourceTask.getStaffId());
            clonedTask.setParentTaskId(null);
            clonedTask.setMilestoneTaskId(null);
            clonedTask.setTaskDuration(sourceTask.getTaskDuration());
            clonedTask.setTaskStartDate(sourceTask.getTaskStartDate());
            clonedTask.setTaskEndDate(sourceTask.getTaskEndDate());
            clonedTask.setTaskStatus("Not Started");
            clonedTask.setProgress(sourceTask.getProgress() == null ? 0 : sourceTask.getProgress());
            clonedTask.setActualStartDate(null);
            clonedTask.setActualEndDate(null);
            clonedTask.setRemarks(sourceTask.getRemarks());

            ProjectTask savedCloned = projectTaskRepository.save(clonedTask);
            firstPass.add(savedCloned);
            if (sourceTask.getProjectTaskId() != null && savedCloned.getProjectTaskId() != null) {
                sourceToNewTaskId.put(sourceTask.getProjectTaskId(), savedCloned.getProjectTaskId());
            }
        }

        List<Long> recalculationRoots = new ArrayList<>();

        for (ProjectTask savedTask : firstPass) {
            Long newTaskId = savedTask.getProjectTaskId();
            Long sourceTaskId = findSourceTaskIdByNewId(sourceToNewTaskId, newTaskId);
            if (sourceTaskId == null) {
                continue;
            }

            ProjectTask sourceTask = sourceById.get(sourceTaskId);
            if (sourceTask == null) {
                continue;
            }

            if (sourceTaskId.equals(firstReplicatedTaskSourceId)
                    || (sourceAnchorTaskId != null && sourceAnchorTaskId.equals(sourceTask.getParentTaskId()))) {
                savedTask.setParentTaskId(anchorTaskId);
                recalculationRoots.add(newTaskId);
            } else if (sourceTask.getParentTaskId() != null) {
                Long mappedParentTaskId = sourceToNewTaskId.get(sourceTask.getParentTaskId());
                if (mappedParentTaskId == null && sourceAnchorTaskId != null && sourceAnchorTaskId.equals(sourceTask.getParentTaskId())) {
                    mappedParentTaskId = anchorTaskId;
                }
                savedTask.setParentTaskId(mappedParentTaskId);
            }

            if (sourceTask.getMilestoneTaskId() != null) {
                savedTask.setMilestoneTaskId(sourceToNewTaskId.get(sourceTask.getMilestoneTaskId()));
            }

            ProjectTask calculatedTask = projectTaskDateCalculationService.calculateTaskDates(savedTask);
            projectTaskRepository.save(calculatedTask);

            replicateTaskResources(sourceTaskId, newTaskId);
            recalculationRoots.add(newTaskId);
        }

        for (Long taskId : recalculationRoots.stream().distinct().toList()) {
            if (taskId != null) {
                projectTaskRecalculationService.recalculateAfterTaskChange(taskId);
            }
        }
    }

    private Long findSourceTaskIdByNewId(Map<Long, Long> sourceToNewTaskId, Long newTaskId) {
        if (newTaskId == null) {
            return null;
        }
        for (Map.Entry<Long, Long> entry : sourceToNewTaskId.entrySet()) {
            if (newTaskId.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void replicateTaskResources(Long sourceTaskId, Long newTaskId) {
        List<ProjectStock> sourceStocks = projectStockRepository.findByProjectTaskId(sourceTaskId);
        if (!sourceStocks.isEmpty()) {
            List<ProjectStock> clonedStocks = new ArrayList<>();
            for (ProjectStock sourceStock : sourceStocks) {
                ProjectStock cloned = new ProjectStock();
                cloned.setProjectTaskId(newTaskId);
                cloned.setProductId(sourceStock.getProductId());
                cloned.setQuantity(sourceStock.getQuantity());
                clonedStocks.add(cloned);
            }
            projectStockRepository.saveAll(clonedStocks);
        }

        List<ProjectAsset> sourceAssets = projectAssetRepository.findByProjectTaskId(sourceTaskId);
        if (!sourceAssets.isEmpty()) {
            List<ProjectAsset> clonedAssets = new ArrayList<>();
            for (ProjectAsset sourceAsset : sourceAssets) {
                ProjectAsset cloned = new ProjectAsset();
                cloned.setProjectTaskId(newTaskId);
                cloned.setProductId(sourceAsset.getProductId());
                cloned.setQuantity(sourceAsset.getQuantity());
                clonedAssets.add(cloned);
            }
            projectAssetRepository.saveAll(clonedAssets);
        }

        List<ProjectBundle> sourceBundles = projectBundleRepository.findByProjectTaskId(sourceTaskId);
        if (!sourceBundles.isEmpty()) {
            List<ProjectBundle> clonedBundles = new ArrayList<>();
            for (ProjectBundle sourceBundle : sourceBundles) {
                ProjectBundle cloned = new ProjectBundle();
                cloned.setProjectTaskId(newTaskId);
                cloned.setBundleId(sourceBundle.getBundleId());
                cloned.setQuantity(sourceBundle.getQuantity());
                clonedBundles.add(cloned);
            }
            projectBundleRepository.saveAll(clonedBundles);
        }

        List<ProjectSkill> sourceSkills = projectSkillRepository.findByProjectTaskId(sourceTaskId);
        if (!sourceSkills.isEmpty()) {
            List<ProjectSkill> clonedSkills = new ArrayList<>();
            for (ProjectSkill sourceSkill : sourceSkills) {
                ProjectSkill cloned = new ProjectSkill();
                cloned.setProjectTaskId(newTaskId);
                cloned.setSkillId(sourceSkill.getSkillId());
                cloned.setUnit(sourceSkill.getUnit());
                clonedSkills.add(cloned);
            }
            List<ProjectSkill> savedClonedSkills = projectSkillRepository.saveAll(clonedSkills);

            Map<Long, Long> sourceToNewSkillId = new HashMap<>();
            for (int i = 0; i < sourceSkills.size() && i < savedClonedSkills.size(); i++) {
                Long sourceSkillId = sourceSkills.get(i).getProjectSkillId();
                Long newSkillId = savedClonedSkills.get(i).getProjectSkillId();
                if (sourceSkillId != null && newSkillId != null) {
                    sourceToNewSkillId.put(sourceSkillId, newSkillId);
                }
            }

            replicateTaskManpower(sourceTaskId, newTaskId, sourceToNewSkillId);
        }
    }

    private void replicateTaskManpower(Long sourceTaskId, Long newTaskId, Map<Long, Long> sourceToNewSkillId) {
        List<ProjectManpower> sourceManpowers = projectManpowerRepository.findByProjectTaskId(sourceTaskId);
        if (sourceManpowers.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        List<ProjectManpower> clonedManpowers = new ArrayList<>();
        for (ProjectManpower sourceManpower : sourceManpowers) {
            Long sourceSkillId = sourceManpower.getProjectSkillId();
            Long newSkillId = sourceSkillId == null ? null : sourceToNewSkillId.get(sourceSkillId);
            if (sourceSkillId != null && newSkillId == null) {
                continue;
            }

            ProjectManpower cloned = new ProjectManpower();
            cloned.setProjectTaskId(newTaskId);
            cloned.setProjectSkillId(newSkillId);
            cloned.setWorkDate(sourceManpower.getWorkDate());
            cloned.setStaffId(sourceManpower.getStaffId());
            cloned.setLoading(sourceManpower.getLoading());
            cloned.setManpowerTouched(resolveReplicatedManpowerTouched(sourceManpower, today));
            clonedManpowers.add(cloned);
        }

        if (!clonedManpowers.isEmpty()) {
            projectManpowerRepository.saveAll(clonedManpowers);
        }
    }

    private Integer resolveReplicatedManpowerTouched(ProjectManpower sourceManpower, LocalDate today) {
        String workDate = sourceManpower.getWorkDate();
        if (workDate == null || workDate.isBlank()) {
            return sourceManpower.getManpowerTouched();
        }

        LocalDate clonedWorkDate = safeDateForSort(workDate);
        if (clonedWorkDate.isAfter(today)) {
            return 0;
        }

        return sourceManpower.getManpowerTouched();
    }

    private LocalDate safeDateForSort(String dateValue) {
        try {
            return parseToLocalDate(dateValue, "dateValue");
        } catch (IllegalArgumentException ex) {
            return LocalDate.MAX;
        }
    }

    private LocalDate parseToLocalDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        String trimmed = value.trim();
        try {
            return Instant.parse(trimmed).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(trimmed);
            } catch (DateTimeParseException ignoredDateOnly) {
                try {
                    return LocalDateTime.parse(trimmed).toLocalDate();
                } catch (DateTimeParseException ignoredDateTime) {
                    throw new IllegalArgumentException("Invalid date value in " + fieldName + ": " + value);
                }
            }
        }
    }
}
