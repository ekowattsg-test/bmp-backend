package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcteol.jwt.backend.entities.PurchaseOrderView;

public interface PurchaseOrderViewRepository extends JpaRepository<PurchaseOrderView, String> {

    List<PurchaseOrderView> findByOrderId(String orderId);

    List<PurchaseOrderView> findByVendorId(Long vendorId);

    List<PurchaseOrderView> findByOrderStatus(String orderStatus);

    List<PurchaseOrderView> findByProductCode(String productCode);
}
