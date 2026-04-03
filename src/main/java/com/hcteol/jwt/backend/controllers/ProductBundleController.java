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

import com.hcteol.jwt.backend.entities.ProductBundle;
import com.hcteol.jwt.backend.services.ProductBundleService;

@RestController
@RequestMapping("/api/productbundles")
public class ProductBundleController {

    @Autowired
    private ProductBundleService productBundleService;

    @GetMapping
    public List<ProductBundle> getAll() {
        return productBundleService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductBundle> getById(@PathVariable Long id) {
        return productBundleService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{bundleCode}")
    public ResponseEntity<ProductBundle> getByBundleCode(@PathVariable String bundleCode) {
        return productBundleService.getByBundleCode(bundleCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProductBundle create(@RequestBody ProductBundle productBundle) {
        return productBundleService.create(productBundle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductBundle> update(@PathVariable Long id, @RequestBody ProductBundle productBundle) {
        try {
            return ResponseEntity.ok(productBundleService.update(id, productBundle));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productBundleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
