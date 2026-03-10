package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.UserRole;
import com.hcteol.jwt.backend.services.UserRoleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;



@RestController
@RequestMapping("/api/userroles")
public class UserRoleController {
    
    @Autowired
    private UserRoleService userRoleService;

    @PostMapping
    public UserRole createUserRole(@RequestBody UserRole userRole) {
        return userRoleService.addUserRole(userRole);
    }
    
    @GetMapping()
    public List<UserRole> getAllUserRoles() {
        return userRoleService.getAllUserRoles();
    }

    @PutMapping("/{id}")
    public UserRole updateUserRole(@PathVariable Long id, @RequestBody UserRole userRoleDetails) {
        return userRoleService.updateUserRole(id, userRoleDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteUserRole(@PathVariable Long id) {
        userRoleService.deleteUserRole(id);
    }

}