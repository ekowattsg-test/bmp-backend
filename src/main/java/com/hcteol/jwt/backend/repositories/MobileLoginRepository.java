package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.MobileLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MobileLoginRepository extends JpaRepository<MobileLogin, String> {
    List<MobileLogin> findByMobileNumber(String mobileNumber);
}
