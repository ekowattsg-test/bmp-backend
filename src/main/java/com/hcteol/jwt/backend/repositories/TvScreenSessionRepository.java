package com.hcteol.jwt.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.TvScreenSession;

@Repository
public interface TvScreenSessionRepository extends JpaRepository<TvScreenSession, Long> {

    Optional<TvScreenSession> findBySessionCode(String sessionCode);

    Optional<TvScreenSession> findByExchangeCode(String exchangeCode);
}
