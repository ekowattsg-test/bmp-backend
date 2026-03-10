package com.hcteol.jwt.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.repositories.ParamRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/params")
public class ParamController {
    @Autowired
    private ParamRepository paramRepository;

    @GetMapping
    public List<Param> getAllParam(){
        return paramRepository.findAll();
    }
    
    
}