package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Data
@Entity
@Immutable
@Table(name = "stock_view")
public class StockView {

    // Product fields
    @Id
    @Column(name = "movement_id")
    private Long movementId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_picture", length = 2000)
    private String productPicture;

    @Column(name = "baselined_date")
    private String baselinedDate;

    @Column(name = "baselined_quantity")
    private Long baselinedQuantity;

    @Column(name = "product_class")
    private String productClass;

    // Stock fields
    @Column(name = "stock_id")
    private Long stockId;

    @Column(name = "create_date")
    private String createDate;

    @Column(name = "stock_code")
    private String stockCode;

    // StockMovement fields
    @Column(name = "movement_type")
    private String movementType;

    private Integer quantity;

    @Column(name = "record_date")
    private String recordDate;

    private String location;

    // StockMovementCode fields
    @Column(name = "hold_modifier")
    private Integer holdModifier;

    @Column(name = "movement_description")
    private String movementDescription;

    @Column(name = "stock_modifier")
    private Integer stockModifier;

    // Calculated fields
    @Column(name = "stock_moved")
    private Integer stockMoved;

    @Column(name = "hold_moved")
    private Integer holdMoved;
}
