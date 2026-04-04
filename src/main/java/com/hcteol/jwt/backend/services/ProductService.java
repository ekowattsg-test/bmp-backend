package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.Product;
import com.hcteol.jwt.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductsByCategoryAndClass(String productCategory, String productClass) {
        return productRepository.findByProductCategoryAndProductClass(productCategory, productClass);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
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
            product.setProductPicture(productDetails.getProductPicture());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
