package com.hcteol.jwt.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.dtos.TvSessionApproveRequest;
import com.hcteol.jwt.backend.dtos.TvSessionApproveResponse;
import com.hcteol.jwt.backend.dtos.TvSessionCreateRequest;
import com.hcteol.jwt.backend.dtos.TvSessionCreateResponse;
import com.hcteol.jwt.backend.dtos.TvSessionExchangeRequest;
import com.hcteol.jwt.backend.dtos.TvSessionExchangeResponse;
import com.hcteol.jwt.backend.dtos.TvSessionStatusResponse;
import com.hcteol.jwt.backend.dtos.UserDto;
import com.hcteol.jwt.backend.services.TvAuthService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tv-auth")
public class TvAuthController {

    private final TvAuthService tvAuthService;

    @PostMapping("/session")
    public ResponseEntity<TvSessionCreateResponse> createSession(@RequestBody(required = false) TvSessionCreateRequest request) {
        return ResponseEntity.ok(tvAuthService.createSession(request));
    }

    @GetMapping("/session/{sessionCode}/status")
    public ResponseEntity<TvSessionStatusResponse> getSessionStatus(@PathVariable String sessionCode) {
        return ResponseEntity.ok(tvAuthService.getSessionStatus(sessionCode));
    }

    @PostMapping("/approve")
    public ResponseEntity<TvSessionApproveResponse> approveSession(@RequestBody TvSessionApproveRequest request,
            Authentication authentication) {
        UserDto approver = authentication == null ? null : (UserDto) authentication.getPrincipal();
        return ResponseEntity.ok(tvAuthService.approveSession(request, approver));
    }

    @PostMapping("/exchange")
    public ResponseEntity<TvSessionExchangeResponse> exchangeToken(@RequestBody TvSessionExchangeRequest request) {
        return ResponseEntity.ok(tvAuthService.exchangeToken(request));
    }
}
