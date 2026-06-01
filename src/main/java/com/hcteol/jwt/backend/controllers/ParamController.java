package com.hcteol.jwt.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.Param;
import com.hcteol.jwt.backend.repositories.ParamRepository;

import java.util.List;
import java.util.Objects;
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
        if (key == null || key.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String normalizedKey = key.trim();
        Optional<Param> p = paramRepository.findById(normalizedKey);
        return p.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Param> createParam(@RequestBody Param param) {
        Param request = Objects.requireNonNull(param);
        if (request.getParam_key() == null || request.getParam_key().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String key = request.getParam_key().trim();
        if (paramRepository.existsById(key)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        request.setParam_key(key);
        applyDefaults(request);
        Param saved = paramRepository.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{key}")
    public ResponseEntity<Param> updateParam(@PathVariable String key, @RequestBody Param param) {
        Param request = Objects.requireNonNull(param);
        if (key == null || key.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String normalizedKey = key.trim();
        if (request.getParam_key() != null && !normalizedKey.equals(request.getParam_key().trim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<Param> existing = paramRepository.findById(normalizedKey);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Param entity = existing.get();
        copyNonNullProperties(request, entity, "param_key");
        if (entity.getChangeable() == null) {
            entity.setChangeable(0);
        }
        Param saved = paramRepository.save(entity);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteParam(@PathVariable String key) {
        if (key == null || key.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String normalizedKey = key.trim();
        if (!paramRepository.existsById(normalizedKey)) {
            return ResponseEntity.notFound().build();
        }
        paramRepository.deleteById(normalizedKey);
        return ResponseEntity.noContent().build();
    }

    private void applyDefaults(Param param) {
        if (param.getChangeable() == null) {
            param.setChangeable(0);
        }
    }

    private void copyNonNullProperties(Param source, Param target, String... ignoreProperties) {
        BeanWrapper sourceWrapper = new BeanWrapperImpl(source);
        java.util.Set<String> ignored = new java.util.HashSet<>();
        if (ignoreProperties != null) {
            java.util.Collections.addAll(ignored, ignoreProperties);
        }

        String[] nullPropertyNames = java.util.Arrays.stream(sourceWrapper.getPropertyDescriptors())
                .map(java.beans.PropertyDescriptor::getName)
                .filter(name -> sourceWrapper.getPropertyValue(name) == null)
                .filter(name -> !ignored.contains(name))
                .toArray(String[]::new);

        BeanUtils.copyProperties(source, target, nullPropertyNames);
    }

}
