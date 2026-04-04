SELECT movement_id, product_id, product_category, product_code, product_description,
       product_name, product_picture, product_class, uom,
       stock_id, create_date, stock_code,
       movement_type, quantity, record_date, location, reference,
       hold_modifier, movement_description, stock_modifier,
    stock_moved, hold_moved
FROM stock_view
WHERE stock_code = :stockCode;
