package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.User;
import com.hcteol.jwt.backend.repositories.UserRepository;
import com.hcteol.jwt.backend.exceptions.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Date;

@Service
public class AdminPasswordService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private PasswordValidationService passwordValidationService;
    
    /**
     * Force change a user's password with security validation
     * First validates the requesting admin user's credentials before allowing the change
     * 
     * @param requestingUserId the ID of the admin/requesting user
     * @param requestingUserPassword the password of the admin/requesting user (as char array for security)
     * @param targetUserId the ID of the user whose password will be changed
     * @param newPassword the new password for the target user (as char array for security)
     * @throws AppException if requesting user not found, credentials invalid, or target user not found
     */
    public void secureForceChangePassword(Long requestingUserId, char[] requestingUserPassword, 
                                         Long targetUserId, char[] newPassword) {
        // Validate requesting user's credentials
        passwordValidationService.validatePasswordOrThrow(requestingUserId, requestingUserPassword);
        
        // Verify requesting user exists and is active
        User requestingUser = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new AppException("Requesting user not found", HttpStatus.NOT_FOUND));
        
        if (requestingUser.getActive() == null || requestingUser.getActive() == 0) {
            throw new AppException("Requesting user account is inactive", HttpStatus.FORBIDDEN);
        }
        
        // Now change the target user's password
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new AppException("Target user not found", HttpStatus.NOT_FOUND));
        
        targetUser.setPassword(passwordEncoder.encode(CharBuffer.wrap(newPassword)));
        targetUser.setLastPasswordChanged(new Date());
        userRepository.save(targetUser);
    }
    
    /**
     * Force change a user's password with security validation (by login)
     * First validates the requesting admin user's credentials before allowing the change
     * 
     * @param requestingUserLogin the login of the admin/requesting user
     * @param requestingUserPassword the password of the admin/requesting user (as char array for security)
     * @param targetUserId the ID of the user whose password will be changed
     * @param newPassword the new password for the target user (as char array for security)
     * @throws AppException if requesting user not found, credentials invalid, or target user not found
     */
    public void secureForceChangePasswordByLogin(String requestingUserLogin, char[] requestingUserPassword,
                                                 Long targetUserId, char[] newPassword) {
        // Validate requesting user's credentials
        passwordValidationService.validatePasswordByLoginOrThrow(requestingUserLogin, requestingUserPassword);
        
        // Verify requesting user exists and is active
        User requestingUser = userRepository.findByLogin(requestingUserLogin)
                .orElseThrow(() -> new AppException("Requesting user not found", HttpStatus.NOT_FOUND));
        
        if (requestingUser.getActive() == null || requestingUser.getActive() == 0) {
            throw new AppException("Requesting user account is inactive", HttpStatus.FORBIDDEN);
        }
        
        // Now change the target user's password
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new AppException("Target user not found", HttpStatus.NOT_FOUND));
        
        targetUser.setPassword(passwordEncoder.encode(CharBuffer.wrap(newPassword)));
        targetUser.setLastPasswordChanged(new Date());
        userRepository.save(targetUser);
    }
    
    /**
     * Force change a user's password (admin operation without security validation)
     * WARNING: Use only in trusted environments or combine with separate authorization checks
     * This bypasses the old password verification requirement and validates no requesting user
     * 
     * @param userId the ID of the user
     * @param newPassword the new password (as char array for security)
     * @throws AppException if user not found
     */
    public void forceChangePassword(Long userId, char[] newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(newPassword)));
        user.setLastPasswordChanged(new Date());
        userRepository.save(user);
    }
    
    /**
     * Force change a user's password by login (admin operation without security validation)
     * WARNING: Use only in trusted environments or combine with separate authorization checks
     * This bypasses the old password verification requirement and validates no requesting user
     * 
     * @param login the user login
     * @param newPassword the new password (as char array for security)
     * @throws AppException if user not found
     */
    public void forceChangePasswordByLogin(String login, char[] newPassword) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(newPassword)));
        user.setLastPasswordChanged(new Date());
        userRepository.save(user);
    }
}
