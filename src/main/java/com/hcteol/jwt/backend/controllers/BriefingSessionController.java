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

import com.hcteol.jwt.backend.entities.BriefingSession;
import com.hcteol.jwt.backend.services.BriefingSessionService;

@RestController
@RequestMapping("/api/briefingsessions")
public class BriefingSessionController {

    @Autowired
    private BriefingSessionService briefingSessionService;

    @GetMapping
    public List<BriefingSession> getAllBriefingSessions() {
        return briefingSessionService.getAllBriefingSessions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BriefingSession> getBriefingSessionById(@PathVariable Long id) {
        return briefingSessionService.getBriefingSessionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BriefingSession> createBriefingSession(@RequestBody BriefingSession briefingSession) {
        BriefingSession created = briefingSessionService.addBriefingSession(briefingSession);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BriefingSession> updateBriefingSession(@PathVariable Long id,
            @RequestBody BriefingSession details) {
        BriefingSession updated = briefingSessionService.updateBriefingSession(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBriefingSession(@PathVariable Long id) {
        briefingSessionService.deleteBriefingSession(id);
        return ResponseEntity.noContent().build();
    }
}
