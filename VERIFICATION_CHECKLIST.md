# Frontend-Backend Integration Verification Checklist

Use this checklist to verify that all frontend-backend integrations are working correctly.

---

## ✅ Pre-Flight Checks

### Backend Running
- [ ] Backend starts without errors
- [ ] Backend is accessible at http://localhost:8080
- [ ] H2 console accessible at http://localhost:8080/h2-console
- [ ] No port conflicts
- [ ] Database tables created successfully

### Frontend Running
- [ ] Frontend starts without errors
- [ ] Frontend is accessible at http://localhost:5173
- [ ] No build/compile errors
- [ ] Environment variables loaded (.env file)
- [ ] All dependencies installed

---

## 🔐 Authentication Tests

### Registration Flow
- [ ] Navigate to /register page
- [ ] Form displays correctly
- [ ] Username validation works (try existing username)
- [ ] Email validation works (try existing email)
- [ ] Password requirements enforced (min 6 characters)
- [ ] Confirm password validation works
- [ ] Successful registration shows no errors
- [ ] JWT token stored in localStorage after registration
- [ ] User automatically logged in after registration
- [ ] Redirected to /lobby after successful registration
- [ ] User data appears in header

**API Call:** `POST /api/auth/register`
**Expected Response:**
```json
{
  "token": "eyJ...",
  "tokenType": "Bearer",
  "username": "testuser",
  "email": "test@example.com"
}
```

### Login Flow (Limited)
- [ ] Navigate to /login page
- [ ] Warning message displays about missing login endpoint
- [ ] Link to register page works

**Note:** Login endpoint not yet implemented in backend.

### Authentication State
- [ ] AuthContext provides user data
- [ ] isAuthenticated is true after login
- [ ] User info displays in header
- [ ] Logout button works
- [ ] Token cleared from localStorage on logout
- [ ] Redirected to /login after logout

---

## 🏠 Lobby Page Tests

### Navigation
- [ ] Can navigate to /lobby when authenticated
- [ ] Redirected to /login when not authenticated
- [ ] Header displays correctly with username
- [ ] Logout button visible and functional

### Room List Display
- [ ] Rooms load on page mount
- [ ] Loading state shows while fetching
- [ ] Empty state shows when no rooms
- [ ] Room cards display with correct data:
  - [ ] Room name
  - [ ] Room code
  - [ ] Player count (current/max)
  - [ ] Starting chips
  - [ ] Status badge (color-coded)
- [ ] Multiple rooms display in grid
- [ ] Refresh button updates room list

**API Call:** `GET /api/rooms/available`
**Expected Response:**
```json
[
  {
    "id": "uuid",
    "roomCode": "ABC123",
    "name": "Test Room",
    "hostId": "uuid",
    "maxPlayers": 6,
    "startingChips": 1000,
    "status": "WAITING",
    "currentPlayerCount": 0,
    "createdAt": "2026-02-01T..."
  }
]
```

### Create Room
- [ ] "Create Room" button shows/hides form
- [ ] Create form displays correctly
- [ ] Room name input works
- [ ] Max players input works (2-10 range)
- [ ] Starting chips input works
- [ ] Form validation works
- [ ] Create button submits form
- [ ] Room created successfully
- [ ] User automatically joins created room
- [ ] Navigated to /room/{roomCode}
- [ ] Error handling works (try invalid data)

**API Calls:**
1. `POST /api/rooms` - Create room
2. `POST /api/room-players/join/{roomCode}` - Join room

### Join Room
- [ ] "Join Room" button visible on room cards
- [ ] Button disabled when room is full
- [ ] Button disabled when game in progress
- [ ] Join successful for available rooms
- [ ] Navigated to /room/{roomCode}
- [ ] Error message shows if join fails

**API Call:** `POST /api/room-players/join/{roomCode}`

---

## 🎮 Game Room Page Tests

### Room Display
- [ ] Room name displays in header
- [ ] Room code displays in header
- [ ] Player count displays correctly
- [ ] Leave room button visible
- [ ] Start game button visible (host only)
- [ ] Start game button hidden (non-host)

### Players List
- [ ] All players display in grid
- [ ] Player usernames show correctly
- [ ] Chip balances display
- [ ] Current user highlighted differently
- [ ] Eliminated status shows (if applicable)
- [ ] Player list updates in real-time

**API Call:** `GET /api/room-players/room/{roomCode}`

### Room State
- [ ] Room status displays correctly
- [ ] WAITING status shows "Waiting for host..."
- [ ] IN_PROGRESS status shows poker table
- [ ] Correct UI for each status

**API Call:** `GET /api/rooms/{roomCode}`

### Start Game (Host Only)
- [ ] Start game button enabled for host
- [ ] Start game button disabled for non-host
- [ ] Click starts game successfully
- [ ] Room status changes to IN_PROGRESS
- [ ] Poker interface appears
- [ ] Error handling works

**API Call:** `POST /api/rooms/{roomCode}/start`

### Leave Room
- [ ] Leave button works
- [ ] Player removed from room
- [ ] Navigated back to /lobby
- [ ] Error handling works

**API Call:** `DELETE /api/room-players/room/{roomId}`

---

## 🎰 Poker Game Tests

### Game Interface Display
- [ ] BetPanel displays when game active
- [ ] Action buttons display
- [ ] Round info displays
- [ ] All UI elements visible

### BetPanel Integration
- [ ] Your bet displays correctly
- [ ] Current bet displays correctly
- [ ] Pot displays correctly
- [ ] Bet slider works
- [ ] Slider min/max values correct
- [ ] Slider updates bet amount
- [ ] Bet amount syncs with slider

**API Call:** `GET /api/poker/round/{roundId}/state`
**Expected Response:**
```json
{
  "round": {
    "id": "uuid",
    "roomId": "uuid",
    "roundNumber": 1,
    "pot": 100,
    "currentBet": 20,
    "status": "ACTIVE"
  },
  "playerBets": [
    {
      "playerId": "uuid",
      "username": "player1",
      "currentBet": 20,
      "chipBalance": 980,
      "hasFolded": false
    }
  ]
}
```

### Current Round
- [ ] Round loads when game starts
- [ ] Round number displays
- [ ] Pot amount displays
- [ ] Current bet displays
- [ ] No round shows placeholder when none active

**API Call:** `GET /api/poker/room/{roomCode}/current-round`

### Poker Actions

#### RAISE Action
- [ ] Raise button visible
- [ ] Raise button disabled when appropriate
- [ ] Click sends action to backend
- [ ] Bet amount included in request
- [ ] Round state updates after action
- [ ] Error handling works

#### CALL Action
- [ ] Call button visible
- [ ] Call button disabled when appropriate
- [ ] Click sends action to backend
- [ ] Round state updates after action

#### CHECK Action
- [ ] Check button visible
- [ ] Check button disabled when bet required
- [ ] Click sends action to backend
- [ ] Round state updates after action

#### FOLD Action
- [ ] Fold button visible
- [ ] Click sends action to backend
- [ ] Player marked as folded
- [ ] Round state updates

**API Call:** `POST /api/poker/action`
**Request Body:**
```json
{
  "roundId": "uuid",
  "playerId": "uuid",
  "action": "RAISE",
  "amount": 50
}
```

---

## 🔴 WebSocket Tests

### Connection
- [ ] WebSocket connects on page load
- [ ] Connection visible in browser console
- [ ] No connection errors
- [ ] Reconnects on disconnect

### Room Subscriptions
- [ ] Subscribe to room topic on room page
- [ ] Room updates received
- [ ] UI updates when message received
- [ ] Unsubscribe on page leave

**WebSocket Topic:** `/topic/room/{roomCode}`

### Round Subscriptions
- [ ] Subscribe to round topic when round active
- [ ] Round updates received
- [ ] Pot updates in real-time
- [ ] Player actions appear instantly
- [ ] Unsubscribe when round ends

**WebSocket Topic:** `/topic/round/{roundId}`

---

## 🌐 API Service Tests

### Base API Client
- [ ] GET requests work
- [ ] POST requests work
- [ ] PUT requests work (if used)
- [ ] DELETE requests work
- [ ] JWT token automatically included in headers
- [ ] Error handling works (400, 401, 404, 500)
- [ ] 401 errors redirect to login
- [ ] Network errors handled gracefully

### Token Management
- [ ] Token stored after registration
- [ ] Token retrieved for API calls
- [ ] Token cleared on logout
- [ ] Token included in Authorization header
- [ ] Token persists across page refreshes

### Error Handling
- [ ] Network errors show user-friendly messages
- [ ] API errors display in UI
- [ ] Console logs errors for debugging
- [ ] App doesn't crash on errors

---

## 🎨 UI/UX Tests

### Styling
- [ ] Dark theme applied correctly
- [ ] Poker table green color (#177121)
- [ ] Radix UI components styled
- [ ] Responsive on different screen sizes
- [ ] Buttons have hover effects
- [ ] Forms look professional

### Navigation
- [ ] React Router working
- [ ] Page transitions smooth
- [ ] Browser back/forward buttons work
- [ ] Direct URL navigation works
- [ ] 404 handling works

### Loading States
- [ ] Loading messages show while fetching
- [ ] Skeleton screens or spinners (if implemented)
- [ ] No blank screens during loads

### Error Messages
- [ ] Errors display clearly
- [ ] Error messages dismissible
- [ ] Appropriate error colors (red)
- [ ] Validation errors show on forms

---

## 🧪 Multi-User Tests

### Two Users, One Room
- [ ] User A creates room
- [ ] User B joins room (incognito/different browser)
- [ ] Both users see each other in player list
- [ ] User A starts game
- [ ] Both users see game start
- [ ] User A performs action
- [ ] User B sees action update
- [ ] Chip balances sync between users

### Room Capacity
- [ ] Room with max 2 players fills up
- [ ] Third user cannot join full room
- [ ] Join button disabled for full rooms

### Host Permissions
- [ ] Only host can start game
- [ ] Only host sees start button
- [ ] Non-host sees waiting message

---

## 📊 Data Validation Tests

### Frontend Validation
- [ ] Username: min 3 characters
- [ ] Email: valid email format
- [ ] Password: min 6 characters
- [ ] Confirm password: matches password
- [ ] Room name: required
- [ ] Max players: 2-10 range
- [ ] Starting chips: positive number

### Backend Validation
- [ ] Duplicate username rejected
- [ ] Duplicate email rejected
- [ ] Invalid poker actions rejected
- [ ] Insufficient chips handled
- [ ] Invalid room codes handled

---

## 🔍 Browser Console Checks

### No Errors
- [ ] No React errors
- [ ] No TypeScript errors
- [ ] No 404 errors for resources
- [ ] No CORS errors
- [ ] No WebSocket errors (except expected)

### Logging
- [ ] API calls logged
- [ ] WebSocket messages logged
- [ ] Auth state changes logged
- [ ] Error details logged

---

## 📱 Cross-Browser Testing

### Chrome
- [ ] All features work
- [ ] No console errors

### Firefox
- [ ] All features work
- [ ] No console errors

### Safari
- [ ] All features work
- [ ] No console errors

### Edge
- [ ] All features work
- [ ] No console errors

---

## 🚀 Performance Checks

### Load Times
- [ ] Initial page load < 3 seconds
- [ ] Room list loads < 1 second
- [ ] Room state loads < 1 second
- [ ] Action response < 500ms

### Real-time Updates
- [ ] WebSocket messages < 100ms
- [ ] UI updates immediately
- [ ] No lag or freezing

---

## 📋 Final Verification

### Complete User Journey
1. [ ] Register new account
2. [ ] See lobby with rooms
3. [ ] Create a room
4. [ ] See room page
5. [ ] Open incognito, register second user
6. [ ] Second user joins room
7. [ ] First user starts game
8. [ ] Both see poker interface
9. [ ] Both perform poker actions
10. [ ] See real-time updates
11. [ ] Leave room
12. [ ] Return to lobby
13. [ ] Logout

### Documentation
- [ ] README.md exists and is accurate
- [ ] QUICK_START.md exists and is accurate
- [ ] FRONTEND_SETUP.md exists and is accurate
- [ ] IMPLEMENTATION_SUMMARY.md exists and is accurate
- [ ] Code comments are clear

---

## 🐛 Known Issues Verification

Verify these known issues are documented:
- [ ] Login endpoint missing (only register works)
- [ ] WebSocket may need backend broadcasting setup
- [ ] Round auto-start not implemented

---

## ✨ Success Criteria

### Minimum Viable Product
- [x] User registration works
- [x] Room creation works
- [x] Room joining works
- [x] Game starting works
- [x] Poker actions work
- [x] Real-time updates work (via WebSocket or polling)
- [x] UI is functional and usable

### Quality Standards
- [ ] No critical bugs
- [ ] No console errors in normal flow
- [ ] All core features work end-to-end
- [ ] Documentation is complete
- [ ] Code is clean and organized

---

## 📝 Test Results

Date: ________________

Tester: ________________

Overall Status: ☐ PASS   ☐ FAIL

Critical Issues Found:
1. ___________________________________
2. ___________________________________
3. ___________________________________

Notes:
_____________________________________
_____________________________________
_____________________________________

---

**Next Steps After Verification:**
1. Fix any critical issues found
2. Add Phase 3 features (transaction history, profiles, etc.)
3. Improve UI/UX based on testing
4. Add comprehensive error handling
5. Optimize performance
6. Prepare for production deployment
