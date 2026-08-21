package com.hcteol.jwt.backend.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.services.AutoHoldMovementService;

@RestController
@RequestMapping("/api/admin/holdmovements")
public class AutoHoldMovementController {

    @Autowired
    private AutoHoldMovementService autoHoldMovementService;

    @PostMapping("/rebuild")
    public ResponseEntity<Map<String, Object>> rebuildAllHolds() {
        Map<String, Object> result = autoHoldMovementService.rebuildAllHolds();
        if (Boolean.FALSE.equals(result.get("enabled"))) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.ok(result);
    }
}
