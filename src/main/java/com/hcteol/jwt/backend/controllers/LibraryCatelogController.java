package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.LibraryCatelog;
import com.hcteol.jwt.backend.services.LibraryCatelogService;

@RestController
@RequestMapping("/api/librarycatelogs")
public class LibraryCatelogController {

    @Autowired
    private LibraryCatelogService libraryCatelogService;

    @GetMapping
    public List<LibraryCatelog> getAllLibraryCatelogs() {
        return libraryCatelogService.getAllLibraryCatelogs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibraryCatelog> getLibraryCatelogById(@PathVariable Long id) {
        return libraryCatelogService.getLibraryCatelogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectCode}")
    public List<LibraryCatelog> getLibraryCatelogsByProjectCode(@PathVariable String projectCode) {
        return libraryCatelogService.getLibraryCatelogsByProjectCode(projectCode);
    }

    @PostMapping
    public ResponseEntity<LibraryCatelog> createLibraryCatelog(@RequestBody LibraryCatelog libraryCatelog) {
        LibraryCatelog created = libraryCatelogService.addLibraryCatelog(libraryCatelog);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibraryCatelog> updateLibraryCatelog(@PathVariable Long id,
            @RequestBody LibraryCatelog details) {
        LibraryCatelog updated = libraryCatelogService.updateLibraryCatelog(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibraryCatelog(@PathVariable Long id) {
        libraryCatelogService.deleteLibraryCatelog(id);
        return ResponseEntity.noContent().build();
    }
}
