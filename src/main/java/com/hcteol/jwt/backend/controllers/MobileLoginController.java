package com.hcteol.jwt.backend.controllers;

import com.hcteol.jwt.backend.entities.MobileLogin;
import com.hcteol.jwt.backend.services.MobileLoginService;
import com.hcteol.jwt.backend.services.UserService;
import com.hcteol.jwt.backend.dtos.OtpDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import com.hcteol.jwt.backend.entities.Staff;
import com.hcteol.jwt.backend.services.UserLoginService;
import com.hcteol.jwt.backend.entities.UserLogin;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import com.hcteol.jwt.backend.dtos.CredentialsDto;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/mobile-logins")
public class MobileLoginController {

    @Autowired
    private MobileLoginService mobileLoginService;

    @Autowired
    private UserService userService;
    @Autowired
    private com.hcteol.jwt.backend.config.UserAuthenticationProvider userAuthenticationProvider;
    @Autowired
    private com.hcteol.jwt.backend.repositories.StaffRepository staffRepository;
    @Autowired
    private UserLoginService userLoginService;

    @Value("${OTP_QR_LOGIN_TIMEOUT_MINUTES:2}")
    private long otpQrLoginTimeoutMinutes;

    @Value("${MOBILE_LOGIN_USERNAME:}")
    private String mobileLoginUsername;

    @Value("${MOBILE_LOGIN_PASSWORD:}")
    private String mobileLoginPassword;

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

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpDto otpDto) {
        var userDto = userService.otpLogin(otpDto.getOtp());
        // attach JWT token
        try {
            userDto.setToken(userAuthenticationProvider.createToken(userDto));
        } catch (Exception ex) {
            // if token creation fails, log and continue returning user info without token
            System.out.println("Failed to create token for OTP login: " + ex.getMessage());
        }
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginWithKey(@RequestBody java.util.Map<String, String> payload) {
        String loginKey = payload == null ? null : payload.get("loginKey");
        if (loginKey == null || loginKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing loginKey");
        }
        // 1. retrieve mobilelogin and ensure status == NEW
        MobileLogin mobileLogin = null;
        try {
            mobileLogin = mobileLoginService.getMobileLoginByKey(loginKey);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login key");
        }

        if (mobileLogin.getStatus() == null || !"NEW".equals(mobileLogin.getStatus())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login key already used or invalid");
        }

        // timeout check
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime requestZdt = null;
        if (mobileLogin.getRequestTime() != null) {
            requestZdt = mobileLogin.getRequestTime().atZone(ZoneId.systemDefault());
        }
        ZonedDateTime cutoff = now.minusMinutes(otpQrLoginTimeoutMinutes);
        if (requestZdt == null || requestZdt.isBefore(cutoff)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login key expired");
        }

        // 2. retrieve staff by mobile number
        String mobileNumber = mobileLogin.getMobileNumber();
        Staff staff = staffRepository.findById(mobileNumber).orElse(null);
        if (staff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Staff not found for mobile number");
        }

        // 2b. verify credentials from env and authenticate
        if (mobileLoginUsername == null || mobileLoginUsername.isBlank() || mobileLoginPassword == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server credentials not configured");
        }

        CredentialsDto creds = new CredentialsDto(mobileLoginUsername, mobileLoginPassword.toCharArray());
        // authenticate via UserService.login which will throw AppException on failure
        com.hcteol.jwt.backend.dtos.UserDto userDto;
        try {
            userDto = userService.authenticate(creds);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }

        // replace last name with staff name for returned user and JWT
        userDto.setLastName(staff.getStaffName());

        // record login with staff name
        UserLogin userLogin = UserLogin.builder()
            .userId(userDto.getId())
            .firstName(userDto.getFirstName())
            .lastName(userDto.getLastName())
            .loginType("PDA")
            .timeLogin(java.time.LocalDateTime.now())
            .build();
        userLoginService.addUserLogin(userLogin);

        // create token with overridden lastName
        try {
            userDto.setToken(userAuthenticationProvider.createToken(userDto));
        } catch (Exception ex) {
            System.out.println("Failed to create token for login key flow: " + ex.getMessage());
        }

        // mark mobile login used
        mobileLogin.setStatus("USED");
        mobileLoginService.updateMobileLogin(loginKey, mobileLogin);

        return ResponseEntity.ok(userDto);
    }
}
