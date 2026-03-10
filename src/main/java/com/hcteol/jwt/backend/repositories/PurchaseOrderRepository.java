package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {
    
    List<PurchaseOrder> findByVendorId(Long vendorId);
    
    List<PurchaseOrder> findByOrderStatus(String orderStatus);
}
