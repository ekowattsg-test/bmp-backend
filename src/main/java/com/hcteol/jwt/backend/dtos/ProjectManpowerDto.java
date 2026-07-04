package com.hcteol.jwt.backend.dtos;

public class ProjectManpowerDto {

    private Long projectManpowerId;
    private Long projectTaskId;
    private Long projectSkillId;
    private String workDate;
    private String staffId;
    private Double loading;
    private Integer manpowerTouched;

    public ProjectManpowerDto() {
    }

    public ProjectManpowerDto(
            Long projectManpowerId,
            Long projectTaskId,
            Long projectSkillId,
            String workDate,
            String staffId,
            Double loading,
            Integer manpowerTouched) {
        this.projectManpowerId = projectManpowerId;
        this.projectTaskId = projectTaskId;
        this.projectSkillId = projectSkillId;
        this.workDate = workDate;
        this.staffId = staffId;
        this.loading = loading;
        this.manpowerTouched = manpowerTouched;
    }

    public Long getProjectManpowerId() {
        return projectManpowerId;
    }

    public void setProjectManpowerId(Long projectManpowerId) {
        this.projectManpowerId = projectManpowerId;
    }

    public Long getProjectTaskId() {
        return projectTaskId;
    }

    public void setProjectTaskId(Long projectTaskId) {
        this.projectTaskId = projectTaskId;
    }

    public Long getProjectSkillId() {
        return projectSkillId;
    }

    public void setProjectSkillId(Long projectSkillId) {
        this.projectSkillId = projectSkillId;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public Double getLoading() {
        return loading;
    }

    public void setLoading(Double loading) {
        this.loading = loading;
    }

    public Integer getManpowerTouched() {
        return manpowerTouched;
    }

    public void setManpowerTouched(Integer manpowerTouched) {
        this.manpowerTouched = manpowerTouched;
    }
}
