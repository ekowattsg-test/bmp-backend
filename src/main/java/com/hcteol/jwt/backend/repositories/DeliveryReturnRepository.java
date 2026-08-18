package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.DeliveryReturn;

@Repository
public interface DeliveryReturnRepository extends JpaRepository<DeliveryReturn, Long> {

    List<DeliveryReturn> findByDoId(String doId);
}
