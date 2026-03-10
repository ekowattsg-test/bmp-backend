package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hcteol.jwt.backend.entities.UserRoleView;

public interface UserRoleViewRepository extends JpaRepository<UserRoleView, Long> {

    @Query("SELECT u FROM UserRoleView u WHERE u.user_id = ?1 and u.company_id = ?2")
    List<UserRoleView> findByUserId(Long userId, String companyId);
}