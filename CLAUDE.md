# Poker Chips Application

Multiplayer poker game management system with real-time WebSocket updates, JWT authentication, room-based gameplay, and chip transaction tracking.

## Tech Stack

| Layer    | Technology                                                  |
|----------|-------------------------------------------------------------|
| Backend  | Java 24, Spring Boot 3.5.4, Spring Security, Spring Data JPA |
| Frontend | React 19, TypeScript 5.9, Vite 7.3, React Router 7         |
| Database | PostgreSQL 15 (Docker), H2 (testing)                        |
| Auth     | JWT (JJWT 0.11.5), BCrypt                                   |
| Realtime | STOMP over SockJS (Spring WebSocket)                        |
| UI       | Radix UI Themes + Slider                                    |

## Project Structure

```
backend/src/main/java/com/joelcode/pokerchipsapplication/
  config/         - Security, CORS, WebSocket, PasswordEncoder beans
  controller/     - REST endpoints (Auth, Room, RoomPlayer, PokerAction, ChipTransaction, User)
  service/        - Business logic; service/events/ for WebSocket event DTOs
  entities/       - JPA entities: User, Room, RoomPlayer, GameRound, ChipTransaction
  repositories/   - Spring Data JPA interfaces with custom @Query methods
  dto/request/    - Inbound DTOs with Jakarta validation
  dto/response/   - Outbound DTOs constructed from entities
  exceptions/     - GlobalExceptionHandler + custom/ domain exceptions
  security/       - JWT filter, token provider, entry point, UserPrincipal

frontend/src/
  components/     - App (router), ProtectedRoute, BetPanel, AuthErrorHandler, ui/
  pages/          - LoginPage, RegisterPage, LobbyPage, GameRoomPage
  services/       - api.ts (fetch wrapper), authService, roomService, gameService, websocket
  context/        - AuthContext (global auth state via React Context)
  hooks/          - useAuth, useWebSocket
  types/          - api.types.ts (all TypeScript interfaces/enums)
  utils/          - authEvents, token utilities
```

## Build & Run Commands

### Prerequisites
```bash
docker compose up -d                       # Start PostgreSQL on port 5433
```

### Backend (from `backend/`)
```bash
./mvnw spring-boot:run                     # Dev server on :8080
./mvnw test                                # Run tests
./mvnw clean package                       # Build JAR
```

### Frontend (from `frontend/`)
```bash
npm install                                # Install dependencies
npm run dev                                # Dev server on :5173
npm run build                              # Production build (tsc + vite)
npm run preview                            # Preview production build
```

### Environment Variables
- Backend: `backend/.env` (see `backend/.env.example`) — DB_USER, DB_PASSWORD, APP_JWT_SECRET, APP_JWT_EXPIRATION
- Frontend: `frontend/.env` (see `frontend/.env.example`) — VITE_API_URL (default `http://localhost:8080`)

## Key API Endpoints

| Method | Path                                | Auth | Purpose              |
|--------|-------------------------------------|------|----------------------|
| POST   | `/api/auth/register`                | No   | User registration    |
| POST   | `/api/auth/login`                   | No   | User login           |
| POST   | `/api/rooms`                        | Yes  | Create room          |
| GET    | `/api/rooms/available`              | Yes  | List available rooms |
| GET    | `/api/rooms/{roomCode}`             | Yes  | Room details         |
| POST   | `/api/rooms/{roomCode}/join`        | Yes  | Join room            |
| POST   | `/api/rooms/{roomCode}/start`       | Yes  | Start game           |
| POST   | `/api/poker/action`                 | Yes  | Poker action         |
| GET    | `/api/poker/room/{roomCode}/current-round` | Yes | Current round state |

## WebSocket Topics

- `/topic/room/{roomCode}` — Room updates (player joins, game start/end)
- `/topic/round/{roundId}` — Round state changes (poker actions, pot updates)
- Endpoint: `/ws` (SockJS)

## Database

- PostgreSQL on port **5433** (mapped from container 5432)
- Hibernate `ddl-auto=update` — schema auto-migrated
- UUID primary keys on all entities (`GenerationType.UUID`)
- Entities: `User` -> `RoomPlayer` <- `Room` -> `GameRound` -> `ChipTransaction`

## Testing

Backend tests: `backend/src/test/` — run with `./mvnw test`
Manual API testing guide: `backend/TESTING_GUIDE.md`

## Additional Documentation

When working on specific areas, check these files for detailed patterns and conventions:

| File | When to check |
|------|---------------|
| `.claude/docs/architectural_patterns.md` | Modifying backend services, adding controllers, frontend state/API patterns |
| `QUICK_START.md` | First-time setup |
| `backend/TESTING_GUIDE.md` | Manual API testing with curl |
| `IMPLEMENTATION_SUMMARY.md` | Understanding Phase 1 & 2 feature scope |
| `frontend/TROUBLESHOOTING.md` | Debugging frontend issues |
