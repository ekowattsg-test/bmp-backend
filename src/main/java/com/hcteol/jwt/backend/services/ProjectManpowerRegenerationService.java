package com.hcteol.jwt.backend.services;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.dtos.ProjectManpowerRegenerationResult;
import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.entities.ProjectManpower;
import com.hcteol.jwt.backend.entities.ProjectSkill;
import com.hcteol.jwt.backend.entities.ProjectStream;
import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.entities.Staff;
import com.hcteol.jwt.backend.entities.staffSkillProfile;
import com.hcteol.jwt.backend.repositories.ParamRepository;
import com.hcteol.jwt.backend.repositories.ProjectManpowerRepository;
import com.hcteol.jwt.backend.repositories.ProjectSkillRepository;
import com.hcteol.jwt.backend.repositories.ProjectStreamRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;
import com.hcteol.jwt.backend.repositories.StaffRepository;
import com.hcteol.jwt.backend.repositories.StaffSkillProfileRepository;

@Service
public class ProjectManpowerRegenerationService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectSkillRepository projectSkillRepository;

    @Autowired
    private ProjectManpowerRepository projectManpowerRepository;

    @Autowired
    private ProjectStreamRepository projectStreamRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffSkillProfileRepository staffSkillProfileRepository;

    @Autowired
    private ParamRepository paramRepository;

    private static final double TASK_SIMILARITY_THRESHOLD = 0.4;

    private static final Set<String> TASK_NAME_STOPWORDS = Set.of(
            "a", "an", "the", "and", "or", "for", "to", "of", "in", "on", "at", "by", "with",
            "from", "into", "is", "are", "be", "as", "this", "that",
            "task", "tasks", "work", "job", "activity", "activities", "process", "step", "steps",
            "do", "does", "done", "new", "old", "update", "updated",
            "dan", "atau", "untuk", "yang", "dengan", "dari", "ke", "di", "pada", "dalam", "ini", "itu",
            "tugas", "kerja", "pekerjaan", "proses", "tahap", "langkah", "baru", "lama", "danlain", "dll",
            "serta", "oleh", "agar", "supaya", "sebagai");

    @Transactional
    public ProjectManpowerRegenerationResult regenerateForwardDatedManpower() {
        return regenerateForwardDatedManpower(LocalDate.now());
    }

    @Transactional
    public ProjectManpowerRegenerationResult regenerateForwardDatedManpower(LocalDate runDate) {
        Objects.requireNonNull(runDate, "runDate cannot be null");
        Instant serviceStartTime = Instant.now();

        int deletedCount = (int) projectManpowerRepository.deleteByWorkDateIsNull();
        WorkdaySettings workdaySettings = resolveWorkdaySettings();

        List<ProjectTask> allTasks = projectTaskRepository.findAll();
        List<ProjectTask> eligibleTasks = allTasks.stream()
                .filter(task -> task != null)
                .filter(task -> !isCompleted(task))
                .toList();

        if (eligibleTasks.isEmpty()) {
            return buildResult(deletedCount, 0, 0, serviceStartTime);
        }

        LinkedHashSet<Long> eligibleTaskIds = new LinkedHashSet<>();
        for (ProjectTask task : eligibleTasks) {
            Long taskId = task.getProjectTaskId();
            if (taskId == null) {
                continue;
            }
            eligibleTaskIds.add(taskId);
        }

        Map<Long, List<ProjectManpower>> futureRowsByTaskId = new HashMap<>();
        if (!eligibleTaskIds.isEmpty()) {
            for (ProjectManpower manpower : projectManpowerRepository.findByProjectTaskIdIn(new ArrayList<>(eligibleTaskIds))) {
                if (manpower == null || manpower.getProjectTaskId() == null) {
                    continue;
                }
                LocalDate workDate = parseToLocalDate(manpower.getWorkDate());
                if (workDate == null || !workDate.isAfter(runDate)) {
                    continue;
                }
                futureRowsByTaskId.computeIfAbsent(manpower.getProjectTaskId(), ignored -> new ArrayList<>()).add(manpower);
            }
        }

        List<ProjectManpower> rowsToSave = new ArrayList<>();
        List<ProjectManpower> rowsToDelete = new ArrayList<>();
        List<ProjectManpower> rowsToCreate = new ArrayList<>();
        for (ProjectTask task : eligibleTasks) {
            LocalDate effectiveStart = resolveEffectiveStartDate(task).orElse(null);
            LocalDate effectiveEnd = resolveEffectiveEndDate(task).orElse(null);
            Long taskId = task.getProjectTaskId();
            if (taskId == null) {
                continue;
            }

            List<ProjectSkill> projectSkills = projectSkillRepository.findByProjectTaskId(taskId);
            Map<ManpowerSlotKey, Integer> requiredCountBySlot = new HashMap<>();
            if (effectiveStart != null && effectiveEnd != null && !effectiveEnd.isBefore(runDate)) {
                LocalDate generationStart = effectiveStart.isAfter(runDate) ? effectiveStart : runDate.plusDays(1);
                if (!generationStart.isAfter(effectiveEnd)) {
                    for (ProjectSkill projectSkill : projectSkills) {
                        if (projectSkill == null || projectSkill.getProjectSkillId() == null) {
                            continue;
                        }

                        int requiredCount = normalizeRequiredCount(projectSkill.getUnit());
                        for (LocalDate cursor = generationStart; !cursor.isAfter(effectiveEnd); cursor = cursor.plusDays(1)) {
                            if (!isWorkingDay(cursor.getDayOfWeek(), workdaySettings)) {
                                continue;
                            }
                            requiredCountBySlot.put(new ManpowerSlotKey(taskId, projectSkill.getProjectSkillId(), cursor), requiredCount);
                        }
                    }
                }
            }

            Map<ManpowerSlotKey, List<ProjectManpower>> existingRowsBySlot = new HashMap<>();
            for (ProjectManpower manpower : futureRowsByTaskId.getOrDefault(taskId, List.of())) {
                if (manpower == null) {
                    continue;
                }
                LocalDate workDate = parseToLocalDate(manpower.getWorkDate());
                if (workDate == null) {
                    continue;
                }
                ManpowerSlotKey slotKey = new ManpowerSlotKey(taskId, manpower.getProjectSkillId(), workDate);
                existingRowsBySlot.computeIfAbsent(slotKey, ignored -> new ArrayList<>()).add(manpower);
            }

            LinkedHashSet<ManpowerSlotKey> slotsToProcess = new LinkedHashSet<>();
            slotsToProcess.addAll(existingRowsBySlot.keySet());
            slotsToProcess.addAll(requiredCountBySlot.keySet());

            for (ManpowerSlotKey slotKey : slotsToProcess) {
                int requiredCount = requiredCountBySlot.getOrDefault(slotKey, 0);
                deletedCount += reconcileSlot(
                        existingRowsBySlot.getOrDefault(slotKey, List.of()),
                        slotKey,
                        requiredCount,
                        rowsToSave,
                        rowsToDelete,
                        rowsToCreate);
            }
        }

        if (!rowsToDelete.isEmpty()) {
            projectManpowerRepository.deleteAll(rowsToDelete);
        }
        if (!rowsToSave.isEmpty()) {
            projectManpowerRepository.saveAll(rowsToSave);
        }
        if (!rowsToCreate.isEmpty()) {
            projectManpowerRepository.saveAll(rowsToCreate);
        }

        int assignedCount = assignForwardDatedRows(runDate);

        return buildResult(deletedCount, rowsToCreate.size(), assignedCount, serviceStartTime);
    }

    private int reconcileSlot(
            List<ProjectManpower> existingRows,
            ManpowerSlotKey slotKey,
            int requiredCount,
            List<ProjectManpower> rowsToSave,
            List<ProjectManpower> rowsToDelete,
            List<ProjectManpower> rowsToCreate) {

        List<ProjectManpower> touchedRows = new ArrayList<>();
        List<ProjectManpower> untouchedRows = new ArrayList<>();
        for (ProjectManpower manpower : existingRows) {
            if (getManpowerTouched(manpower) == 0) {
                untouchedRows.add(manpower);
            } else {
                touchedRows.add(manpower);
            }
        }

        List<ProjectManpower> clearedUntouchedRows = new ArrayList<>();
        for (ProjectManpower manpower : untouchedRows) {
            if (!isBlankStaffId(manpower.getStaffId())) {
                manpower.setStaffId(null);
                clearedUntouchedRows.add(manpower);
            }
        }

        int existingCount = existingRows.size();
        if (existingCount < requiredCount) {
            for (int i = 0; i < requiredCount - existingCount; i++) {
                ProjectManpower manpower = new ProjectManpower();
                manpower.setProjectTaskId(slotKey.projectTaskId());
                manpower.setProjectSkillId(slotKey.projectSkillId());
                manpower.setWorkDate(slotKey.workDate().toString());
                manpower.setStaffId(null);
                manpower.setLoading(1.0);
                manpower.setManpowerTouched(0);
                rowsToCreate.add(manpower);
            }
            rowsToSave.addAll(clearedUntouchedRows);
            return 0;
        }

        List<ProjectManpower> rowsSelectedForDelete = new ArrayList<>();
        if (existingCount > requiredCount) {
            int excessCount = existingCount - requiredCount;
            List<ProjectManpower> removableUntouchedRows = untouchedRows.stream()
                    .sorted(Comparator
                            .comparing((ProjectManpower manpower) -> isBlankStaffId(manpower.getStaffId()) ? 0 : 1)
                            .thenComparing(
                                    manpower -> Optional.ofNullable(manpower.getProjectManpowerId()).orElse(Long.MIN_VALUE),
                                    Comparator.reverseOrder()))
                    .toList();

            int removableCount = Math.min(excessCount, removableUntouchedRows.size());
            rowsSelectedForDelete.addAll(removableUntouchedRows.subList(0, removableCount));

            // REVIEW_MARKER: Remove this preservation branch if the final business rule must strictly trim touched rows down to required unit.
            if (excessCount > removableUntouchedRows.size() && !touchedRows.isEmpty()) {
                // Preserve touched rows for now, even when they exceed the current required unit.
            }
        }

        rowsToDelete.addAll(rowsSelectedForDelete);
        for (ProjectManpower manpower : clearedUntouchedRows) {
            if (!rowsSelectedForDelete.contains(manpower)) {
                rowsToSave.add(manpower);
            }
        }

        return rowsSelectedForDelete.size();
    }

    private int normalizeRequiredCount(Integer unit) {
        return unit == null || unit < 1 ? 0 : unit;
    }

    private boolean isBlankStaffId(String staffId) {
        return staffId == null || staffId.isBlank();
    }

    private ProjectManpowerRegenerationResult buildResult(
            int deletedCount,
            int createdCount,
            int assignedCount,
            Instant serviceStartTime) {
        Instant serviceEndTime = Instant.now();
        long totalTimeTakenMs = Math.max(0L, serviceEndTime.toEpochMilli() - serviceStartTime.toEpochMilli());
        return new ProjectManpowerRegenerationResult(
                deletedCount,
                createdCount,
                assignedCount,
                serviceStartTime.toString(),
                serviceEndTime.toString(),
                totalTimeTakenMs);
    }

    private int assignForwardDatedRows(LocalDate runDate) {
        int assignmentHorizonDays = parseParamInt("manpowerAssignmentHorizonDays", 30, 0, 3650);
        LocalDate assignmentHorizonDate = runDate.plusDays(assignmentHorizonDays);

        List<ProjectTask> allTasks = projectTaskRepository.findAll();
        List<ProjectSkill> allProjectSkills = projectSkillRepository.findAll();
        List<ProjectManpower> allManpowers = projectManpowerRepository.findAll();

        Map<Long, ProjectTask> taskById = new HashMap<>();
        for (ProjectTask task : allTasks) {
            if (task != null && task.getProjectTaskId() != null) {
                taskById.put(task.getProjectTaskId(), task);
            }
        }

        Map<Long, ProjectSkill> projectSkillById = new HashMap<>();
        for (ProjectSkill skill : allProjectSkills) {
            if (skill != null && skill.getProjectSkillId() != null) {
                projectSkillById.put(skill.getProjectSkillId(), skill);
            }
        }

        Map<String, Staff> staffById = new HashMap<>();
        for (Staff staff : staffRepository.findAll()) {
            if (staff != null && staff.getStaffId() != null) {
                staffById.put(staff.getStaffId(), staff);
            }
        }

        Map<Long, List<staffSkillProfile>> profilesBySkillId = new HashMap<>();
        for (staffSkillProfile profile : staffSkillProfileRepository.findAll()) {
            if (profile == null || profile.getStaffSkillId() == null || profile.getStaffId() == null) {
                continue;
            }
            profilesBySkillId.computeIfAbsent(profile.getStaffSkillId(), ignored -> new ArrayList<>()).add(profile);
        }

        Map<Long, String> projectCodeByStreamId = new HashMap<>();
        for (ProjectStream stream : projectStreamRepository.findAll()) {
            if (stream != null && stream.getProjectStreamId() != null) {
                projectCodeByStreamId.put(stream.getProjectStreamId(), stream.getProjectCode());
            }
        }

        Map<Long, TreeSet<String>> staffByRequiredSkill = new HashMap<>();
        for (Map.Entry<Long, List<staffSkillProfile>> entry : profilesBySkillId.entrySet()) {
            Long skillId = entry.getKey();
            for (staffSkillProfile profile : entry.getValue()) {
                Staff staff = staffById.get(profile.getStaffId());
                if (!isStaffContractValid(staff, runDate)) {
                    continue;
                }
                if (!isSkillProfileValid(profile, runDate)) {
                    continue;
                }
                staffByRequiredSkill.computeIfAbsent(skillId, ignored -> new TreeSet<>()).add(profile.getStaffId());
            }
        }

        for (Map.Entry<Long, TreeSet<String>> entry : new HashMap<>(staffByRequiredSkill).entrySet()) {
            TreeSet<String> filtered = new TreeSet<>();
            for (String staffId : entry.getValue()) {
                Staff staff = staffById.get(staffId);
                if (isStaffContractValid(staff, runDate)) {
                    filtered.add(staffId);
                }
            }
            if (filtered.isEmpty()) {
                staffByRequiredSkill.remove(entry.getKey());
            } else {
                staffByRequiredSkill.put(entry.getKey(), filtered);
            }
        }

        List<DeploymentRecord> history = new ArrayList<>();
        Map<LocalDate, Set<String>> busyByDate = new HashMap<>();
        for (ProjectManpower manpower : allManpowers) {
            if (manpower == null || manpower.getStaffId() == null || manpower.getStaffId().isBlank()) {
                continue;
            }
            LocalDate workDate = parseToLocalDate(manpower.getWorkDate());
            if (workDate == null) {
                continue;
            }

            Long taskId = manpower.getProjectTaskId();
            Long projectSkillId = manpower.getProjectSkillId();
            if (taskId == null || projectSkillId == null) {
                continue;
            }

            ProjectTask task = taskById.get(taskId);
            ProjectSkill projectSkill = projectSkillById.get(projectSkillId);
            if (task == null || projectSkill == null || projectSkill.getSkillId() == null) {
                continue;
            }

            String projectCode = projectCodeByStreamId.get(task.getProjectStreamId());
            history.add(new DeploymentRecord(
                    manpower.getStaffId(),
                    taskId,
                    projectSkill.getSkillId(),
                    projectCode,
                    safeString(task.getTaskName()),
                    workDate,
                    getManpowerTouched(manpower),
                    isCompleted(task)));

            busyByDate.computeIfAbsent(workDate, ignored -> new HashSet<>()).add(manpower.getStaffId());
        }

        List<ProjectManpower> targets = new ArrayList<>();
        for (ProjectManpower manpower : allManpowers) {
            if (manpower == null || manpower.getStaffId() != null) {
                continue;
            }

            LocalDate workDate = parseToLocalDate(manpower.getWorkDate());
            if (workDate == null || !workDate.isAfter(runDate) || workDate.isAfter(assignmentHorizonDate)) {
                continue;
            }

            ProjectTask task = taskById.get(manpower.getProjectTaskId());
            if (task == null || isCompleted(task) || getManpowerTouched(manpower) != 0) {
                continue;
            }

            ProjectSkill projectSkill = projectSkillById.get(manpower.getProjectSkillId());
            if (projectSkill == null || projectSkill.getSkillId() == null) {
                continue;
            }

            targets.add(manpower);
        }

        List<ProjectManpower> changedRows = new ArrayList<>();
        Map<LocalDate, List<ProjectManpower>> targetsByDate = new HashMap<>();
        for (ProjectManpower target : targets) {
            LocalDate workDate = parseToLocalDate(target.getWorkDate());
            if (workDate == null) {
                continue;
            }
            targetsByDate.computeIfAbsent(workDate, ignored -> new ArrayList<>()).add(target);
        }

        List<LocalDate> sortedDates = targetsByDate.keySet().stream().sorted().toList();
        for (LocalDate workDate : sortedDates) {
            List<ProjectManpower> dateTargets = new ArrayList<>(targetsByDate.getOrDefault(workDate, List.of()));
            dateTargets.sort(Comparator.comparing(target -> Optional.ofNullable(target.getProjectManpowerId()).orElse(Long.MAX_VALUE)));

            Map<Long, List<ProjectManpower>> targetsByRequiredSkill = new HashMap<>();
            for (ProjectManpower target : dateTargets) {
                ProjectSkill projectSkill = projectSkillById.get(target.getProjectSkillId());
                if (projectSkill == null || projectSkill.getSkillId() == null) {
                    continue;
                }
                targetsByRequiredSkill.computeIfAbsent(projectSkill.getSkillId(), ignored -> new ArrayList<>()).add(target);
            }

            List<Long> requiredSkills = new ArrayList<>(targetsByRequiredSkill.keySet());
            requiredSkills.sort(Comparator
                    .comparing((Long skillId) -> isCriticalSkill(skillId, targetsByRequiredSkill, staffByRequiredSkill, busyByDate, workDate) ? 0 : 1)
                    .thenComparingDouble(skillId -> scarcityRatio(skillId, targetsByRequiredSkill, staffByRequiredSkill, busyByDate, workDate))
                    .thenComparingLong(skillId -> skillId));

            Set<Long> processedTargetIds = new HashSet<>();

            // Pass 1: assign critical/scarce skill demand first.
            for (Long skillId : requiredSkills) {
                if (!isCriticalSkill(skillId, targetsByRequiredSkill, staffByRequiredSkill, busyByDate, workDate)) {
                    continue;
                }

                List<ProjectManpower> skillTargets = targetsByRequiredSkill.getOrDefault(skillId, List.of());
                skillTargets.sort(Comparator.comparing(target -> Optional.ofNullable(target.getProjectManpowerId()).orElse(Long.MAX_VALUE)));
                for (ProjectManpower target : skillTargets) {
                    if (assignSingleTarget(target, workDate, runDate, taskById, projectSkillById, projectCodeByStreamId,
                            history, busyByDate, staffByRequiredSkill, changedRows)) {
                        processedTargetIds.add(Optional.ofNullable(target.getProjectManpowerId()).orElse(Long.MIN_VALUE));
                    }
                }
            }

            // Pass 2: assign remaining date rows using existing context-priority selector.
            for (ProjectManpower target : dateTargets) {
                Long targetId = Optional.ofNullable(target.getProjectManpowerId()).orElse(Long.MIN_VALUE);
                if (processedTargetIds.contains(targetId)) {
                    continue;
                }
                assignSingleTarget(target, workDate, runDate, taskById, projectSkillById, projectCodeByStreamId,
                        history, busyByDate, staffByRequiredSkill, changedRows);
            }
        }

        if (!changedRows.isEmpty()) {
            projectManpowerRepository.saveAll(changedRows);
        }

        return changedRows.size();
    }

    private boolean assignSingleTarget(
            ProjectManpower target,
            LocalDate workDate,
            LocalDate runDate,
            Map<Long, ProjectTask> taskById,
            Map<Long, ProjectSkill> projectSkillById,
            Map<Long, String> projectCodeByStreamId,
            List<DeploymentRecord> history,
            Map<LocalDate, Set<String>> busyByDate,
            Map<Long, TreeSet<String>> staffByRequiredSkill,
            List<ProjectManpower> changedRows) {

        ProjectTask task = taskById.get(target.getProjectTaskId());
        ProjectSkill projectSkill = projectSkillById.get(target.getProjectSkillId());
        if (task == null || projectSkill == null || projectSkill.getSkillId() == null) {
            return false;
        }

        String selectedStaffId = selectStaffForTarget(
                task,
                projectSkill,
                workDate,
                runDate,
                history,
                busyByDate,
                staffByRequiredSkill,
                projectCodeByStreamId.get(task.getProjectStreamId()));

        if (selectedStaffId == null) {
            return false;
        }

        target.setStaffId(selectedStaffId);
        changedRows.add(target);

        busyByDate.computeIfAbsent(workDate, ignored -> new HashSet<>()).add(selectedStaffId);
        history.add(new DeploymentRecord(
                selectedStaffId,
                task.getProjectTaskId(),
                projectSkill.getSkillId(),
                projectCodeByStreamId.get(task.getProjectStreamId()),
                safeString(task.getTaskName()),
                workDate,
                getManpowerTouched(target),
                isCompleted(task)));
        return true;
    }

    private boolean isCriticalSkill(
            Long skillId,
            Map<Long, List<ProjectManpower>> targetsByRequiredSkill,
            Map<Long, TreeSet<String>> staffByRequiredSkill,
            Map<LocalDate, Set<String>> busyByDate,
            LocalDate workDate) {
        int demand = targetsByRequiredSkill.getOrDefault(skillId, List.of()).size();
        int supply = availableSupply(skillId, staffByRequiredSkill, busyByDate, workDate);
        return supply <= demand;
    }

    private double scarcityRatio(
            Long skillId,
            Map<Long, List<ProjectManpower>> targetsByRequiredSkill,
            Map<Long, TreeSet<String>> staffByRequiredSkill,
            Map<LocalDate, Set<String>> busyByDate,
            LocalDate workDate) {
        int demand = targetsByRequiredSkill.getOrDefault(skillId, List.of()).size();
        if (demand <= 0) {
            return Double.MAX_VALUE;
        }
        int supply = availableSupply(skillId, staffByRequiredSkill, busyByDate, workDate);
        return (double) supply / demand;
    }

    private int availableSupply(
            Long skillId,
            Map<Long, TreeSet<String>> staffByRequiredSkill,
            Map<LocalDate, Set<String>> busyByDate,
            LocalDate workDate) {
        TreeSet<String> candidates = staffByRequiredSkill.get(skillId);
        if (candidates == null || candidates.isEmpty()) {
            return 0;
        }
        Set<String> busyStaff = busyByDate.getOrDefault(workDate, Set.of());
        int count = 0;
        for (String staffId : candidates) {
            if (!busyStaff.contains(staffId)) {
                count++;
            }
        }
        return count;
    }

    private String selectStaffForTarget(
            ProjectTask task,
            ProjectSkill projectSkill,
            LocalDate workDate,
            LocalDate runDate,
            List<DeploymentRecord> history,
            Map<LocalDate, Set<String>> busyByDate,
            Map<Long, TreeSet<String>> staffByRequiredSkill,
            String taskProjectCode) {

        TreeSet<String> baseStaff = staffByRequiredSkill.get(projectSkill.getSkillId());
        if (baseStaff == null || baseStaff.isEmpty()) {
            return null;
        }

        Set<String> busyStaff = busyByDate.getOrDefault(workDate, Set.of());
        List<String> availableStaff = baseStaff.stream()
                .filter(staffId -> !busyStaff.contains(staffId))
                .filter(staffId -> isStaffAssignableOnDate(staffId, projectSkill.getSkillId(), workDate))
                .toList();

        if (availableStaff.isEmpty()) {
            return null;
        }

        Set<String> availableSet = new HashSet<>(availableStaff);

        String sameTaskStaff = findMostRecentStaff(
                history,
                availableSet,
                workDate,
                record -> Objects.equals(record.taskId(), task.getProjectTaskId())
                && Objects.equals(record.requiredSkillId(), projectSkill.getSkillId()));
        if (sameTaskStaff != null) {
            return sameTaskStaff;
        }

        String sameProjectSimilar = findMostRecentStaff(
                history,
                availableSet,
                workDate,
                record -> Objects.equals(record.requiredSkillId(), projectSkill.getSkillId())
                && Objects.equals(record.projectCode(), taskProjectCode)
                && isSimilarTaskContext(record.taskName(), safeString(task.getTaskName())));
        if (sameProjectSimilar != null) {
            return sameProjectSimilar;
        }

        String crossProjectSimilar = findCrossProjectStaffWithPriority(
                history,
                availableSet,
                runDate,
                record -> Objects.equals(record.requiredSkillId(), projectSkill.getSkillId())
                && !Objects.equals(record.projectCode(), taskProjectCode)
                && isSimilarTaskContext(record.taskName(), safeString(task.getTaskName())));
        if (crossProjectSimilar != null) {
            return crossProjectSimilar;
        }

        return availableStaff.get(0);
    }

    private String findMostRecentStaff(
            List<DeploymentRecord> history,
            Set<String> availableStaff,
            LocalDate targetDate,
            java.util.function.Predicate<DeploymentRecord> matcher) {

        return history.stream()
                .filter(record -> record.workDate() != null && record.workDate().isBefore(targetDate))
                .filter(record -> availableStaff.contains(record.staffId()))
                .filter(matcher)
                .sorted(Comparator
                        .comparing((DeploymentRecord record) -> record.workDate(), Comparator.reverseOrder())
                        .thenComparing(record -> record.staffId()))
                .map(record -> record.staffId())
                .findFirst()
                .orElse(null);
    }

    private String findCrossProjectStaffWithPriority(
            List<DeploymentRecord> history,
            Set<String> availableStaff,
            LocalDate runDate,
            java.util.function.Predicate<DeploymentRecord> matcher) {

        return history.stream()
                .filter(record -> availableStaff.contains(record.staffId()))
                .filter(matcher)
                .sorted(Comparator
                        .comparing((DeploymentRecord record) -> crossProjectPriorityTier(record, runDate))
                        .thenComparing((DeploymentRecord record) -> record.workDate(), Comparator.reverseOrder())
                        .thenComparing(record -> record.staffId()))
                .map(record -> record.staffId())
                .findFirst()
                .orElse(null);
    }

    private int crossProjectPriorityTier(DeploymentRecord record, LocalDate runDate) {
        if (record.sourceTaskCompleted()) {
            return 1;
        }

        boolean priorDate = runDate.isAfter(record.workDate());
        if (record.sourceManpowerTouched() == 1) {
            return priorDate ? 1 : 2;
        }
        return priorDate ? 3 : 4;
    }

    private boolean isStaffAssignableOnDate(String staffId, Long skillId, LocalDate workDate) {
        if (staffId == null || skillId == null || workDate == null) {
            return false;
        }

        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (!isStaffContractValid(staff, workDate)) {
            return false;
        }

        List<staffSkillProfile> profiles = staffSkillProfileRepository.findByStaffSkillId(skillId);
        for (staffSkillProfile profile : profiles) {
            if (!staffId.equals(profile.getStaffId())) {
                continue;
            }
            if (isSkillProfileValid(profile, workDate)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStaffContractValid(Staff staff, LocalDate workDate) {
        if (staff == null || staff.getStaffId() == null) {
            return false;
        }
        if (staff.getActive() == null || staff.getActive() != 1) {
            return false;
        }
        if (staff.getServiceEndDate() == null) {
            return true;
        }
        return !workDate.isAfter(staff.getServiceEndDate().toLocalDate());
    }

    private boolean isSkillProfileValid(staffSkillProfile profile, LocalDate workDate) {
        if (profile == null || profile.getStaffId() == null || profile.getStaffSkillId() == null) {
            return false;
        }
        if (profile.getNoExpiry() != null && profile.getNoExpiry() == 1) {
            return true;
        }
        if (profile.getExpiryDate() == null) {
            return true;
        }
        return !workDate.isAfter(profile.getExpiryDate().toLocalDate());
    }

    private boolean isSimilarTaskContext(String leftTaskName, String rightTaskName) {
        Set<String> leftTokens = tokenizeTaskName(leftTaskName);
        Set<String> rightTokens = tokenizeTaskName(rightTaskName);

        if (leftTokens.isEmpty() || rightTokens.isEmpty()) {
            return normalizeTaskName(leftTaskName).equals(normalizeTaskName(rightTaskName));
        }

        int shared = 0;
        for (String token : leftTokens) {
            if (rightTokens.contains(token)) {
                shared++;
            }
        }
        if (shared < 1) {
            return false;
        }

        int union = leftTokens.size() + rightTokens.size() - shared;
        double jaccard = union == 0 ? 0.0 : (double) shared / union;

        String leftNormalized = normalizeTaskName(leftTaskName);
        String rightNormalized = normalizeTaskName(rightTaskName);
        double containsBonus = (leftNormalized.contains(rightNormalized) || rightNormalized.contains(leftNormalized))
                ? 0.2
                : 0.0;

        return (jaccard + containsBonus) >= TASK_SIMILARITY_THRESHOLD;
    }

    private Set<String> tokenizeTaskName(String taskName) {
        String normalized = normalizeTaskName(taskName);
        if (normalized.isBlank()) {
            return Set.of();
        }

        Set<String> tokens = new LinkedHashSet<>();
        for (String token : normalized.split("\\s+")) {
            if (token.isBlank() || TASK_NAME_STOPWORDS.contains(token)) {
                continue;
            }
            tokens.add(token);
        }
        return tokens;
    }

    private String normalizeTaskName(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private record DeploymentRecord(
            String staffId,
            Long taskId,
            Long requiredSkillId,
            String projectCode,
            String taskName,
            LocalDate workDate,
            int sourceManpowerTouched,
            boolean sourceTaskCompleted) {

    }

    private boolean isCompleted(ProjectTask task) {
        return "completed".equals(normalizeStatus(task.getTaskStatus()));
    }

    private int getManpowerTouched(ProjectManpower manpower) {
        Integer manpowerTouched = manpower.getManpowerTouched();
        return manpowerTouched == null ? 0 : manpowerTouched;
    }

    private record ManpowerSlotKey(Long projectTaskId, Long projectSkillId, LocalDate workDate) {

    }

    private Optional<LocalDate> resolveEffectiveStartDate(ProjectTask task) {
        if (task == null) {
            return Optional.empty();
        }

        if ("not started".equals(normalizeStatus(task.getTaskStatus()))) {
            return Optional.ofNullable(parseToLocalDate(task.getTaskStartDate()));
        }

        return Optional.ofNullable(parseToLocalDate(task.getActualStartDate()))
                .or(() -> Optional.ofNullable(parseToLocalDate(task.getTaskStartDate())));
    }

    private Optional<LocalDate> resolveEffectiveEndDate(ProjectTask task) {
        if (task == null) {
            return Optional.empty();
        }

        if ("completed".equals(normalizeStatus(task.getTaskStatus()))) {
            return Optional.ofNullable(parseToLocalDate(task.getActualEndDate()));
        }

        return Optional.ofNullable(parseToLocalDate(task.getTaskEndDate()));
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase();
    }

    private WorkdaySettings resolveWorkdaySettings() {
        int workDaysPerWeek = parseParamInt("workDaysPerWeek", 7, 1, 7);
        int firstWorkDay = parseParamInt("firstWorkDay", 1, 1, 7);
        int lastWorkDay = parseParamInt("lastWorkDay", firstWorkDay, 1, 7);
        return new WorkdaySettings(workDaysPerWeek, firstWorkDay, lastWorkDay);
    }

    private int parseParamInt(String key, int defaultValue, int min, int max) {
        Optional<Param> optionalParam = paramRepository.findById(Objects.requireNonNull(key, "key cannot be null"));
        if (optionalParam.isEmpty() || optionalParam.get().getValue_string() == null) {
            return defaultValue;
        }

        try {
            int parsed = Integer.parseInt(optionalParam.get().getValue_string().trim());
            if (parsed < min) {
                return min;
            }
            if (parsed > max) {
                return max;
            }
            return parsed;
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private boolean isWorkingDay(DayOfWeek dayOfWeek, WorkdaySettings settings) {
        int day = dayOfWeek.getValue();
        boolean inRange;
        if (settings.firstWorkDay <= settings.lastWorkDay) {
            inRange = day >= settings.firstWorkDay && day <= settings.lastWorkDay;
        } else {
            inRange = day >= settings.firstWorkDay || day <= settings.lastWorkDay;
        }

        if (!inRange) {
            return false;
        }

        int offsetFromFirst = day - settings.firstWorkDay;
        if (offsetFromFirst < 0) {
            offsetFromFirst += 7;
        }
        return offsetFromFirst < settings.workDaysPerWeek;
    }

    private static final class WorkdaySettings {

        private final int workDaysPerWeek;
        private final int firstWorkDay;
        private final int lastWorkDay;

        private WorkdaySettings(int workDaysPerWeek, int firstWorkDay, int lastWorkDay) {
            this.workDaysPerWeek = workDaysPerWeek;
            this.firstWorkDay = firstWorkDay;
            this.lastWorkDay = lastWorkDay;
        }
    }

    private LocalDate parseToLocalDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
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
                    return null;
                }
            }
        }
    }
}
