package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.dtos.SecurePasswordChangeDto;
import com.hcteol.jwt.backend.services.AdminPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/password")
public class AdminPasswordController {
    
    @Autowired
    private AdminPasswordService adminPasswordService;
    
    /**
     * Securely change another user's password
     * Validates requesting user credentials before allowing the password change
     * 
     * POST /api/admin/password/force-change
     */
    @PostMapping("/force-change")
    public ResponseEntity<Map<String, String>> secureForceChangePassword(
            @RequestBody SecurePasswordChangeDto request) {
        
        try {
            adminPasswordService.secureForceChangePassword(
                request.getRequestingUserId(),
                request.getRequestingUserPassword(),
                request.getTargetUserId(),
                request.getNewPassword()
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Password changed successfully",
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                "message", e.getMessage(),
                "status", "error"
            ));
        }
    }
    
    /**
     * Securely change another user's password using login
     * Validates requesting user credentials before allowing the password change
     * 
     * POST /api/admin/password/force-change-by-login
     */
    @PostMapping("/force-change-by-login")
    public ResponseEntity<Map<String, String>> secureForceChangePasswordByLogin(
            @RequestBody AdminPasswordChangeByLoginDto request) {
        
        try {
            adminPasswordService.secureForceChangePasswordByLogin(
                request.getRequestingUserLogin(),
                request.getRequestingUserPassword(),
                request.getTargetUserId(),
                request.getNewPassword()
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Password changed successfully",
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                "message", e.getMessage(),
                "status", "error"
            ));
        }
    }
}

class AdminPasswordChangeByLoginDto {
    private String requestingUserLogin;
    private char[] requestingUserPassword;
    private Long targetUserId;
    private char[] newPassword;
    
    public String getRequestingUserLogin() {
        return requestingUserLogin;
    }
    
    public void setRequestingUserLogin(String requestingUserLogin) {
        this.requestingUserLogin = requestingUserLogin;
    }
    
    public char[] getRequestingUserPassword() {
        return requestingUserPassword;
    }
    
    public void setRequestingUserPassword(char[] requestingUserPassword) {
        this.requestingUserPassword = requestingUserPassword;
    }
    
    public Long getTargetUserId() {
        return targetUserId;
    }
    
    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
    
    public char[] getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(char[] newPassword) {
        this.newPassword = newPassword;
    }
}
