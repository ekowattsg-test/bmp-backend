package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.MobileLogin;
import com.hcteol.jwt.backend.services.MobileLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile-logins")
public class MobileLoginController {

    @Autowired
    private MobileLoginService mobileLoginService;

    @PostMapping
    public ResponseEntity<MobileLogin> create(@RequestBody MobileLogin mobileLogin) {
        MobileLogin saved = mobileLoginService.addMobileLogin(mobileLogin);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<MobileLogin>> getAll() {
        return ResponseEntity.ok(mobileLoginService.getAllMobileLogins());
    }

    @GetMapping("/{loginKey}")
    public ResponseEntity<MobileLogin> getByKey(@PathVariable String loginKey) {
        return ResponseEntity.ok(mobileLoginService.getMobileLoginByKey(loginKey));
    }

    @GetMapping("/number/{mobileNumber}")
    public ResponseEntity<List<MobileLogin>> getByNumber(@PathVariable String mobileNumber) {
        return ResponseEntity.ok(mobileLoginService.getByMobileNumber(mobileNumber));
    }

    @PutMapping("/{loginKey}")
    public ResponseEntity<MobileLogin> update(@PathVariable String loginKey, @RequestBody MobileLogin mobileLogin) {
        return ResponseEntity.ok(mobileLoginService.updateMobileLogin(loginKey, mobileLogin));
    }

    @DeleteMapping("/{loginKey}")
    public ResponseEntity<Void> delete(@PathVariable String loginKey) {
        mobileLoginService.deleteMobileLogin(loginKey);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
