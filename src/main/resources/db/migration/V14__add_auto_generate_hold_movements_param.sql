INSERT INTO param (param_key, value_string, changeable)
SELECT 'systemStockCode', 'SYSHOLD', 1
WHERE NOT EXISTS (
    SELECT 1 FROM param WHERE param_key = 'systemStockCode'
);
