package com.hcteol.jwt.backend.dtos;

public class ProjectSkillDto {

    private Long projectSkillId;
    private Long projectTaskId;
    private Long skillId;
    private Integer unit;

    public ProjectSkillDto() {
    }

    public ProjectSkillDto(Long projectSkillId, Long projectTaskId, Long skillId, Integer unit) {
        this.projectSkillId = projectSkillId;
        this.projectTaskId = projectTaskId;
        this.skillId = skillId;
        this.unit = unit;
    }

    public Long getProjectSkillId() {
        return projectSkillId;
    }

    public void setProjectSkillId(Long projectSkillId) {
        this.projectSkillId = projectSkillId;
    }

    public Long getProjectTaskId() {
        return projectTaskId;
    }

    public void setProjectTaskId(Long projectTaskId) {
        this.projectTaskId = projectTaskId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }
}
