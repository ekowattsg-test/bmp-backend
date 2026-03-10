package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.Language;
import com.hcteol.jwt.backend.repositories.LanguageRepository;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {
    @Autowired
    private LanguageRepository languageRepository;

    @GetMapping()
    public List<Language> getAllLanguage() {
        return languageRepository.findAll();
}
    
}