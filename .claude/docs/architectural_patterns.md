# Architectural Patterns & Conventions

Patterns observed across multiple files in this codebase. Reference this when adding new features to stay consistent.

---

## Backend Patterns

### Three-Tier Layering: Controller → Service → Repository

Every domain follows the same structure:
- **Controller** (`@RestController`): HTTP mapping, request validation, delegates to service
- **Service** (`@Service`, `@Transactional`): Business logic, exception throwing, WebSocket broadcasting
- **Repository** (extends `JpaRepository`): Data access with optional `@Query` methods

Examples: `RoomController:22` → `RoomService:27` → `RoomRepo:19`; `AuthController:13` → `UserService:16` → `UserRepo:13`

### Dependency Injection

Two styles coexist — constructor injection (preferred in controllers) and field injection (`@Autowired`):
- Constructor injection: `RoomController:33-35`
- Field injection: `AuthController:18-19`
- Optional dependency: `RoomService:31` uses `@Autowired(required = false)` for `SimpMessagingTemplate`

When adding new code, prefer constructor injection.

### DTO Request/Response Separation

Inbound DTOs live in `dto/request/`, outbound in `dto/response/`. All request DTOs include:
- No-arg constructor + all-arg constructor
- Jakarta validation annotations (`@NotBlank`, `@Size`, `@Email`)
- Getter/setter methods (no Lombok)

Response DTOs construct from entities, often with computed fields:
- `RoomResponse:32-46` — overloaded constructors, derives `currentPlayerCount` from collection
- `AuthResponse:1-30` — wraps token + user info

### Entity Conventions

All JPA entities follow:
- UUID primary keys: `@GeneratedValue(strategy = GenerationType.UUID)` — see `User:28`, `Room:29`
- `@CreationTimestamp` for audit fields — `User:40-41`
- `@JsonIgnore` on collection sides to prevent circular serialization — `User:46`
- `@JsonProperty` for custom JSON field names — `Room:33`, `Room:47`
- Enum fields for status tracking — `Room:41` with `RoomStatus` enum at `Room:155-157`
- `FetchType.LAZY` on `@ManyToOne` relations — `Room:36-38`

### Global Exception Handling

Centralized in `GlobalExceptionHandler:1-94` using `@RestControllerAdvice`:
- Each custom exception maps to a specific HTTP status (404, 409, 400, 401)
- Custom exceptions in `exceptions/custom/` extend `RuntimeException`
- Validation errors aggregated from `MethodArgumentNotValidException:66-81`
- Standardized `ErrorResponse` structure: message, status, timestamp

To add a new exception: create class in `exceptions/custom/`, add handler method in `GlobalExceptionHandler`.

### JWT Security Flow

Authentication chain: `SecurityConfig:24-68` → `JwtAuthenticationFilter` → `JwtTokenProvider`
- Stateless sessions (`SessionCreationPolicy.STATELESS`) — `SecurityConfig:55`
- Public endpoints listed at `SecurityConfig:58-60`: `/api/auth/**`, `/ws/**`
- Token contains: subject (userId), claims (username, email) — `JwtTokenProvider:25-38`
- HS512 signing algorithm — `JwtTokenProvider:36`

### WebSocket Broadcasting

Services broadcast state changes via `SimpMessagingTemplate`:
- `RoomService:120-124` — sends game events to `/topic/room/{roomCode}`
- `RoomPlayerService:66-69` — broadcasts player join events
- Event DTOs in `service/events/`: `GameEvent`, `GameEndedEvent`, `PlayerEvent`

Pattern: mutate state in `@Transactional` method, then broadcast via template.

### Repository Queries

- Simple queries: derive from method name (`findByUsername`, `findByRoomCode`)
- Complex queries: `@Query` with JPQL — `RoomRepo:34-44`
- Mutations: `@Modifying @Transactional` — `RoomRepo:50-52`
- Parameter binding via `@Param`
- Collection functions in JPQL: `SIZE()` for checking capacity — `RoomRepo:36`

---

## Frontend Patterns

### Singleton API Client

`services/api.ts:37-212` — centralized fetch wrapper exported as singleton (line 212):
- All HTTP methods (GET, POST, PUT, DELETE) with typed responses
- Auto-injects auth token via `buildHeaders():62-75`
- 401 responses trigger `authEvents.emit()` for global logout — `api.ts:82-85`
- Custom `ApiError` class extends `Error` with status and data — `api.ts:15-24`

### Domain Service Layer

Each API domain gets its own service file wrapping `apiClient`:
- `authService.ts` — login/register + token storage
- `roomService.ts` — room CRUD, uses `requiresAuth: true`
- `gameService.ts` — poker actions, round state; handles 404 gracefully for optional resources (lines 56-64)

Pattern: service methods return typed promises, handle domain-specific edge cases, throw for UI layer to catch.

### React Context for Auth Only

Auth state managed via Context API (`AuthContext.tsx:6-98`):
- Provider wraps entire app at `main.tsx`
- Exposes: `user`, `isAuthenticated`, `isLoading`, `login()`, `logout()`, `refreshUser()`
- `useAuth` hook (`hooks/useAuth.ts:9-17`) validates context availability

All other state is component-local (no Redux, no global store for game state).

### Page Component Pattern

Every page follows the same structure:
1. State declarations: form data, loading, error, domain data
2. `useEffect` for initial data load / auth redirect
3. Handler functions for user actions (form submit, button clicks)
4. Conditional rendering: loading spinner → error message → content

Examples: `LoginPage.tsx:7-122`, `LobbyPage.tsx:12-239`, `GameRoomPage.tsx:24-311`

### Protected Routes

`ProtectedRoute.tsx:14-31` — wrapper component:
- Shows loading state while auth initializes
- Redirects to `/login` with `location` state for post-login return
- Wraps protected pages in `App.tsx` router config

### WebSocket Hook

`useWebSocket.ts:11-59` — manages connection lifecycle:
- Single connection attempt via ref (line 13)
- Returns subscription helpers for room and round topics
- Cleanup on component unmount

Underlying service: `websocket.ts:12-165` — singleton STOMP client over SockJS with subscription map tracking.

### TypeScript Type Definitions

All types centralized in `types/api.types.ts:1-239`:
- Interfaces mirror backend DTOs (e.g., `UserDTO`, `RoomDTO`, `AuthResponse`)
- Enums for all status/action constants: `RoomStatus`, `PokerAction`, `RoundStatus`, `TransactionType`
- Generic `PageResponse<T>` for paginated endpoints (lines 218-226)

When adding a new endpoint: add types here first, then service method, then UI.

---

## Cross-Cutting Conventions

### Error Handling Flow

Backend: throw domain exception → `GlobalExceptionHandler` maps to HTTP status + `ErrorResponse`
Frontend: `apiClient` catches non-2xx → creates `ApiError` → service/page catches → stores in component `error` state → renders red error div

401 errors bypass normal flow: `api.ts` emits auth event → `AuthErrorHandler` listens → triggers logout.

### Adding a New Feature Checklist

1. **Entity** — new JPA entity in `entities/` with UUID PK, timestamps
2. **Repository** — interface in `repositories/` extending `JpaRepository`
3. **DTOs** — request in `dto/request/` with validation, response in `dto/response/`
4. **Service** — business logic in `service/`, `@Transactional`, throw custom exceptions
5. **Controller** — REST endpoints in `controller/`, delegate to service
6. **Exception** — if needed, add to `exceptions/custom/` + handler in `GlobalExceptionHandler`
7. **Frontend types** — add interfaces/enums to `types/api.types.ts`
8. **Frontend service** — add methods to existing or new service file
9. **Frontend page/component** — follow page component pattern above
10. **WebSocket** — if real-time needed, add topic + broadcast in service + subscription in hook
