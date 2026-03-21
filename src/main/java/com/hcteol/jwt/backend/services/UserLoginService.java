package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.UserLogin;
import com.hcteol.jwt.backend.repositories.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class UserLoginService {

    @Autowired
    private UserLoginRepository userLoginRepository;

    public UserLogin addUserLogin(UserLogin userLogin) {
        return userLoginRepository.save(userLogin);
    }

    public List<UserLogin> getAllUserLogins() {
        return userLoginRepository.findAll();
    }

    public UserLogin getUserLoginById(Long id) {
        return userLoginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserLogin not found with id: " + id));
    }

    public UserLogin updateUserLogin(Long id, UserLogin userLoginDetails) {
        UserLogin userLogin = userLoginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserLogin not found with id: " + id));

        userLogin.setFirstName(userLoginDetails.getFirstName());
        userLogin.setLastName(userLoginDetails.getLastName());
        userLogin.setUserId(userLoginDetails.getUserId());
        userLogin.setLoginType(userLoginDetails.getLoginType());
        userLogin.setTimeLogin(userLoginDetails.getTimeLogin());

        return userLoginRepository.save(userLogin);
    }

    public List<UserLogin> getUserLoginsByUserId(Long userId) {
        return userLoginRepository.findByUserId(userId);
    }

    public List<UserLogin> getUserLoginsByDateRange(LocalDateTime start, LocalDateTime end) {
        return userLoginRepository.findByTimeLoginBetween(start, end);
    }

    public void deleteUserLogin(Long id) {
        UserLogin userLogin = userLoginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserLogin not found with id: " + id));
        userLoginRepository.delete(userLogin);
    }
}
