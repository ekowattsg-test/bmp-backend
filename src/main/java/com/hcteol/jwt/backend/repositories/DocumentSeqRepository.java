package com.hcteol.jwt.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.hcteol.jwt.backend.entities.DocumentSeq;

public interface DocumentSeqRepository extends JpaRepository<DocumentSeq, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from DocumentSeq d where d.docType = :id")
    java.util.Optional<DocumentSeq> findByDocTypeForUpdate(@Param("id") String id);

}
