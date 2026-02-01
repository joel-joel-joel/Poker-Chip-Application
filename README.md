# Poker Chips Application

A full-stack poker chip management application with real-time multiplayer support built with Spring Boot and React.

## Overview

This application allows users to create poker rooms, manage chip balances, and play poker games with real-time updates. It features a complete REST API backend and a modern React frontend with WebSocket support.

## Features

### Authentication & User Management
- User registration with username/email validation
- JWT token-based authentication
- User profile management

### Room Management
- Create custom poker rooms
- Join/leave rooms
- Room status tracking (WAITING, IN_PROGRESS, FINISHED)
- Player count limits
- Customizable starting chip amounts

### Poker Game
- Full poker action support (BET, CALL, RAISE, CHECK, FOLD, ALL_IN)
- Real-time game state updates
- Pot management
- Round tracking
- Player elimination

### Transaction System
- Complete chip transaction history
- Transaction types: BET, CALL, RAISE, WIN, TRANSFER, etc.
- Player statistics
- Room activity analytics

### Real-time Updates
- WebSocket integration for live game updates
- Room state synchronization
- Player action notifications

## Tech Stack

### Backend
- **Framework:** Spring Boot 3.x
- **Language:** Java 17
- **Database:** H2 (in-memory) / PostgreSQL (production)
- **Security:** Spring Security with JWT
- **WebSocket:** STOMP over SockJS
- **Build Tool:** Maven

### Frontend
- **Framework:** React 19
- **Language:** TypeScript
- **Routing:** React Router v7
- **UI Library:** Radix UI
- **State Management:** React Context
- **HTTP Client:** Fetch API
- **WebSocket:** SockJS + STOMP
- **Build Tool:** Vite

## Project Structure

```
PokerChipsApplication/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/joelcode/pokerchipsapplication/
│   │       ├── controller/    # REST API controllers
│   │       ├── service/       # Business logic
│   │       ├── model/         # Entity models
│   │       ├── repository/    # JPA repositories
│   │       ├── dto/           # Data transfer objects
│   │       ├── security/      # JWT & security config
│   │       └── websocket/     # WebSocket config
│   └── pom.xml
├── frontend/                   # React frontend
│   ├── src/
│   │   ├── components/        # React components
│   │   ├── pages/             # Page components
│   │   ├── services/          # API services
│   │   ├── context/           # React contexts
│   │   ├── hooks/             # Custom hooks
│   │   ├── types/             # TypeScript types
│   │   └── utils/             # Utility functions
│   └── package.json
├── QUICK_START.md             # Quick start guide
├── FRONTEND_SETUP.md          # Frontend documentation
└── IMPLEMENTATION_SUMMARY.md  # Implementation details
```

## Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- Maven (or use included wrapper)
- npm or yarn

### Backend Setup

1. Navigate to backend directory:
   ```bash
   cd backend
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

   Backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start development server:
   ```bash
   npm run dev
   ```

   Frontend will start on `http://localhost:5173`

### First Use

1. Open browser to `http://localhost:5173`
2. Register a new account
3. Create a poker room
4. Invite friends or open incognito window to join with another user
5. Start playing!

See [QUICK_START.md](QUICK_START.md) for detailed instructions.

## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | No |
| GET | `/api/auth/check-username` | Check username availability | No |
| GET | `/api/auth/check-email` | Check email availability | No |

### Room Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/rooms` | Create new room | Yes |
| GET | `/api/rooms/available` | List available rooms | No |
| GET | `/api/rooms/{roomCode}` | Get room details | No |
| GET | `/api/rooms/{roomCode}/state` | Get room state | No |
| POST | `/api/rooms/{roomCode}/start` | Start game (host only) | Yes |
| POST | `/api/rooms/{roomCode}/end` | End game (host only) | Yes |

### Game Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/poker/round/start` | Start new round | Yes |
| POST | `/api/poker/action` | Perform poker action | Yes |
| GET | `/api/poker/round/{roundId}/state` | Get round state | No |
| GET | `/api/poker/room/{roomCode}/current-round` | Get current round | No |

See backend source code for complete API documentation.

## Environment Variables

### Backend (application.properties)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
jwt.secret=your-secret-key
```

### Frontend (.env)
```
VITE_API_URL=http://localhost:8080
```

## Development

### Backend Development

Run with auto-reload:
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

Run tests:
```bash
./mvnw test
```

### Frontend Development

Start dev server (auto-reload enabled):
```bash
npm run dev
```

Build for production:
```bash
npm run build
```

Preview production build:
```bash
npm run preview
```

## Testing

### Backend Testing
See [TESTING.md](backend/TESTING.md) for backend testing guide.

Key test files:
- Game logic tests
- Room management tests
- Transaction tests

### Frontend Testing

Manual testing checklist:
- [ ] User registration
- [ ] Room creation
- [ ] Join room
- [ ] Start game
- [ ] Poker actions
- [ ] Leave room
- [ ] Logout

## Deployment

### Backend Deployment

1. Build JAR:
   ```bash
   ./mvnw clean package
   ```

2. Run JAR:
   ```bash
   java -jar target/poker-chips-application-0.0.1-SNAPSHOT.jar
   ```

### Frontend Deployment

1. Build production bundle:
   ```bash
   npm run build
   ```

2. Serve the `dist` folder with any static hosting service:
   - Netlify
   - Vercel
   - AWS S3 + CloudFront
   - Nginx

## Architecture

### Backend Architecture
```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ HTTP/WebSocket
       ▼
┌─────────────────────┐
│  Spring Security    │ JWT Authentication
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│   Controllers       │ REST API
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│    Services         │ Business Logic
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│   Repositories      │ Data Access
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│    Database         │ H2/PostgreSQL
└─────────────────────┘
```

### Frontend Architecture
```
┌─────────────────────┐
│   React Router      │ Navigation
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│   Auth Context      │ Global State
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│   Pages/Components  │ UI Layer
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│    Services         │ API Layer
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│   Backend API       │ REST + WebSocket
└─────────────────────┘
```

## Known Issues

1. **Login Endpoint Missing:** Backend only has registration endpoint. Users must register to get a JWT token.

2. **WebSocket Broadcasting:** Backend WebSocket is configured but may need additional message broadcasting logic.

3. **Round Auto-Start:** Game rooms don't automatically start poker rounds when game begins.

See [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) for complete known issues list.

## Future Enhancements

### Phase 3 (Planned)
- [ ] Separate login endpoint
- [ ] Transaction history page
- [ ] Player profile page
- [ ] Leaderboard view
- [ ] Toast notifications
- [ ] Error boundaries
- [ ] Loading skeletons

### Future Features
- [ ] Tournament mode
- [ ] Private rooms with passwords
- [ ] Spectator mode
- [ ] In-game chat
- [ ] Hand history
- [ ] Game replay system
- [ ] Admin dashboard
- [ ] Mobile app (React Native)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write/update tests
5. Submit a pull request

## License

This project is for educational purposes.

## Support

For issues or questions:
- Check the documentation files
- Review backend/frontend logs
- Open an issue on GitHub

## Acknowledgments

- Built with Spring Boot and React
- Uses Radix UI for components
- WebSocket via SockJS + STOMP
- Authentication with JWT

---

**Version:** 1.0.0
**Status:** MVP Complete ✅
**Last Updated:** February 1, 2026
