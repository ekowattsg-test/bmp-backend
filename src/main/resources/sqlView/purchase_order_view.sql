CREATE OR REPLACE
VIEW `purchase_order_view` AS
    SELECT 
        `o`.`vendor_id` AS `vendor_id`,
        `v`.`vendor_name` AS `vendor_name`,
        `o`.`order_date` AS `order_date`,
        `o`.`order_status` AS `order_status`,
        `o`.`purchase_amount` AS `purchase_amount`,
        `i`.`item_id` AS `item_id`,
        `i`.`internal_order_id` AS `internal_order_id`,
        `i`.`internal_product_code` AS `internal_product_code`,
        `i`.`line_total` AS `line_total`,
        `i`.`order_id` AS `order_id`,
        `i`.`product_code` AS `product_code`,
        `i`.`quantity` AS `quantity`,
        `i`.`unit_price` AS `unit_price`,
        `i`.`item_type` AS `item_type`
    FROM
        ((`purchase_order` `o`
        JOIN `purchase_order_item` `i`)
        JOIN `vendor` `v`)
    WHERE
        ((`o`.`order_id` = `i`.`order_id`)
            AND (`o`.`vendor_id` = `v`.`vendor_id`));