package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.OperationRole;
import com.hcteol.jwt.backend.services.OperationRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operationroles")
public class OperationRoleController {

    @Autowired
    private OperationRoleService service;

    @PostMapping
    public OperationRole create(@RequestBody OperationRole role) {
        return service.create(role);
    }

    @GetMapping
    public List<OperationRole> list() {
        return service.findAll();
    }

    @GetMapping("/{roleName}")
    public OperationRole get(@PathVariable String roleName) {
        return service.findById(roleName);
    }

    @PutMapping("/{roleName}")
    public OperationRole update(@PathVariable String roleName, @RequestBody OperationRole role) {
        return service.update(roleName, role);
    }

    @DeleteMapping("/{roleName}")
    public void delete(@PathVariable String roleName) {
        service.delete(roleName);
    }
}
