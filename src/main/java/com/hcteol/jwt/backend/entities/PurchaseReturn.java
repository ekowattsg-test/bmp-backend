package com.hcteol.jwt.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "purchase_return")
public class PurchaseReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Long returnId;

    @Column(name = "po_id", nullable = false)
    private String poId;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "return_status")
    private String returnStatus; // NEW, APPROVED, CREDITED

    @Column(name = "returned_by")
    private String returnedBy;

    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "location")
    private String location;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "credit_amount")
    private BigDecimal creditAmount;
}
