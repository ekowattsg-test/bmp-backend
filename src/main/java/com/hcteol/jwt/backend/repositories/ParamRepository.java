package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.Param;

public interface ParamRepository extends JpaRepository<Param, String>{
    
}