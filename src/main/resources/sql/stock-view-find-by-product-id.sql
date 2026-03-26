SELECT movement_id, product_id, product_category, product_code, product_description,
       product_name, product_picture, baselined_date, baselined_quantity, product_class,
       stock_id, create_date, stock_code,
       movement_type, quantity, record_date, location,
       hold_modifier, movement_description, stock_modifier,
    stock_moved, hold_moved
FROM stock_view
WHERE product_id = :productId;
