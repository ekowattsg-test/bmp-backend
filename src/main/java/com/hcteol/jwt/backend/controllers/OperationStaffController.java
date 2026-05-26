package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.OperationStaff;
import com.hcteol.jwt.backend.services.OperationStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operationstaffs")
public class OperationStaffController {

    @Autowired
    private OperationStaffService service;

    @PostMapping
    public ResponseEntity<OperationStaff> create(@RequestBody OperationStaff os) {
        OperationStaff created = service.create(os);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<OperationStaff> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationStaff> get(@PathVariable Long id) {
        OperationStaff r = service.findById(id);
        if (r == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(r);
    }

    @GetMapping("/staff/{staffId}")
    public List<OperationStaff> getByStaff(@PathVariable String staffId) {
        return service.findByStaffId(staffId);
    }

    @GetMapping("/role/{roleName}")
    public List<OperationStaff> getByRole(@PathVariable String roleName) {
        return service.findByRoleName(roleName);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationStaff> update(@PathVariable Long id, @RequestBody OperationStaff os) {
        OperationStaff updated = service.update(id, os);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/staff/{staffId}")
    public ResponseEntity<Void> deleteByStaff(@PathVariable String staffId) {
        service.deleteByStaffId(staffId);
        return ResponseEntity.noContent().build();
    }
}
