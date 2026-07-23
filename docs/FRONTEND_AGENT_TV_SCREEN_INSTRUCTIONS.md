# Frontend Agent Instructions: TV QR + PIN Screen Mode

Implement TV bootstrap and approved screen flow using backend TV auth framework.

## Goal

- TV browser shows QR + 4-digit PIN.
- App user scans QR and approves in mobile app.
- TV exchanges one-time code for JWT and starts screen mode.
- Approved screen shows **list of project codes**.

## Backend endpoints

- `POST /api/tv-auth/session` (public)
- `GET /api/tv-auth/session/{sessionCode}/status` (public)
- `POST /api/tv-auth/exchange` (public)
- `POST /api/tv-auth/approve` (mobile app, authenticated BMP user)

## Timestamp contract

- All TV auth timestamps returned by backend are ISO 8601 strings with an explicit timezone offset.
- Example: `2026-07-23T10:30:00+08:00`
- Frontend must parse them as zoned timestamps and must not assume local naive datetime strings.

## TV Browser Flow

1. On `/tv/bootstrap` load:

- Call `POST /api/tv-auth/session`.
- Render `qrPayload` as QR code.
- Render `pin` visibly on screen.
- Show challenge countdown from `challengeExpiresAt`.

2. Poll status:

- Poll `GET /api/tv-auth/session/{sessionCode}/status` every `pollIntervalSeconds`.
- If `status === APPROVED` and `exchangeCode` present, call exchange.
- If `status === EXPIRED`, restart by creating a new session.

3. Exchange:

- Call `POST /api/tv-auth/exchange` with `{ exchangeCode }`.
- Persist returned `token` in memory only (avoid localStorage if possible).
- Navigate to `destinationUrl` from response.
- Use JWT as `Authorization: Bearer <token>` for protected APIs.

4. Approved screen (`/tv/projects`):

- Initially render `projectCodes` from exchange response.
- Auto-refresh data every `refreshIntervalSeconds`.
- If any protected API returns `401`, redirect to `/tv/bootstrap`.

## Mobile App Approval Flow

1. QR scan decodes `sessionCode` from `qrPayload`.
2. Prompt user to input PIN shown on TV.
3. Call authenticated `POST /api/tv-auth/approve`:

- `{ sessionCode, pin, destinationUrl: "/tv/projects" }`

4. Show success/failure feedback.

## UI Requirements

- TV bootstrap screen must be simple and visible from distance.
- Large QR, large PIN, clear expiry timer.
- Clear retry button if session expires.
- On approved screen, show project code list with last refresh timestamp.

## Security Rules

- Do not place JWT token in URL.
- Do not log JWT in console.
- Handle expired/invalid session by resetting to bootstrap.
- Keep exchange one-time (already enforced backend-side).

## Minimal payload typings

- `TvSessionCreateResponse`: `{ sessionCode, pin, qrPayload, challengeExpiresAt, pollIntervalSeconds }`
- `TvSessionStatusResponse`: `{ sessionCode, status, exchangeCode, destinationUrl, challengeExpiresAt, sessionExpiresAt }`
- `TvSessionExchangeResponse`: `{ token, destinationUrl, sessionExpiresAt, refreshIntervalSeconds, projectCodes }`
