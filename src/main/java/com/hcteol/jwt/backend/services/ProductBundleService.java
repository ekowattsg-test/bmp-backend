package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProductBundle;
import com.hcteol.jwt.backend.repositories.ProductBundleRepository;

@Service
public class ProductBundleService {

    @Autowired
    private ProductBundleRepository productBundleRepository;

    public List<ProductBundle> getAll() {
        return productBundleRepository.findAll();
    }

    public Optional<ProductBundle> getById(Long id) {
        return productBundleRepository.findById(id);
    }

    public Optional<ProductBundle> getByBundleCode(String bundleCode) {
        return productBundleRepository.findByBundleCode(bundleCode);
    }

    public ProductBundle create(ProductBundle productBundle) {
        return productBundleRepository.save(productBundle);
    }

    public ProductBundle update(Long id, ProductBundle details) {
        return productBundleRepository.findById(id).map(pb -> {
            pb.setBundleCode(details.getBundleCode());
            pb.setBundleName(details.getBundleName());
            pb.setBundleMembers(details.getBundleMembers());
            return productBundleRepository.save(pb);
        }).orElseThrow(() -> new RuntimeException("ProductBundle not found with id " + id));
    }

    public void delete(Long id) {
        productBundleRepository.deleteById(id);
    }
}
