# API Endpoints

This document lists the API endpoints and the `DeliveryOrder` request/response DTO fields (now using `customerId` and `projectCode`).

## DeliveryOrder DTO (request/response)

- `orderId` (String) — DO-... document id
- `customerId` (Long) — customer identifier
- `projectCode` (String) — optional project code
- `orderDate` (Date)
- `deliveryAmount` (Double)
- `orderStatus` (String) — e.g. NEW, ISSUED, CONFIRMED, READY, RDELIVERED/DELIVERED, CANCELLED
- `issuedDate` (Date)
- `confirmedDate` (Date)
- `readyDate` (Date)
- `deliveredDate` (Date)
- `cancelledDate` (Date)
- `items` (List of `DeliveryOrderItemDto`)

DeliveryOrderItemDto fields (summary): `itemId`, `orderId`, `itemType`, `productCode`, `internalProductCode`, `internalOrderId`, `quantity`, `unitPrice`, `lineTotal`.

## Delivery Order endpoints

- GET /api/deliveryOrders — list all delivery orders
- GET /api/deliveryOrders/{orderId} — get delivery order by id
- POST /api/deliveryOrders — create delivery order (body: DeliveryOrderDto)
- PUT /api/deliveryOrders/{orderId} — update delivery order (body: DeliveryOrderDto)
- DELETE /api/deliveryOrders/{orderId} — delete delivery order
- PATCH /api/deliveryOrders/{orderId}/status — update order status (body: status string)

## Delivery Order Item endpoints

- POST /api/deliveryOrderItems — create item (body: DeliveryOrderItemDto)
- GET /api/deliveryOrderItems — list items
- GET /api/deliveryOrderItems/{itemId} — get item by id
- GET /api/deliveryOrderItems/order/{orderId} — get items by order id
- PUT /api/deliveryOrderItems/{itemId} — update item
- DELETE /api/deliveryOrderItems/{itemId} — delete item
- DELETE /api/deliveryOrderItems/order/{orderId} — delete items by order id

## Project Leader endpoints

- GET /api/projectleaders — list all project leaders
- GET /api/projectleaders/{id} — get project leader by id
- GET /api/projectleaders/project/{projectCode} — list project leaders by project code
- GET /api/projectleaders/staff/{projectLeaderStaffId} — list project leaders by staff id
- GET /api/projectleaders/active/{active} — list project leaders by active status (1 active, 0 inactive)
- POST /api/projectleaders — create project leader
- PUT /api/projectleaders/{id} — update project leader
- DELETE /api/projectleaders/{id} — delete project leader

## Project Stream endpoints

- GET /api/projectstreams — list all project streams
- GET /api/projectstreams?projectCode={projectCode} — list project streams by project code
- GET /api/projectstreams?streamType={streamType} — list project streams by stream type
- GET /api/projectstreams?projectCode={projectCode}&streamType={streamType} — list project streams by project code and stream type
- GET /api/projectstreams/{id} — get project stream by id
- GET /api/projectstreams/project/{projectCode} — list project streams by project code (path variant)
- POST /api/projectstreams — create project stream
- PUT /api/projectstreams/{id} — update project stream
- DELETE /api/projectstreams/{id} — delete project stream

## ProjectTask entity payload (request/response)

- `projectTaskId` (Long)
- `projectStreamId` (Long)
- `taskType` (String) — `D` dependent task, `A` anchor task, `B` baseline task
- `taskName` (String)
- `staffId` (String) — person in-charge of task
- `taskStartDate` (String)
- `taskEndDate` (String)

## ProjectTask endpoints

- GET /api/projecttasks — list all project tasks
- GET /api/projecttasks/{id} — get project task by id
- GET /api/projecttasks/stream/{projectStreamId} — list project tasks by stream id
- POST /api/projecttasks — create project task (body: ProjectTask)
- PUT /api/projecttasks/{id} — update project task (body: ProjectTask)
- DELETE /api/projecttasks/{id} — delete project task

## Notable other endpoints (summary)

- /api/customers — customer CRUD/read endpoints
- /api/companies — company CRUD
- /api/products — product CRUD + filter
- /api/purchaseOrders — purchase order CRUD + `/product/{productId}/stats` endpoint
- /api/purchaseOrderItems — purchase order item CRUD
- /api/purchaseorderview — read-only view endpoints for PO aggregated data
- /api/stocks, /api/stockmovements, /api/stockviews — stock & movement endpoints
- /api/projects, /api/projectstreams, /api/projecttasks, /api/projectstocks, /api/projectbundles, /api/projectmanpowers, /api/projectleaders — project-related endpoints
- /api/staffs, /api/staffskills, /api/staffskillprofiles — staff management endpoints
- /api/roles, /api/operationroles, /api/operationstaffs — role / operation endpoints
- Auth & login endpoints: `/login`, `/register`, `/api/mobile-logins`, `/api/userlogin` etc.

### Mobile Login endpoint notes

- `POST /api/mobile-logins/request` - creates a mobile login request from `mobileNumber` only. Server generates `loginKey` (alphanumeric UUID), `requestTime` (current timestamp), `otp` (6-digit numeric), and sets `status` to `NEW`.

## How to regenerate a full raw list locally

Run this in PowerShell from the project root to print all mapping annotations:

```powershell
Select-String -Path 'src/main/java/**/*.java' -Pattern '@RequestMapping|@GetMapping|@PostMapping|@PutMapping|@DeleteMapping|@PatchMapping' | Select-Object Path,LineNumber,Line
```

This prints all controller mapping annotations and their file locations so you can build a complete endpoint matrix.

---

Generated from the codebase on June 11, 2026.
