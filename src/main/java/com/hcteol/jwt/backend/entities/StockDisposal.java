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
@Table(name = "stock_disposal")
public class StockDisposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disposal_id")
    private Long disposalId;

    @Column(name = "location")
    private String location;

    @Column(name = "disposed_by")
    private String disposedBy;

    @Column(name = "disposal_date")
    private LocalDateTime disposalDate;

    @Column(name = "disposal_reason")
    private String disposalReason;

    @Column(name = "disposal_method")
    private String disposalMethod;

    @Column(name = "disposal_status")
    private String disposalStatus; // NEW, DISPOSED

    @Column(name = "total_quantity")
    private Integer totalQuantity;
}
