package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.ProductHierarchy;

public interface ProductHierarchyRepository extends JpaRepository<ProductHierarchy, Long> {

    List<ProductHierarchy> findByParentProductId(Long parentProductId);

    List<ProductHierarchy> findByChildProductId(Long childProductId);
}
