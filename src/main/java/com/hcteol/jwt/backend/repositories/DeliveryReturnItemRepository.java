package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.DeliveryReturnItem;

@Repository
public interface DeliveryReturnItemRepository extends JpaRepository<DeliveryReturnItem, Long> {

    List<DeliveryReturnItem> findByReturnId(Long returnId);
}
