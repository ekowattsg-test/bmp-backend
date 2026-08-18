package com.hcteol.jwt.backend.entities;

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
@Table(name = "delivery_return")
public class DeliveryReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Long returnId;

    @Column(name = "do_id", nullable = false)
    private String doId;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "return_status")
    private String returnStatus; // NEW, CREDITED

    @Column(name = "returned_by")
    private String returnedBy;

    @Column(name = "location")
    private String location;

    @Column(name = "total_quantity")
    private Integer totalQuantity;
}
