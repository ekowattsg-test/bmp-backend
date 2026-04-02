package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcteol.jwt.backend.entities.ProductHierarchy;
import com.hcteol.jwt.backend.repositories.ProductHierarchyRepository;

@Service
public class ProductHierarchyService {

    @Autowired
    private ProductHierarchyRepository productHierarchyRepository;

    public List<ProductHierarchy> getAll() {
        return productHierarchyRepository.findAll();
    }

    public Optional<ProductHierarchy> getById(Long id) {
        return productHierarchyRepository.findById(id);
    }

    public List<ProductHierarchy> getByParentProductId(Long parentProductId) {
        return productHierarchyRepository.findByParentProductId(parentProductId);
    }

    public List<ProductHierarchy> getByChildProductId(Long childProductId) {
        return productHierarchyRepository.findByChildProductId(childProductId);
    }

    public ProductHierarchy create(ProductHierarchy productHierarchy) {
        return productHierarchyRepository.save(productHierarchy);
    }

    public ProductHierarchy update(Long id, ProductHierarchy details) {
        return productHierarchyRepository.findById(id).map(ph -> {
            ph.setParentProductId(details.getParentProductId());
            ph.setChildProductId(details.getChildProductId());
            ph.setNumberOfChildren(details.getNumberOfChildren());
            return productHierarchyRepository.save(ph);
        }).orElseThrow(() -> new RuntimeException("ProductHierarchy not found with id " + id));
    }

    public void delete(Long id) {
        productHierarchyRepository.deleteById(id);
    }
}
