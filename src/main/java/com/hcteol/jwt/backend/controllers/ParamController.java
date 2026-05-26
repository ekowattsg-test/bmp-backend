package com.hcteol.jwt.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.repositories.ParamRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/params")
public class ParamController {

    @Autowired
    private ParamRepository paramRepository;

    @GetMapping
    public List<Param> getAllParam() {
        return paramRepository.findAll();
    }

    @GetMapping("/{key}")
    public ResponseEntity<Param> getByKey(@PathVariable String key) {
        Optional<Param> p = paramRepository.findById(key);
        return p.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Param> createParam(@RequestBody Param param) {
        if (param.getParam_key() == null || param.getParam_key().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (paramRepository.existsById(param.getParam_key())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Param saved = paramRepository.save(param);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{key}")
    public ResponseEntity<Param> updateParam(@PathVariable String key, @RequestBody Param param) {
        Optional<Param> existing = paramRepository.findById(key);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Param e = existing.get();
        // Param now stores a single string value; update that
        e.setValue_string(param.getValue_string());
        Param saved = paramRepository.save(e);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteParam(@PathVariable String key) {
        if (!paramRepository.existsById(key)) {
            return ResponseEntity.notFound().build();
        }
        paramRepository.deleteById(key);
        return ResponseEntity.noContent().build();
    }

}
