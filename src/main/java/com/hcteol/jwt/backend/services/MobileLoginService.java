package com.hcteol.jwt.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcteol.jwt.backend.entities.MobileLogin;
import com.hcteol.jwt.backend.repositories.MobileLoginRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MobileLoginService {

    @Autowired
    private MobileLoginRepository mobileLoginRepository;

    public MobileLogin addMobileLogin(MobileLogin mobileLogin) {
        return mobileLoginRepository.save(mobileLogin);
    }

    public MobileLogin createNewRequest(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.isBlank()) {
            throw new IllegalArgumentException("mobileNumber is required");
        }

        MobileLogin mobileLogin = MobileLogin.builder()
                .loginKey(generateUniqueLoginKey())
                .mobileNumber(mobileNumber.trim())
                .requestTime(LocalDateTime.now())
                .otp(generateSixDigitOtp())
                .status("NEW")
                .build();

        return mobileLoginRepository.save(mobileLogin);
    }

    public List<MobileLogin> getAllMobileLogins() {
        return mobileLoginRepository.findAll();
    }

    public MobileLogin getMobileLoginByKey(String loginKey) {
        return mobileLoginRepository.findById(loginKey)
                .orElseThrow(() -> new RuntimeException("MobileLogin not found with key: " + loginKey));
    }

    public List<MobileLogin> getByMobileNumber(String mobileNumber) {
        return mobileLoginRepository.findByMobileNumber(mobileNumber);
    }

    @Transactional
    public MobileLogin updateMobileLogin(String loginKey, MobileLogin details) {
        MobileLogin existing = mobileLoginRepository.findById(loginKey)
                .orElseThrow(() -> new RuntimeException("MobileLogin not found with key: " + loginKey));

        log.debug("Updating MobileLogin {} - existing mobileNumber='{}', new mobileNumber='{}'",
                loginKey, existing.getMobileNumber(), details.getMobileNumber());

        existing.setMobileNumber(details.getMobileNumber());
        existing.setRequestTime(details.getRequestTime());
        existing.setStatus(details.getStatus());

        MobileLogin saved = mobileLoginRepository.save(existing);
        log.debug("Saved MobileLogin {} - mobileNumber='{}'", loginKey, saved.getMobileNumber());
        return saved;
    }

    public void deleteMobileLogin(String loginKey) {
        MobileLogin existing = mobileLoginRepository.findById(loginKey)
                .orElseThrow(() -> new RuntimeException("MobileLogin not found with key: " + loginKey));
        mobileLoginRepository.delete(existing);
    }

    private String generateUniqueLoginKey() {
        String loginKey;
        do {
            loginKey = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        } while (mobileLoginRepository.existsById(loginKey));
        return loginKey;
    }

    private String generateSixDigitOtp() {
        int otp = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%06d", otp);
    }
}
