package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    Optional<User> findByMobileNumber(String mobileNumber);
}
