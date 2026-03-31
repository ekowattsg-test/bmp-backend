package com.hcteol.jwt.backend.entities;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Data
@Entity
@Immutable
@Subselect("select * from purchase_order_view")
@Synchronize({"purchase_order", "purchase_order_item", "vendor"})
public class PurchaseOrderView {

    // PurchaseOrderItem fields
    @Id
    @Column(name = "item_id")
    private String itemId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "item_type")
    private String itemType;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "internal_product_code")
    private String internalProductCode;

    @Column(name = "internal_order_id")
    private Long internalOrderId;

    private Integer quantity;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "line_total")
    private Double lineTotal;

    // PurchaseOrder fields
    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "purchase_amount")
    private Double purchaseAmount;

    // Vendor fields
    @Column(name = "vendor_name")
    private String vendorName;
}
