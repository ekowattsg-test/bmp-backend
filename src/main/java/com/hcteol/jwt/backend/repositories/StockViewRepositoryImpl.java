package com.hcteol.jwt.backend.repositories;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.StockView;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class StockViewRepositoryImpl implements StockViewRepositoryCustom {

    private static final String FIND_BY_STOCK_ID_SQL_PATH = "sql/stock-view-find-by-stock-id.sql";
    private static final String FIND_BY_PRODUCT_ID_SQL_PATH = "sql/stock-view-find-by-product-id.sql";
    private static final String FIND_BY_STOCK_CODE_SQL_PATH = "sql/stock-view-find-by-stock-code.sql";

    @PersistenceContext
    private EntityManager entityManager;

    private String findByStockIdSql;
    private String findByProductIdSql;
    private String findByStockCodeSql;

    @PostConstruct
    public void init() {
        this.findByStockIdSql = loadSql(FIND_BY_STOCK_ID_SQL_PATH);
        this.findByProductIdSql = loadSql(FIND_BY_PRODUCT_ID_SQL_PATH);
        this.findByStockCodeSql = loadSql(FIND_BY_STOCK_CODE_SQL_PATH);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<StockView> findByStockId(Long stockId) {
        return entityManager.createNativeQuery(findByStockIdSql, StockView.class)
                .setParameter("stockId", stockId)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<StockView> findByProductId(Long productId) {
        return entityManager.createNativeQuery(findByProductIdSql, StockView.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<StockView> findByStockCode(String stockCode) {
        return entityManager.createNativeQuery(findByStockCodeSql, StockView.class)
                .setParameter("stockCode", stockCode)
                .getResultList();
    }

    private String loadSql(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load SQL file from classpath: " + path, ex);
        }
    }
}
