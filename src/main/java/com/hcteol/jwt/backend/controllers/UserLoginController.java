package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.UserLogin;
import com.hcteol.jwt.backend.services.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/userlogins")
public class UserLoginController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping
    public ResponseEntity<UserLogin> createUserLogin(@RequestBody UserLogin userLogin) {
        UserLogin saved = userLoginService.addUserLogin(userLogin);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<UserLogin>> getAllUserLogins() {
        List<UserLogin> list = userLoginService.getAllUserLogins();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserLogin>> getUserLoginsByUserId(@PathVariable Long userId) {
        List<UserLogin> list = userLoginService.getUserLoginsByUserId(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/range")
    public ResponseEntity<List<UserLogin>> getUserLoginsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<UserLogin> list = userLoginService.getUserLoginsByDateRange(start, end);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserLogin> getUserLoginById(@PathVariable Long id) {
        UserLogin userLogin = userLoginService.getUserLoginById(id);
        return ResponseEntity.ok(userLogin);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserLogin> updateUserLogin(@PathVariable Long id, @RequestBody UserLogin userLogin) {
        UserLogin updated = userLoginService.updateUserLogin(id, userLogin);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserLogin(@PathVariable Long id) {
        userLoginService.deleteUserLogin(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
