package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "delivery_return_item")
public class DeliveryReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "return_id", nullable = false)
    private Long returnId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "quantity")
    private Integer quantity;
}
