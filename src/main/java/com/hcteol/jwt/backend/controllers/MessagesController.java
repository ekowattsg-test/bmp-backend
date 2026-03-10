package com.hcteol.jwt.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hcteol.jwt.backend.dtos.UserDto;

import java.util.Arrays;
import java.util.List;

@RestController
public class MessagesController {

    @GetMapping("/messages")
    public ResponseEntity<List<String>> messages(@AuthenticationPrincipal UserDto user) {
        return ResponseEntity.ok(Arrays.asList("first", "second"));
    }

}
