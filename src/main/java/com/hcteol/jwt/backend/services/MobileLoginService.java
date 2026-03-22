package com.hcteol.jwt.backend.services;

import com.hcteol.jwt.backend.entities.MobileLogin;
import com.hcteol.jwt.backend.repositories.MobileLoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class MobileLoginService {

    @Autowired
    private MobileLoginRepository mobileLoginRepository;

    public MobileLogin addMobileLogin(MobileLogin mobileLogin) {
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
}
