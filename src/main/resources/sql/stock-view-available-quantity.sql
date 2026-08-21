SELECT COALESCE(SUM(stock_moved + hold_moved), 0) AS available_quantity
FROM stock_view
WHERE product_id = :productId
  AND LOWER(location) LIKE '%' || LOWER(:location) || '%';
