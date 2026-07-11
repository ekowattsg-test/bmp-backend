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

import com.hcteol.jwt.backend.entities.BriefingMember;
import com.hcteol.jwt.backend.services.BriefingMemberService;

@RestController
@RequestMapping("/api/briefingmembers")
public class BriefingMemberController {

    @Autowired
    private BriefingMemberService briefingMemberService;

    @GetMapping
    public List<BriefingMember> getAllBriefingMembers() {
        return briefingMemberService.getAllBriefingMembers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BriefingMember> getBriefingMemberById(@PathVariable Long id) {
        return briefingMemberService.getBriefingMemberById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BriefingMember> createBriefingMember(@RequestBody BriefingMember briefingMember) {
        BriefingMember created = briefingMemberService.addBriefingMember(briefingMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BriefingMember> updateBriefingMember(@PathVariable Long id,
            @RequestBody BriefingMember details) {
        BriefingMember updated = briefingMemberService.updateBriefingMember(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBriefingMember(@PathVariable Long id) {
        briefingMemberService.deleteBriefingMember(id);
        return ResponseEntity.noContent().build();
    }
}
