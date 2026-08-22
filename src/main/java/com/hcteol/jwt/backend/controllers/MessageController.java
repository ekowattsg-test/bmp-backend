package com.hcteol.jwt.backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.dtos.MessageDto;
import com.hcteol.jwt.backend.dtos.SendMessageRequest;
import com.hcteol.jwt.backend.dtos.UserDto;
import com.hcteol.jwt.backend.services.MessageService;
import com.hcteol.jwt.backend.services.StaffService;
import com.hcteol.jwt.backend.services.UserService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private UserService userService;

    @GetMapping("/conversations")
    public ResponseEntity<List<MessageDto>> getConversations(
            Authentication authentication,
            @RequestParam String mobileNumber) {
        String staffId = resolveStaffId(authentication, mobileNumber);
        return ResponseEntity.ok(messageService.getConversations(staffId));
    }

    @GetMapping("/direct")
    public ResponseEntity<List<MessageDto>> getDirectMessages(
            Authentication authentication,
            @RequestParam String mobileNumber,
            @RequestParam String staffId) {
        String viewerStaffId = resolveStaffId(authentication, mobileNumber);
        return ResponseEntity.ok(messageService.getDirectConversation(viewerStaffId, staffId));
    }

    @GetMapping("/project")
    public ResponseEntity<List<MessageDto>> getProjectMessages(
            Authentication authentication,
            @RequestParam String mobileNumber,
            @RequestParam String projectCode) {
        String viewerStaffId = resolveStaffId(authentication, mobileNumber);
        return ResponseEntity.ok(messageService.getProjectMessages(viewerStaffId, projectCode));
    }

    @GetMapping("/broadcast")
    public ResponseEntity<List<MessageDto>> getBroadcastMessages(
            Authentication authentication,
            @RequestParam String mobileNumber) {
        String viewerStaffId = resolveStaffId(authentication, mobileNumber);
        return ResponseEntity.ok(messageService.getBroadcastMessages(viewerStaffId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            Authentication authentication,
            @RequestParam String mobileNumber) {
        String staffId = resolveStaffId(authentication, mobileNumber);
        long count = messageService.getUnreadCount(staffId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(
            Authentication authentication,
            @RequestParam String mobileNumber,
            @RequestBody SendMessageRequest request) {
        String staffId = resolveStaffId(authentication, mobileNumber);
        return ResponseEntity.ok(messageService.sendMessage(request, staffId));
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            Authentication authentication,
            @RequestParam String mobileNumber,
            @PathVariable Long messageId) {
        String staffId = resolveStaffId(authentication, mobileNumber);
        messageService.markAsRead(messageId, staffId);
        return ResponseEntity.ok().build();
    }

    private String resolveStaffId(Authentication authentication, String mobileNumber) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDto user)) {
            throw new IllegalArgumentException("Authentication required");
        }
        if (mobileNumber == null || mobileNumber.isBlank()) {
            throw new IllegalArgumentException("Mobile number is required");
        }
        UserDto currentUser = userService.findByLogin(user.getLogin());
        if (currentUser == null || !mobileNumber.trim().equalsIgnoreCase(currentUser.getMobileNumber())) {
            throw new IllegalArgumentException("Mobile number does not match authenticated user");
        }
        return staffService.getStaffByMobileNumber(mobileNumber)
                .map(com.hcteol.jwt.backend.entities.Staff::getStaffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found for user"));
    }
}
