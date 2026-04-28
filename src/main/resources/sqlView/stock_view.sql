CREATE OR REPLACE
VIEW `stock_view` AS
    SELECT 
        `product`.`product_id` AS `product_id`,
        `product`.`product_category` AS `product_category`,
        `product`.`product_code` AS `product_code`,
        `product`.`product_description` AS `product_description`,
        `product`.`product_name` AS `product_name`,
        `product`.`product_picture` AS `product_picture`,
        `product`.`product_class` AS `product_class`,
        `product`.`product_brand` AS `product_brand`,
        `product`.`common_name` AS `common_name`,
        `product`.`specification` AS `specification`,
        `product`.`uom` AS `uom`,
        `stock`.`stock_id` AS `stock_id`,
        `stock`.`create_date` AS `create_date`,
        `stock`.`stock_code` AS `stock_code`,
        COALESCE(`stock_movement`.`movement_id`, (0 - `product`.`product_id`)) AS `movement_id`,
        `stock_movement`.`movement_type` AS `movement_type`,
        COALESCE(`stock_movement`.`quantity`, 0) AS `quantity`,
        `stock_movement`.`record_date` AS `record_date`,
        `stock_movement`.`location` AS `location`,
        `stock_movement`.`reference` AS `reference`,
        `stock_movement`.`action_by` AS `action_by`,
        COALESCE(`stock_movement_code`.`hold_modifier`, 0) AS `hold_modifier`,
        `stock_movement_code`.`movement_description` AS `movement_description`,
        COALESCE(`stock_movement_code`.`stock_modifier`, 0) AS `stock_modifier`,
        (COALESCE(`stock_movement`.`quantity`, 0) * COALESCE(`stock_movement_code`.`stock_modifier`, 0)) AS `stock_moved`,
        (COALESCE(`stock_movement`.`quantity`, 0) * COALESCE(`stock_movement_code`.`hold_modifier`, 0)) AS `hold_moved`
    FROM
        `product`
        LEFT JOIN `stock` ON `product`.`product_id` = `stock`.`product_id`
        LEFT JOIN `stock_movement` ON `stock`.`stock_id` = `stock_movement`.`stock_id`
        LEFT JOIN `stock_movement_code` ON `stock_movement`.`movement_type` = `stock_movement_code`.`movement_type`
            WHERE
        (`stock_movement`.`record_date` >= (SELECT 
                `param`.`value_string`
            FROM
                `param`
            WHERE
                (`param`.`param_key` = 'baselineDate')));