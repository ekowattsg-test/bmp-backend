package com.hcteol.jwt.backend.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectLeader;

@Repository
public interface ProjectStaffRepository extends JpaRepository<ProjectLeader, Long> {

    @Query(value = """
            SELECT DISTINCT staff_id FROM (
                SELECT project_leader_staff_id AS staff_id FROM project_leader
                WHERE project_code = :projectCode AND active = 1
              UNION
                SELECT pt.staff_id FROM project_task pt
                JOIN project_stream ps ON pt.project_stream_id = ps.project_stream_id
                WHERE ps.project_code = :projectCode
              UNION
                SELECT pm.staff_id FROM project_manpower pm
                JOIN project_skill psk ON pm.project_skill_id = psk.project_skill_id
                JOIN project_task pt ON psk.project_task_id = pt.project_task_id
                JOIN project_stream ps ON pt.project_stream_id = ps.project_stream_id
                WHERE ps.project_code = :projectCode
            ) AS project_staff
            WHERE staff_id IS NOT NULL AND staff_id <> ''
            """, nativeQuery = true)
    Set<String> findAllStaffIdsByProjectCode(@Param("projectCode") String projectCode);

    @Query(value = """
            SELECT DISTINCT staff_id FROM (
                SELECT project_leader_staff_id AS staff_id FROM project_leader
                WHERE project_code = :projectCode AND active = 1
            ) AS project_leaders
            WHERE staff_id IS NOT NULL AND staff_id <> ''
            """, nativeQuery = true)
    Set<String> findLeaderStaffIdsByProjectCode(@Param("projectCode") String projectCode);
}
