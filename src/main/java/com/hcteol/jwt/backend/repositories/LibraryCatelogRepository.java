package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.LibraryCatelog;

@Repository
public interface LibraryCatelogRepository extends JpaRepository<LibraryCatelog, Long> {

    List<LibraryCatelog> findByProjectCode(String projectCode);
}
