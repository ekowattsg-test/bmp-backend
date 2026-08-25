package com.hcteol.jwt.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.ProjectStream;

@Repository
public interface ProjectStreamRepository extends JpaRepository<ProjectStream, Long> {

    List<ProjectStream> findByProjectCode(String projectCode);

    List<ProjectStream> findByStreamType(String streamType);

    List<ProjectStream> findByProjectCodeAndStreamType(String projectCode, String streamType);

    List<ProjectStream> findByProjectCodeAndStreamNumber(String projectCode, Long streamNumber);

    List<ProjectStream> findByProjectCodeAndParentStreamNumber(String projectCode, Long parentStreamNumber);

    @Query("SELECT MAX(p.streamNumber) FROM ProjectStream p WHERE p.projectCode = :projectCode")
    Optional<Long> findMaxStreamNumberByProjectCode(@Param("projectCode") String projectCode);
}
