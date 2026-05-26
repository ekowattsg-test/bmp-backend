package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.DeliveryOrderItem;

@Repository
public interface DeliveryOrderItemRepository extends JpaRepository<DeliveryOrderItem, String> {

    List<DeliveryOrderItem> findByOrderId(String orderId);

    void deleteByOrderId(String orderId);
}
