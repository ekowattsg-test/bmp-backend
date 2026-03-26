package com.hcteol.jwt.backend.repositories;

import java.util.List;

import com.hcteol.jwt.backend.entities.UserRoleView;

public interface UserRoleViewRepositoryCustom {
    List<UserRoleView> findByUserId(Long userId, String companyId);
}
