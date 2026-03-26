package com.hcteol.jwt.backend.repositories;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.UserRoleView;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class UserRoleViewRepositoryImpl implements UserRoleViewRepositoryCustom {

    private static final String FIND_BY_USER_ID_SQL_PATH = "sql/user-role-view-find-by-user-id.sql";

    @PersistenceContext
    private EntityManager entityManager;

    private String findByUserIdSql;

    @PostConstruct
    public void init() {
        this.findByUserIdSql = loadSql(FIND_BY_USER_ID_SQL_PATH);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserRoleView> findByUserId(Long userId, String companyId) {
        return entityManager.createNativeQuery(findByUserIdSql, UserRoleView.class)
                .setParameter("userId", userId)
                .setParameter("companyId", companyId)
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
