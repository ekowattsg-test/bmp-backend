package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.OperationRole;
import com.hcteol.jwt.backend.repositories.OperationRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationRoleService {

    @Autowired
    private OperationRoleRepository repository;

    public OperationRole create(OperationRole role) {
        return repository.save(role);
    }

    public List<OperationRole> findAll() {
        return repository.findAll();
    }

    public OperationRole findById(String id) {
        return repository.findById(id).orElse(null);
    }

    public OperationRole update(String id, OperationRole role) {
        OperationRole existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setRoleDescription(role.getRoleDescription());
            return repository.save(existing);
        }
        return null;
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
