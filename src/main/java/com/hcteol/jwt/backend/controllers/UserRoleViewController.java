package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.dtos.UserDto;
import com.hcteol.jwt.backend.entities.UserRoleView;
import com.hcteol.jwt.backend.repositories.UserRoleViewRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/userroleviews")
public class UserRoleViewController {
    @Autowired
    
    private UserRoleViewRepository userRoleViewRepository;

    @GetMapping("/{id}/{comp}")
    public List<UserRoleView> getUserRoleView(@PathVariable Long id, @PathVariable String comp) {
        return userRoleViewRepository.findByUserId(id, comp);
    }

    // New endpoint: get all userroleviews
    @GetMapping()
    public List<UserRoleView> getAllUserRoleViews() {
        return userRoleViewRepository.findAll();
    }
}