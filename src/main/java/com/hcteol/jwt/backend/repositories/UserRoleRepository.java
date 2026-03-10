package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	UserRole findByUserIdAndRoleId(Long userId, Long roleId);
}