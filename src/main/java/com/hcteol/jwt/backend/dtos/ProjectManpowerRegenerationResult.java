package com.hcteol.jwt.backend.dtos;

public class ProjectManpowerRegenerationResult {

    private final int deletedCount;
    private final int createdCount;
    private final int assignedCount;
    private final String serviceStartTime;
    private final String serviceEndTime;
    private final long totalTimeTakenMs;

    public ProjectManpowerRegenerationResult(int deletedCount, int createdCount) {
        this(deletedCount, createdCount, 0, null, null, 0L);
    }

    public ProjectManpowerRegenerationResult(int deletedCount, int createdCount, int assignedCount) {
        this(deletedCount, createdCount, assignedCount, null, null, 0L);
    }

    public ProjectManpowerRegenerationResult(
            int deletedCount,
            int createdCount,
            int assignedCount,
            String serviceStartTime,
            String serviceEndTime,
            long totalTimeTakenMs) {
        this.deletedCount = deletedCount;
        this.createdCount = createdCount;
        this.assignedCount = assignedCount;
        this.serviceStartTime = serviceStartTime;
        this.serviceEndTime = serviceEndTime;
        this.totalTimeTakenMs = totalTimeTakenMs;
    }

    public int getDeletedCount() {
        return deletedCount;
    }

    public int getCreatedCount() {
        return createdCount;
    }

    public int getAssignedCount() {
        return assignedCount;
    }

    public String getServiceStartTime() {
        return serviceStartTime;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public long getTotalTimeTakenMs() {
        return totalTimeTakenMs;
    }
}
