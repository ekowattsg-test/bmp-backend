package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.DeliveryOrder;

@Repository
public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, String> {

    List<DeliveryOrder> findByOrderStatus(String orderStatus);
}
