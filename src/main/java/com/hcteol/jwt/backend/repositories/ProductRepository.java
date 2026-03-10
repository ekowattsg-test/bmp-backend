package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	// AND condition for productCategory (type) and productClass
	java.util.List<Product> findByProductCategoryAndProductClass(String productCategory, String productClass);
}
