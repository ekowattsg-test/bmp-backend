package com.hcteol.jwt.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.ProductBundle;

public interface ProductBundleRepository extends JpaRepository<ProductBundle, Long> {

    Optional<ProductBundle> findByBundleCode(String bundleCode);
}
