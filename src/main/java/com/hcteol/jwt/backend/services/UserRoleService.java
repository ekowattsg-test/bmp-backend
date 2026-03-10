package com.hcteol.jwt.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.hcteol.jwt.backend.entities.UserRole;
import com.hcteol.jwt.backend.repositories.UserRoleRepository;

@Service
public class UserRoleService {
    
    @Autowired
    private UserRoleRepository userRoleRepository;

    public UserRole addUserRole(@RequestBody UserRole userRole) {
        // Check for duplicate userId and roleId
        UserRole existing = userRoleRepository.findByUserIdAndRoleId(userRole.getUserId(), userRole.getRoleId());
        if (existing != null) {
            throw new RuntimeException("Duplicate userId and roleId pair");
        }
        return userRoleRepository.save(userRole);
    }

    public List<UserRole> getAllUserRoles() {
        return userRoleRepository.findAll();
    }

    public UserRole updateUserRole(Long id, UserRole userRoleDetails) {
        UserRole existingUserRole = userRoleRepository.findById(id).orElse(null);
        if (existingUserRole != null) {
            // Check for duplicate userId and roleId (excluding current record)
            UserRole duplicate = userRoleRepository.findByUserIdAndRoleId(userRoleDetails.getUserId(), userRoleDetails.getRoleId());
            if (duplicate != null && !duplicate.getId().equals(id)) {
                throw new RuntimeException("Duplicate userId and roleId pair");
            }
            existingUserRole.setUserId(userRoleDetails.getUserId());
            existingUserRole.setRoleId(userRoleDetails.getRoleId());
            return userRoleRepository.save(existingUserRole);
        }
        return null;
    }

    public void deleteUserRole(Long id) {
        userRoleRepository.deleteById(id);
    }

}