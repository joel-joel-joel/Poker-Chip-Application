# Poker Chips Application - Frontend Setup Guide

## Overview

This guide explains how to set up and run the newly integrated frontend application that connects to the existing backend API.

## What's Been Implemented

### Phase 1: Foundation ✅
- ✅ **API Service Layer** - Base HTTP client with JWT token management
- ✅ **Authentication Service** - Registration and user management
- ✅ **TypeScript Types** - Complete DTO interfaces matching backend
- ✅ **Authentication Context** - React context for global auth state
- ✅ **Token Management** - localStorage-based JWT token handling

### Phase 2: Core Features ✅
- ✅ **React Router** - Multi-page navigation with protected routes
- ✅ **Login/Register Pages** - User authentication UI
- ✅ **Room Service** - Room management API integration
- ✅ **Game Service** - Poker action API integration
- ✅ **Lobby Page** - Room browser and creation
- ✅ **Game Room Page** - Live poker table interface
- ✅ **BetPanel Integration** - Connected to backend game state
- ✅ **WebSocket Integration** - Real-time game updates

## Architecture

```
frontend/src/
├── components/
│   ├── ui/              # Reusable UI components
│   │   ├── button.tsx
│   │   └── slider.tsx
│   ├── App.tsx          # Router configuration
│   ├── BetPanel.tsx     # Poker betting panel (integrated)
│   └── ProtectedRoute.tsx
├── pages/
│   ├── LoginPage.tsx
│   ├── RegisterPage.tsx
│   ├── LobbyPage.tsx
│   └── GameRoomPage.tsx
├── services/
│   ├── api.ts           # Base API client
│   ├── authService.ts   # Authentication APIs
│   ├── roomService.ts   # Room management APIs
│   ├── gameService.ts   # Poker game APIs
│   └── websocket.ts     # WebSocket service
├── context/
│   └── AuthContext.tsx  # Global auth state
├── hooks/
│   ├── useAuth.ts
│   └── useWebSocket.ts
├── types/
│   └── api.types.ts     # TypeScript interfaces
└── utils/
    └── token.ts         # JWT token utilities
```

## Prerequisites

- Node.js (v16 or higher)
- npm or yarn
- Backend server running on `http://localhost:8080`

## Installation

1. **Navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Configure environment variables:**

   The `.env` file has been created with default settings:
   ```
   VITE_API_URL=http://localhost:8080
   ```

   Update this if your backend runs on a different URL.

## Running the Application

1. **Start the backend server first:**
   ```bash
   # In the backend directory
   ./mvnw spring-boot:run
   ```

2. **Start the frontend dev server:**
   ```bash
   # In the frontend directory
   npm run dev
   ```

3. **Open your browser:**
   Navigate to `http://localhost:5173` (or the URL shown in terminal)

## User Flow

### 1. Registration
- Navigate to `/register`
- Enter username, email, and password
- Form validates username/email availability via API
- On success, automatically logs in and redirects to lobby

### 2. Lobby
- View available rooms
- Create a new room with custom settings:
  - Room name
  - Max players (2-10)
  - Starting chips
- Join existing rooms
- Real-time room list updates

### 3. Game Room
- View all players and their chip balances
- Host can start the game
- Once started, poker interface appears:
  - **BetPanel** - Shows your bet, current bet, and total pot
  - **Action Buttons** - Raise, Fold, Call, Check
  - **Player List** - Live chip balances
- Real-time updates via WebSocket
- Leave room at any time

## API Endpoints Used

### Authentication
- `POST /api/auth/register` - Create account
- `GET /api/auth/check-username` - Validate username
- `GET /api/auth/check-email` - Validate email
- `GET /api/user/me` - Get current user

### Room Management
- `GET /api/rooms/available` - List joinable rooms
- `POST /api/rooms` - Create new room
- `GET /api/rooms/{roomCode}` - Get room details
- `GET /api/rooms/{roomCode}/state` - Get room state
- `POST /api/rooms/{roomCode}/start` - Start game (host only)
- `POST /api/room-players/join/{roomCode}` - Join room
- `DELETE /api/room-players/room/{roomId}` - Leave room
- `GET /api/room-players/room/{roomCode}` - Get players

### Poker Game
- `GET /api/poker/room/{roomCode}/current-round` - Get active round
- `GET /api/poker/round/{roundId}/state` - Get round state
- `POST /api/poker/action` - Perform poker action (BET/CALL/RAISE/CHECK/FOLD)

### WebSocket Topics
- `/topic/room/{roomCode}` - Room updates
- `/topic/round/{roundId}` - Round updates

## Known Limitations

### 1. Login Endpoint Missing
The backend currently only has a **register** endpoint (`/api/auth/register`). There is no separate **login** endpoint.

**Current behavior:**
- `/login` page exists but shows a warning that login is not implemented
- Users must use `/register` to create an account and get a token

**Recommendation:**
Add a `POST /api/auth/login` endpoint to the backend:
```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    // Validate username/password
    // Generate JWT token
    // Return AuthResponse
}
```

### 2. WebSocket Topics
The WebSocket service is implemented but the backend needs to broadcast updates to topics:
- `/topic/room/{roomCode}` - When room state changes
- `/topic/round/{roundId}` - When round state changes

### 3. Poker Round Starting
The game room page currently doesn't automatically start poker rounds. You may need to add a "Start Round" button or have the backend auto-start rounds when game begins.

## Troubleshooting

### "Network error" or "Failed to fetch"
- Ensure backend is running on `http://localhost:8080`
- Check browser console for CORS errors
- Verify `VITE_API_URL` in `.env` file

### "Unauthorized" or redirected to login
- JWT token may have expired
- Try logging out and registering again
- Check browser localStorage for `poker_auth_token`

### WebSocket not connecting
- Verify backend has WebSocket endpoint at `/ws`
- Check browser console for connection errors
- Ensure SockJS is properly configured on backend

### Room not loading
- Check that backend database is running
- Verify room exists in database
- Check backend logs for errors

## Next Steps

### Recommended Enhancements

1. **Add Login Endpoint** - Implement separate login in backend
2. **Transaction History Page** - Display chip transaction logs
3. **Player Profile Page** - View stats, update settings
4. **Leaderboard Page** - Show top players
5. **Error Boundaries** - Better error handling in React
6. **Loading States** - Skeleton loaders for better UX
7. **Notifications** - Toast messages for actions
8. **Game Rules Display** - Help section for poker rules
9. **Sound Effects** - Audio feedback for actions
10. **Mobile Responsiveness** - Optimize for mobile devices

### Advanced Features

- **Tournament Mode** - Multi-round tournaments
- **Private Rooms** - Password-protected rooms
- **Spectator Mode** - Watch games without playing
- **Chat System** - In-game messaging
- **Hand History** - View past hands
- **Replay System** - Review completed games
- **Admin Dashboard** - Monitor all rooms and players

## Testing

### Manual Testing Checklist

- [ ] Register new account
- [ ] Create a room
- [ ] Join a room
- [ ] View room details
- [ ] Start game (as host)
- [ ] Perform poker actions
- [ ] Leave room
- [ ] Logout
- [ ] Register with existing username (should fail)
- [ ] Join full room (should fail)

### API Testing
You can test API endpoints directly using curl or Postman:

```bash
# Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

# Get available rooms (requires token)
curl -X GET http://localhost:8080/api/rooms/available \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Development Tips

1. **Hot Reload** - Frontend automatically reloads on file changes
2. **React DevTools** - Install browser extension for debugging
3. **Network Tab** - Monitor API calls in browser DevTools
4. **Console Logs** - WebSocket and API calls are logged to console
5. **TypeScript** - Use VS Code for type checking and autocomplete

## Support

For issues or questions:
1. Check backend logs for API errors
2. Check browser console for frontend errors
3. Verify all dependencies are installed
4. Ensure backend and frontend versions match

---

**Status:** Phase 1 & 2 Complete ✅
**Next Phase:** Enhanced features and polish
