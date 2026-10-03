package com.hcteol.jwt.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hcteol.jwt.backend.dtos.ProjectTaskProgressUpdateResponse;
import com.hcteol.jwt.backend.entities.ProjectTaskProgress;
import com.hcteol.jwt.backend.repositories.ProjectTaskProgressRepository;
import com.hcteol.jwt.backend.repositories.ProjectTaskRepository;

@ExtendWith(MockitoExtension.class)
class ProjectTaskProgressServiceTest {

    @Mock
    private ProjectTaskProgressRepository projectTaskProgressRepository;

    @Mock
    private ProjectTaskRepository projectTaskRepository;

    @Mock
    private ProjectTaskDateCalculationService projectTaskDateCalculationService;

    @Mock
    private ProjectTaskRecalculationService projectTaskRecalculationService;

    @InjectMocks
    private ProjectTaskProgressService projectTaskProgressService;

    @Test
    void createProgressPersistsReportedProgress() {
        ProjectTaskProgress details = new ProjectTaskProgress();
        details.setReportedProgress(65);
        when(projectTaskProgressRepository.save(any(ProjectTaskProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectTaskProgress created = projectTaskProgressService.addProjectTaskProgress(details);

        assertEquals(65, created.getReportedProgress());
    }

    @Test
    void updateProgressPersistsReportedProgress() {
        ProjectTaskProgress existing = new ProjectTaskProgress();
        existing.setProjectTaskProgressId(12L);
        ProjectTaskProgress details = new ProjectTaskProgress();
        details.setReportedProgress(80);
        when(projectTaskProgressRepository.findById(12L)).thenReturn(Optional.of(existing));
        when(projectTaskProgressRepository.save(any(ProjectTaskProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectTaskProgressUpdateResponse response = projectTaskProgressService
                .updateProjectTaskProgressWithTaskSnapshot(12L, details);

        assertNotNull(response);
        assertEquals(80, response.getProjectTaskProgress().getReportedProgress());
    }
}
