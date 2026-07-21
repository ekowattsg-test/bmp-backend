package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.Vendor;
import com.hcteol.jwt.backend.repositories.VendorRepository;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public Vendor getVendorById(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + vendorId));
    }

    public List<Vendor> searchVendorsByName(String vendorName) {
        return vendorRepository.findByVendorNameContainingIgnoreCase(vendorName);
    }

    public Vendor createVendor(Vendor vendor) {
        return vendorRepository.save(Objects.requireNonNull(vendor, "vendor cannot be null"));
    }

    public Vendor updateVendor(Long vendorId, Vendor details) {
        Vendor existing = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + vendorId));

        BeanUtils.copyProperties(Objects.requireNonNull(details, "details cannot be null"), existing, "vendorId");
        return vendorRepository.save(existing);
    }

    public void deleteVendor(Long vendorId) {
        vendorRepository.deleteById(vendorId);
    }
}
