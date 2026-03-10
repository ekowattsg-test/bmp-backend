package com.hcteol.jwt.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.Role;
import com.hcteol.jwt.backend.repositories.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    public Role updateRole(Long roleId, Role roleDetails) {
        Role existingRole = roleRepository.findById(roleId).orElse(null);
        if (existingRole != null) {
            existingRole.setRole(roleDetails.getRole());
            existingRole.setDescription(roleDetails.getDescription());
            existingRole.setLevel(roleDetails.getLevel());
            existingRole.setMenu(roleDetails.getMenu());
            return roleRepository.save(existingRole);
        }
        return null;
    }

    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }
}