package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.Language;

public interface LanguageRepository extends JpaRepository<Language, String>{

}