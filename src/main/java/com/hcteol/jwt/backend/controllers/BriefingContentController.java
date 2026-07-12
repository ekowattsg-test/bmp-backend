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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.BriefingContent;
import com.hcteol.jwt.backend.services.BriefingContentService;

@RestController
@RequestMapping("/api/briefingcontents")
public class BriefingContentController {

    @Autowired
    private BriefingContentService briefingContentService;

    @GetMapping
    public List<BriefingContent> getBriefingContents(@RequestParam(required = false) Long briefingId) {
        if (briefingId != null) {
            return briefingContentService.getByBriefingId(briefingId);
        }
        return briefingContentService.getAllBriefingContents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BriefingContent> getBriefingContentById(@PathVariable Long id) {
        return briefingContentService.getBriefingContentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/briefing/{briefingId}/seq/{seqNumber}")
    public ResponseEntity<BriefingContent> getBriefingContentByBriefingIdAndSeqNumber(
            @PathVariable Long briefingId,
            @PathVariable String seqNumber) {
        return briefingContentService.getByBriefingIdAndSequenceNumber(briefingId, seqNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BriefingContent> createBriefingContent(@RequestBody BriefingContent content) {
        BriefingContent created = briefingContentService.addBriefingContent(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BriefingContent> updateBriefingContent(@PathVariable Long id, @RequestBody BriefingContent details) {
        BriefingContent updated = briefingContentService.updateBriefingContent(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBriefingContent(@PathVariable Long id) {
        briefingContentService.deleteBriefingContent(id);
        return ResponseEntity.noContent().build();
    }
}
