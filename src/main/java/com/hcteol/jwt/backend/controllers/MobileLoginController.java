package com.hcteol.jwt.backend.controllers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.dtos.CredentialsDto;
import com.hcteol.jwt.backend.dtos.MobileLoginRequestDto;
import com.hcteol.jwt.backend.dtos.OtpDto;
import com.hcteol.jwt.backend.entities.MobileLogin;
import com.hcteol.jwt.backend.entities.Staff;
import com.hcteol.jwt.backend.entities.UserLogin;
import com.hcteol.jwt.backend.services.MobileLoginService;
import com.hcteol.jwt.backend.services.StaffService;
import com.hcteol.jwt.backend.services.UserLoginService;
import com.hcteol.jwt.backend.services.UserService;

@RestController
@RequestMapping("/api/mobile-logins")
public class MobileLoginController {

    private static final Logger log = LoggerFactory.getLogger(MobileLoginController.class);

    @Autowired
    private MobileLoginService mobileLoginService;

    @Autowired
    private UserService userService;
    @Autowired
    private StaffService staffService;
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

    @PostMapping("/request")
    public ResponseEntity<?> createRequest(@RequestBody MobileLoginRequestDto requestDto) {
        if (requestDto == null || requestDto.getMobileNumber() == null || requestDto.getMobileNumber().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("mobileNumber is required");
        }

        String requestedMobileNumber = requestDto.getMobileNumber().trim();
        log.debug("Creating mobile login request for requested mobile number='{}'", requestedMobileNumber);

        var staffOpt = staffService.getStaffByMobileNumber(requestedMobileNumber);
        if (staffOpt.isEmpty()) {
            log.debug("No staff record found for requested mobile number='{}'", requestedMobileNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found for mobile number");
        }

        Staff staff = staffOpt.get();
        log.debug("Found staff for mobile number='{}': staffId='{}', staffName='{}', active={}",
                requestedMobileNumber, staff.getStaffId(), staff.getStaffName(), staff.getActive());
        if (staff.getActive() == null || staff.getActive() != 1) {
            log.debug("Staff is inactive for requested mobile number='{}'", requestedMobileNumber);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Staff is inactive");
        }

        MobileLogin saved = mobileLoginService.createNewRequest(requestedMobileNumber);
        log.debug("Created mobile login request for requested mobile number='{}' with loginKey='{}'",
                requestedMobileNumber, saved.getLoginKey());
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
        MobileLogin mobileLogin;
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
        log.debug("Resolving staff during login key flow for mobile number='{}'", mobileNumber);
        Staff staff = staffRepository.findByMobileNumber(mobileNumber).orElse(null);
        if (staff == null) {
            log.debug("No staff record found during login key flow for mobile number='{}'", mobileNumber);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Staff not found for mobile number");
        }
        log.debug("Resolved staff during login key flow for mobile number='{}': staffId='{}', staffName='{}', active={}",
                mobileNumber, staff.getStaffId(), staff.getStaffName(), staff.getActive());
        if (staff.getActive() == null || staff.getActive() != 1) {
            log.debug("Staff is inactive during login key flow for mobile number='{}'", mobileNumber);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Staff is inactive");
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

        // replace returned user's fields with staff info: set staff name as lastName,
        // replace mobile number with staff mobile and clear first name
        userDto.setLastName(staff.getStaffName());
        userDto.setMobileNumber(staff.getMobileNumber());
        userDto.setFirstName(null);

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
