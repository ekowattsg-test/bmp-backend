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
- `parentTaskId` (Long) — parent task reference for dependent tasks
- `milestoneTaskId` (Long) — reference milestone task id
- `taskDuration` (Long) — duration in days
- `taskStartDate` (String)
- `taskEndDate` (String)
- `taskStatus` (String) — e.g. `Not Started`, `In Progress`, `Completed`
- `actualStartDate` (String)
- `actualEndDate` (String)
- `remarks` (String)

## ProjectTask endpoints

- GET /api/projecttasks — list all project tasks
- GET /api/projecttasks/{id} — get project task by id
- GET /api/projecttasks/stream/{projectStreamId} — list project tasks by stream id
- POST /api/projecttasks — create project task (body: ProjectTask)
- POST /api/projecttasks/calculate — calculate task dates from submitted task + task type alignWith rules (body: ProjectTask, response: calculated ProjectTask, not persisted)
- PUT /api/projecttasks/{id} — update project task (body: ProjectTask)
- DELETE /api/projecttasks/{id} — delete project task

ProjectTask calculate rules (by ProjectTaskType.alignWith):

- `no` — no action
- `latest` — set end date to the same value as start date
- `anywhere` — respect submitted start date, set end date by adding `taskDuration - 1` working days
- `start-start` — use parent task start date as current task start date, set end date by adding `taskDuration - 1` working days
- `end-end` — use parent task end date as current task end date, set start date by subtracting `taskDuration + 1` working days
- `end-start` — set current task start date to parent task end date + 1 working day, then set end date by adding `taskDuration - 1` working days

Working-day math uses parameter key `workDaysPerWeek`.
Before calculation, dependency chain is validated to prevent infinite parent-task loops.

## ProjectTaskType entity payload (request/response)

- `projectTaskCode` (String)
- `projectTaskDescription` (String)
- `userTask` (Integer) — `1` show in user task type dropdown, `0` hide
- `editStartDate` (Integer) — `1` allow editing start date, `0` disallow
- `createByStream` (Integer) — `1` create by stream only, `0` create by task only
- `canDelete` (Integer) — `1` deletable, `0` protected
- `minimumDays` (Long)
- `maximumDays` (Long)
- `alignWith` (String) — e.g. `latest`, `anywhere`, `start-start`, `end-end`, `end-start`
- `inventoryType` (String) — e.g. `any`, `stock`, `asset`, `none`
- `manpowerRequired` (Integer) — `1` manpower required, `0` not required

## ProjectTaskType endpoints

- GET /api/projecttasktypes — list all project task types
- GET /api/projecttasktypes/{projectTaskCode} — get project task type by code
- POST /api/projecttasktypes — create project task type (body: ProjectTaskType)
- PUT /api/projecttasktypes/{projectTaskCode} — update project task type (body: ProjectTaskType)
- DELETE /api/projecttasktypes/{projectTaskCode} — delete project task type

## ProjectAsset endpoints

- GET /api/projectassets — list all project assets
- GET /api/projectassets/{id} — get project asset by id
- GET /api/projectassets/task/{projectTaskId} — list project assets by project task id
- POST /api/projectassets — create project asset
- PUT /api/projectassets/{id} — update project asset
- DELETE /api/projectassets/{id} — delete project asset

## ProjectSkill endpoints

- GET /api/projectskills — list all project skills
- GET /api/projectskills/{id} — get project skill by id
- GET /api/projectskills/task/{projectTaskId} — list project skills by project task id
- POST /api/projectskills — create project skill
- PUT /api/projectskills/{id} — update project skill
- DELETE /api/projectskills/{id} — delete project skill

## ProjectStreamAsset endpoints

- GET /api/projectstreamassets — list all project stream assets
- GET /api/projectstreamassets/{id} — get project stream asset by id
- GET /api/projectstreamassets/stream/{projectStreamId} — list project stream assets by project stream id
- POST /api/projectstreamassets — create project stream asset
- PUT /api/projectstreamassets/{id} — update project stream asset
- DELETE /api/projectstreamassets/{id} — delete project stream asset

## ProjectStreamBundle endpoints

- GET /api/projectstreambundles — list all project stream bundles
- GET /api/projectstreambundles/{id} — get project stream bundle by id
- GET /api/projectstreambundles/stream/{projectStreamId} — list project stream bundles by project stream id
- POST /api/projectstreambundles — create project stream bundle
- PUT /api/projectstreambundles/{id} — update project stream bundle
- DELETE /api/projectstreambundles/{id} — delete project stream bundle

## ProjectInventoryView endpoints

- GET /api/projectinventoryviews — list all inventory rows across task and stream inventory
- GET /api/projectinventoryviews?projectCode={projectCode} — filter by project code
- GET /api/projectinventoryviews?inventoryType={inventoryType} — filter by inventory type
- GET /api/projectinventoryviews?productId={productId} — filter by product id
- GET /api/projectinventoryviews?activityId={activityId} — filter by task/stream activity id
- GET /api/projectinventoryviews?projectCode={projectCode}&inventoryType={inventoryType}&productId={productId}&activityId={activityId} — combined filter
- GET /api/projectinventoryviews/{rowId} — get one inventory row by synthetic view id
- GET /api/projectinventoryviews/project/{projectCode} — list inventory rows for one project
- GET /api/projectinventoryviews/product/{productId} — list inventory rows for one product
- GET /api/projectinventoryviews/activity/{activityId} — list inventory rows for one task/stream activity id

## ProjectSkillView endpoints

- GET /api/projectskillviews — search project skill rows with optional filters: `projectCode`, `projectStreamId`, `projectTaskId`, `staffSkillId`, `taskStatus`, `active`, `projectStatus`
- GET /api/projectskillviews/{rowId} — get one project skill view row by synthetic row id
- GET /api/projectskillviews/project/{projectCode} — list project skill rows for one project
- GET /api/projectskillviews/task/{projectTaskId} — list project skill rows for one task
- GET /api/projectskillviews/skill/{staffSkillId} — list project skill rows for one staff skill id

## ProjectManpowerView endpoints

- GET /api/projectmanpowerviews — search project manpower rows with optional filters: `projectCode`, `projectStreamId`, `projectTaskId`, `staffId`, `taskStatus`, `active`, `projectStatus`
- GET /api/projectmanpowerviews/{rowId} — get one project manpower view row by synthetic row id
- GET /api/projectmanpowerviews/project/{projectCode} — list project manpower rows for one project
- GET /api/projectmanpowerviews/task/{projectTaskId} — list project manpower rows for one task
- GET /api/projectmanpowerviews/staff/{staffId} — list project manpower rows for one staff id

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

Generated from the codebase on June 13, 2026.
