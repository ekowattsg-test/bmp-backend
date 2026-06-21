package com.hcteol.jwt.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.BundleMember;
import com.hcteol.jwt.backend.entities.ProductBundle;
import com.hcteol.jwt.backend.repositories.ProductBundleRepository;

@Service
public class ProductBundleService {

    @Autowired
    private ProductBundleRepository productBundleRepository;

    @Autowired
    private DocumentSeqService documentSeqService;

    public List<ProductBundle> getAll() {
        return productBundleRepository.findAll();
    }

    public Optional<ProductBundle> getById(Long id) {
        return productBundleRepository.findById(id);
    }

    public Optional<ProductBundle> getByBundleCode(String bundleCode) {
        return productBundleRepository.findByBundleCode(bundleCode);
    }

    @Transactional
    public ProductBundle create(ProductBundle productBundle) {
        if (productBundle.getBundleCode() == null || productBundle.getBundleCode().trim().isEmpty()) {
            String token = UUID.randomUUID().toString();
            Long seq = documentSeqService.getNextSeq("BD", token);
            productBundle.setBundleCode("BD-" + seq);
        }
        return productBundleRepository.save(productBundle);
    }

    public ProductBundle update(Long id, ProductBundle details) {
        return productBundleRepository.findById(id).map(pb -> {
            pb.setBundleCode(details.getBundleCode());
            pb.setBundleName(details.getBundleName());
            if (pb.getBundleMembers() == null) {
                pb.setBundleMembers(new ArrayList<>());
            }
            pb.getBundleMembers().clear();
            if (details.getBundleMembers() != null) {
                pb.getBundleMembers().addAll(details.getBundleMembers());
            }
            return productBundleRepository.save(pb);
        }).orElseThrow(() -> new RuntimeException("ProductBundle not found with id " + id));
    }

    public void delete(Long id) {
        productBundleRepository.deleteById(id);
    }

    @Transactional
    public ProductBundle addMember(Long bundleId, BundleMember member) {
        return productBundleRepository.findById(bundleId).map(pb -> {
            if (pb.getBundleMembers() == null) {
                pb.setBundleMembers(new ArrayList<>());
            }
            pb.getBundleMembers().removeIf(m -> m.getProductId() != null && m.getProductId().equals(member.getProductId()));
            pb.getBundleMembers().add(member);
            return productBundleRepository.save(pb);
        }).orElseThrow(() -> new RuntimeException("ProductBundle not found with id " + bundleId));
    }

    @Transactional
    public ProductBundle removeMember(Long bundleId, Long productId) {
        return productBundleRepository.findById(bundleId).map(pb -> {
            if (pb.getBundleMembers() != null) {
                pb.getBundleMembers().removeIf(m -> m.getProductId() != null && m.getProductId().equals(productId));
            }
            return productBundleRepository.save(pb);
        }).orElseThrow(() -> new RuntimeException("ProductBundle not found with id " + bundleId));
    }
}
