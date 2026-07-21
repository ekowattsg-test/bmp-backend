package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.Vendor;
import com.hcteol.jwt.backend.services.VendorService;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @GetMapping
    public ResponseEntity<List<Vendor>> getVendors() {
        List<Vendor> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long vendorId) {
        try {
            Vendor vendor = vendorService.getVendorById(vendorId);
            return ResponseEntity.ok(vendor);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vendor>> searchVendorsByName(@RequestParam("name") String name) {
        List<Vendor> vendors = vendorService.searchVendorsByName(name);
        return ResponseEntity.ok(vendors);
    }

    @PostMapping
    public ResponseEntity<Vendor> createVendor(@RequestBody Vendor vendor) {
        Vendor created = vendorService.createVendor(vendor);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{vendorId}")
    public ResponseEntity<Vendor> updateVendor(@PathVariable Long vendorId, @RequestBody Vendor details) {
        try {
            Vendor updated = vendorService.updateVendor(vendorId, details);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{vendorId}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long vendorId) {
        vendorService.deleteVendor(vendorId);
        return ResponseEntity.noContent().build();
    }
}
