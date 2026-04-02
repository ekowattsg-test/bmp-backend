package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.ProductHierarchy;
import com.hcteol.jwt.backend.services.ProductHierarchyService;

@RestController
@RequestMapping("/api/producthierarchies")
public class ProductHierarchyController {

    @Autowired
    private ProductHierarchyService productHierarchyService;

    @GetMapping
    public List<ProductHierarchy> getAll() {
        return productHierarchyService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductHierarchy> getById(@PathVariable Long id) {
        return productHierarchyService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/parent/{parentProductId}")
    public List<ProductHierarchy> getByParentProductId(@PathVariable Long parentProductId) {
        return productHierarchyService.getByParentProductId(parentProductId);
    }

    @GetMapping("/child/{childProductId}")
    public List<ProductHierarchy> getByChildProductId(@PathVariable Long childProductId) {
        return productHierarchyService.getByChildProductId(childProductId);
    }

    @PostMapping
    public ProductHierarchy create(@RequestBody ProductHierarchy productHierarchy) {
        return productHierarchyService.create(productHierarchy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductHierarchy> update(@PathVariable Long id, @RequestBody ProductHierarchy productHierarchy) {
        try {
            return ResponseEntity.ok(productHierarchyService.update(id, productHierarchy));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productHierarchyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
