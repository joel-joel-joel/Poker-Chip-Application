# Quick Start Guide - Poker Chips Application

## Getting Started in 5 Minutes

### Prerequisites
- Java 17+ installed
- Node.js 16+ installed
- Terminal/Command Line

---

## Step 1: Start the Backend (30 seconds)

```bash
cd backend
./mvnw spring-boot:run
```

Wait for: `Started PokerChipsApplication in X.XX seconds`

Backend will run on: http://localhost:8080

---

## Step 2: Start the Frontend (30 seconds)

Open a **NEW** terminal window:

```bash
cd frontend
npm run dev
```

Frontend will run on: http://localhost:5173

---

## Step 3: Open Your Browser

Navigate to: **http://localhost:5173**

---

## Step 4: Create Your First Account

1. Click **"Register here"**
2. Fill out the form:
   - Username: `player1`
   - Email: `player1@example.com`
   - Password: `password123`
3. Click **"Register"**

You'll be automatically logged in and redirected to the lobby!

---

## Step 5: Create a Poker Room

1. In the lobby, click **"Create Room"**
2. Fill out the form:
   - Room Name: `My First Game`
   - Max Players: `6`
   - Starting Chips: `1000`
3. Click **"Create Room"**

You'll automatically join and enter the room!

---

## Step 6: Invite Another Player (Optional)

### Option 1: Open Incognito Window
1. Open an incognito/private browser window
2. Go to http://localhost:5173
3. Register a new user (`player2`)
4. Join the room from the lobby

### Option 2: Share Room Code
- Your room code is displayed at the top of the room page
- Share it with friends to join

---

## Step 7: Start the Game

Once you have at least 2 players:

1. As the **host**, click **"Start Game"** in the room header
2. The poker table interface will appear
3. Players can now perform actions:
   - **Raise** - Increase the bet
   - **Call** - Match current bet
   - **Check** - Pass without betting
   - **Fold** - Forfeit your hand

---

## Features You Can Try

### In the Lobby:
- ✅ View all available rooms
- ✅ Create custom rooms
- ✅ Join existing rooms
- ✅ See player counts

### In the Game Room:
- ✅ View all players and chip balances
- ✅ Perform poker actions
- ✅ See real-time pot updates
- ✅ Adjust bet amounts with slider
- ✅ Leave room anytime

---

## Troubleshooting

### Backend won't start
**Error:** "Port 8080 already in use"
**Solution:** Kill the process using port 8080:
```bash
# Mac/Linux
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Frontend won't start
**Error:** "EADDRINUSE: address already in use"
**Solution:** Kill the process or change port in vite.config.js

### Can't connect to backend
**Check:**
1. Backend is running (check terminal)
2. Backend URL is correct in `frontend/.env`
3. No CORS errors in browser console

### Register fails
**Check:**
1. Backend is running
2. Database is connected
3. Check browser console for errors
4. Verify username/email aren't already taken

---

## Default Configuration

| Setting | Value |
|---------|-------|
| Backend URL | http://localhost:8080 |
| Frontend URL | http://localhost:5173 |
| Database | H2 (in-memory) |
| JWT Secret | (configured in application.properties) |
| WebSocket Endpoint | /ws |

---

## API Endpoints Reference

### Authentication
- POST `/api/auth/register` - Create account

### Rooms
- GET `/api/rooms/available` - List rooms
- POST `/api/rooms` - Create room
- POST `/api/rooms/{roomCode}/start` - Start game

### Game
- POST `/api/poker/action` - Perform poker action
- GET `/api/poker/round/{roundId}/state` - Get round state

---

## Keyboard Shortcuts

| Key | Action |
|-----|--------|
| R | Refresh lobby |
| Esc | Close modals |

---

## Tips

1. **Create rooms with descriptive names** - Makes it easier for players to find
2. **Set appropriate chip amounts** - 1000-5000 works well for casual games
3. **Use browser DevTools** - Check Network tab for API calls
4. **Check console logs** - WebSocket and API activity is logged

---

## Next Steps

After getting comfortable with the basics:

1. Try creating multiple rooms
2. Experiment with different player counts
3. Test poker actions (raise, fold, call)
4. Monitor real-time updates
5. Check the leaderboard

---

## Getting Help

**Check these files for more info:**
- `FRONTEND_SETUP.md` - Detailed frontend documentation
- `IMPLEMENTATION_SUMMARY.md` - Complete implementation details
- Backend README - Backend API documentation

**Common Issues:**
- Login not working? → Backend doesn't have login endpoint yet, use Register
- WebSocket errors? → Ignore for now, polling fallback works
- State not updating? → Refresh the page or check backend logs

---

## Development Mode

### Enable Hot Reload
Both frontend and backend support hot reload:
- Frontend: Automatic (Vite)
- Backend: Automatic with Spring DevTools

### View Logs
- Frontend: Browser Console (F12)
- Backend: Terminal where backend is running

### Database Console (H2)
Access at: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

---

## Production Deployment

For production deployment, see:
- Frontend: Build with `npm run build`
- Backend: Package with `./mvnw package`
- Deploy both to your hosting platform

---

**Enjoy playing poker!** 🎰♠️♥️♣️♦️
