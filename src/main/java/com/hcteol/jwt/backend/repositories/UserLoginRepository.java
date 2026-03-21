package com.hcteol.jwt.backend.repositories;

import com.hcteol.jwt.backend.entities.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {

	List<UserLogin> findByUserId(Long userId);

	List<UserLogin> findByTimeLoginBetween(LocalDateTime start, LocalDateTime end);

}
