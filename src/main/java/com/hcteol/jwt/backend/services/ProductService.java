package com.hcteol.jwt.backend.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.Product;
import com.hcteol.jwt.backend.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DocumentSeqService documentSeqService;

    public List<Product> getProductsByCategoryAndClass(String productCategory, String productClass) {
        return productRepository.findByProductCategoryAndProductClass(productCategory, productClass);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) {
        if (product.getProductCode() == null || product.getProductCode().trim().isEmpty()) {
            String token = UUID.randomUUID().toString();
            Long seq = documentSeqService.getNextSeq("PD", token);
            product.setProductCode("PD-" + seq);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setProductName(productDetails.getProductName());
            product.setProductDescription(productDetails.getProductDescription());
            product.setUom(productDetails.getUom());
            product.setProductCategory(productDetails.getProductCategory());
            product.setProductClass(productDetails.getProductClass());
            product.setProductCode(productDetails.getProductCode());
            product.setProductBrand(productDetails.getProductBrand());
            product.setCommonName(productDetails.getCommonName());
            product.setSpecification(productDetails.getSpecification());
            product.setProductPicture(productDetails.getProductPicture());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id " + id);
        }
        productRepository.deleteById(id);
    }
}
