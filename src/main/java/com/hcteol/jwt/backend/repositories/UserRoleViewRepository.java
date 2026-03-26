package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.UserRoleView;

public interface UserRoleViewRepository extends JpaRepository<UserRoleView, Long>, UserRoleViewRepositoryCustom {

    List<UserRoleView> findByUserId(Long userId, String companyId);
}