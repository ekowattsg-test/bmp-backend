-- DESIGN NOTE: row_id uses inventory_type + id + qualifier (activityId for non-bundled, bundleId for bundled).
-- Format: <inventoryType>-<inventoryId>-<qualifier> ensures uniqueness across all branches.
-- Non-bundled: qualifier = activityId (task_id or stream_id)
-- Bundled: qualifier = bundleId (project_bundle_id or project_stream_bundle_id)
DROP VIEW IF EXISTS project_inventory_view CASCADE;

CREATE OR REPLACE VIEW project_inventory_view AS
SELECT (t.inventory_type || '-' || t.inventory_id || '-' || t.qualifier) AS row_id,
       t.inventory_id,
       t.product_id,
       t.activity_id,
       t.quantity,
       t.product_name,
       t.product_category,
       t.product_uom,
       t.activity_name,
       t.start_date,
       t.end_date,
       t.actual_start_date,
       t.actual_end_date,
       t.status,
       t.project_code,
       t.inventory_type
FROM (
  -- Asset branch: qualifier = activity_id (task_id)
  SELECT a.project_stock_id AS inventory_id,
         a.product_id,
         a.project_task_id AS activity_id,
         a.project_task_id::text AS qualifier,
         a.quantity,
         d.product_name,
         d.product_category,
         d.uom AS product_uom,
         t.task_name AS activity_name,
         t.task_start_date AS start_date,
         t.task_end_date AS end_date,
         t.actual_start_date,
         t.actual_end_date,
         t.task_status AS status,
         p.project_code,
         'Asset'::text AS inventory_type
    FROM project_asset a,
         project_task t,
         project_stream s,
         project p,
         product d
   WHERE a.project_task_id = t.project_task_id
     AND t.project_stream_id = s.project_stream_id
     AND s.project_code::text = p.project_code::text
     AND a.product_id = d.product_id
  UNION ALL
  -- Stock branch: qualifier = activity_id (task_id)
  SELECT a.project_stock_id AS inventory_id,
         a.product_id,
         a.project_task_id AS activity_id,
         a.project_task_id::text AS qualifier,
         a.quantity,
         d.product_name,
         d.product_category,
         d.uom AS product_uom,
         t.task_name AS activity_name,
         t.task_start_date AS start_date,
         t.task_end_date AS end_date,
         t.actual_start_date,
         t.actual_end_date,
         t.task_status AS status,
         p.project_code,
         'Stock'::text AS inventory_type
    FROM project_stock a,
         project_task t,
         project_stream s,
         project p,
         product d
   WHERE a.project_task_id = t.project_task_id
     AND t.project_stream_id = s.project_stream_id
     AND s.project_code::text = p.project_code::text
     AND a.product_id = d.product_id
  UNION ALL
  -- Bundle branch: qualifier = project_bundle_id
  SELECT a.member_id AS inventory_id,
         a.product_id,
         b.project_task_id AS activity_id,
         b.project_bundle_id::text AS qualifier,
         a.quantity * b.quantity AS quantity,
         d.product_name,
         d.product_category,
         d.uom AS product_uom,
         t.task_name AS activity_name,
         t.task_start_date AS start_date,
         t.task_end_date AS end_date,
         t.actual_start_date,
         t.actual_end_date,
         t.task_status AS status,
         s.project_code,
         'Bundle'::text AS inventory_type
    FROM project_bundle b,
         product_bundle e,
         bundle_member a,
         project_task t,
         project_stream s,
         product d
   WHERE a.bundle_id = e.bundle_id
     AND b.bundle_id = e.bundle_id
     AND b.project_task_id = t.project_task_id
     AND t.project_stream_id = s.project_stream_id
     AND a.product_id = d.product_id
  UNION ALL
  -- Stream Asset branch: qualifier = activity_id (stream_id)
  SELECT a.project_stream_asset_id AS inventory_id,
         a.product_id,
         a.project_stream_id AS activity_id,
         a.project_stream_id::text AS qualifier,
         a.quantity,
         d.product_name,
         d.product_category,
         d.uom AS product_uom,
         s.stream_name AS activity_name,
         s.stream_start_date AS start_date,
         s.stream_end_date AS end_date,
         s.stream_start_date AS actual_start_date,
         s.stream_end_date AS actual_end_date,
         'In Progress'::text AS status,
         s.project_code,
         'StreamAsset'::text AS inventory_type
    FROM project_stream_asset a,
         project_stream s,
         product d
   WHERE a.project_stream_id = s.project_stream_id
     AND a.product_id = d.product_id
  UNION ALL
  -- Stream Bundle branch: qualifier = project_stream_bundle_id
  SELECT a.member_id AS inventory_id,
         a.product_id,
         b.project_stream_id AS activity_id,
         b.project_stream_bundle_id::text AS qualifier,
         a.quantity::double precision * b.quantity AS quantity,
         d.product_name,
         d.product_category,
         d.uom AS product_uom,
         s.stream_name AS activity_name,
         s.stream_start_date AS start_date,
         s.stream_end_date AS end_date,
         s.stream_start_date AS actual_start_date,
         s.stream_end_date AS actual_end_date,
         'In Progress'::text AS status,
         s.project_code,
         'StreamBundle'::text AS inventory_type
    FROM project_stream_bundle b,
         product_bundle e,
         bundle_member a,
         project_stream s,
         product d
   WHERE a.bundle_id = e.bundle_id
     AND b.bundle_id = e.bundle_id
     AND b.project_stream_id = s.project_stream_id
     AND a.product_id = d.product_id
) t;