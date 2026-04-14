package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.User;
import com.hcteol.jwt.backend.repositories.UserRepository;
import com.hcteol.jwt.backend.exceptions.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;

@Service
public class PasswordValidationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Validates a user's password
     * 
     * @param userId the ID of the user
     * @param password the password to validate (as char array for security)
     * @return true if password is valid, false otherwise
     * @throws AppException if user not found
     */
    public boolean validatePassword(Long userId, char[] password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        
        return passwordEncoder.matches(CharBuffer.wrap(password), user.getPassword());
    }
    
    /**
     * Validates a user's password by login
     * 
     * @param login the user login
     * @param password the password to validate (as char array for security)
     * @return true if password is valid, false otherwise
     * @throws AppException if user not found
     */
    public boolean validatePasswordByLogin(String login, char[] password) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        
        return passwordEncoder.matches(CharBuffer.wrap(password), user.getPassword());
    }
    
    /**
     * Validates a user's password and throws exception if invalid
     * 
     * @param userId the ID of the user
     * @param password the password to validate (as char array for security)
     * @throws AppException if password is invalid
     */
    public void validatePasswordOrThrow(Long userId, char[] password) {
        if (!validatePassword(userId, password)) {
            throw new AppException("Invalid password", HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * Validates a user's password by login and throws exception if invalid
     * 
     * @param login the user login
     * @param password the password to validate (as char array for security)
     * @throws AppException if password is invalid
     */
    public void validatePasswordByLoginOrThrow(String login, char[] password) {
        if (!validatePasswordByLogin(login, password)) {
            throw new AppException("Invalid password", HttpStatus.UNAUTHORIZED);
        }
    }
}
