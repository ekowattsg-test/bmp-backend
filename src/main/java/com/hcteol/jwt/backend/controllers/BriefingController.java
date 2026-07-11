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

import com.hcteol.jwt.backend.entities.Briefing;
import com.hcteol.jwt.backend.services.BriefingService;

@RestController
@RequestMapping("/api/briefings")
public class BriefingController {

    @Autowired
    private BriefingService briefingService;

    @GetMapping
    public List<Briefing> getAllBriefings() {
        return briefingService.getAllBriefings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Briefing> getBriefingById(@PathVariable Long id) {
        return briefingService.getBriefingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Briefing> createBriefing(@RequestBody Briefing briefingData) {
        Briefing created = briefingService.addBriefing(briefingData);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Briefing> updateBriefing(@PathVariable Long id, @RequestBody Briefing details) {
        Briefing updated = briefingService.updateBriefing(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBriefing(@PathVariable Long id) {
        briefingService.deleteBriefing(id);
        return ResponseEntity.noContent().build();
    }
}
