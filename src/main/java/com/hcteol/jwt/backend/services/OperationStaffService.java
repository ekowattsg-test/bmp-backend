package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.OperationStaff;
import com.hcteol.jwt.backend.repositories.OperationStaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationStaffService {

    @Autowired
    private OperationStaffRepository repository;

    public OperationStaff create(OperationStaff os) {
        return repository.save(os);
    }

    public List<OperationStaff> findAll() {
        return repository.findAll();
    }

    public OperationStaff findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<OperationStaff> findByStaffId(String staffId) {
        return repository.findByStaffId(staffId);
    }

    public List<OperationStaff> findByRoleName(String roleName) {
        return repository.findByRoleName(roleName);
    }

    public OperationStaff update(Long id, OperationStaff os) {
        OperationStaff existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setStaffId(os.getStaffId());
            existing.setRoleName(os.getRoleName());
            return repository.save(existing);
        }
        return null;
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void deleteByStaffId(String staffId) {
        repository.deleteByStaffId(staffId);
    }
}
