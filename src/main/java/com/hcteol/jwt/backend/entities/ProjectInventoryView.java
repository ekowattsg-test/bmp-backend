package com.hcteol.jwt.backend.entities;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@Immutable
@Subselect("select * from project_inventory_view")
@Synchronize({
    "project_asset",
    "project_stock",
    "project_bundle",
    "project_stream_asset",
    "project_stream_bundle",
    "project_task",
    "project_stream",
    "project",
    "product",
    "product_bundle",
    "bundle_member"
})
public class ProjectInventoryView {

    @Id
    @Column(name = "row_id")
    private String rowId;

    @Column(name = "inventory_id")
    private Long inventoryId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "activity_id")
    private Long activityId;

    private Double quantity;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "product_uom")
    private String productUom;

    @Column(name = "activity_name")
    private String activityName;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "actual_start_date")
    private String actualStartDate;

    @Column(name = "actual_end_date")
    private String actualEndDate;

    private String status;

    @Column(name = "project_code")
    private String projectCode;

    @Column(name = "inventory_type")
    private String inventoryType;
}
