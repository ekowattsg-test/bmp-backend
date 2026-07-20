package com.hcteol.jwt.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcteol.jwt.backend.entities.RequisitionOrder;

@Repository
public interface RequisitionOrderRepository extends JpaRepository<RequisitionOrder, Long> {

    List<RequisitionOrder> findByRequisitionCycleId(Long requisitionCycleId);

    List<RequisitionOrder> findByProjectCode(String projectCode);

    List<RequisitionOrder> findByProductRequested(Long productRequested);

    List<RequisitionOrder> findByPurchaseOrderId(String purchaseOrderId);

    List<RequisitionOrder> findByPurchaseOrderIdIsNull();

    List<RequisitionOrder> findByRequisitionCycleIdAndProjectCode(Long requisitionCycleId, String projectCode);

    List<RequisitionOrder> findByRequisitionCycleIdAndProductRequested(Long requisitionCycleId,
            Long productRequested);

    List<RequisitionOrder> findByRequisitionCycleIdAndPurchaseOrderId(Long requisitionCycleId, String purchaseOrderId);

    List<RequisitionOrder> findByProjectCodeAndProductRequested(String projectCode, Long productRequested);

    List<RequisitionOrder> findByProjectCodeAndPurchaseOrderId(String projectCode, String purchaseOrderId);

    List<RequisitionOrder> findByProductRequestedAndPurchaseOrderId(Long productRequested, String purchaseOrderId);

    List<RequisitionOrder> findByRequisitionCycleIdAndProjectCodeAndProductRequested(Long requisitionCycleId,
            String projectCode,
            Long productRequested);

    List<RequisitionOrder> findByRequisitionCycleIdAndProjectCodeAndPurchaseOrderId(Long requisitionCycleId,
            String projectCode,
            String purchaseOrderId);

    List<RequisitionOrder> findByRequisitionCycleIdAndProductRequestedAndPurchaseOrderId(Long requisitionCycleId,
            Long productRequested,
            String purchaseOrderId);

    List<RequisitionOrder> findByProjectCodeAndProductRequestedAndPurchaseOrderId(String projectCode,
            Long productRequested,
            String purchaseOrderId);

    List<RequisitionOrder> findByRequisitionCycleIdAndProjectCodeAndProductRequestedAndPurchaseOrderId(
            Long requisitionCycleId,
            String projectCode,
            Long productRequested,
            String purchaseOrderId);
}
