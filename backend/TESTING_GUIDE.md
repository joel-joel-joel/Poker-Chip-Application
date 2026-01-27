# Comprehensive Testing Guide - Poker Chips Application

This guide provides step-by-step terminal commands to test all poker game logic before frontend integration.

## Prerequisites

1. **Start the application:**
   ```bash
   cd /Users/joelong/Documents/SWE-Projects/PokerChipsApplication/backend
   ./mvnw spring-boot:run
   ```

2. **Open a new terminal** for running test commands

3. **Base URL:** `http://localhost:8080`

---

## Test Setup: Create Users and Room

### Step 1: Register Three Test Players

**Register Alice:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@test.com",
    "password": "password123"
  }' | jq '.'
```

**Expected Response:**
```json
{
  "token": "eyJhbGc...",
  "tokenType": "Bearer",
  "username": "alice",
  "email": "alice@test.com"
}
```

**Save Alice's token:**
```bash
export ALICE_TOKEN="<paste-token-here>"
```

**Register Bob:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "bob",
    "email": "bob@test.com",
    "password": "password123"
  }' | jq '.'
```

**Save Bob's token:**
```bash
export BOB_TOKEN="<paste-token-here>"
```

**Register Charlie:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "charlie",
    "email": "charlie@test.com",
    "password": "password123"
  }' | jq '.'
```

**Save Charlie's token:**
```bash
export CHARLIE_TOKEN="<paste-token-here>"
```

---

### Step 2: Create a Room (Alice as Host)

```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ALICE_TOKEN" \
  -d '{
    "name": "Test Poker Game",
    "maxPlayers": 6,
    "startingChips": 1000
  }' | jq '.'
```

**Expected Response:**
```json
{
  "id": "...",
  "code": "ABC123",
  "name": "Test Poker Game",
  "hostUsername": "alice",
  "roomStatus": "WAITING",
  "maxPlayers": 6,
  "startingChips": 1000,
  "currentPlayerCount": 1,
  "createdAt": "2026-01-16T10:30:00"
}
```

**Save the room code:**
```bash
export ROOM_CODE="<paste-room-code-here>"
```

---

### Step 3: Bob and Charlie Join the Room

**Bob joins:**
```bash
curl -X POST http://localhost:8080/api/room-players/join/$ROOM_CODE \
  -H "Authorization: Bearer $BOB_TOKEN" | jq '.'
```

**Expected Response:**
```json
{
  "id": "...",
  "username": "bob",
  "chipBalance": 1000,
  "position": 1,
  "status": "ACTIVE"
}
```

**Charlie joins:**
```bash
curl -X POST http://localhost:8080/api/room-players/join/$ROOM_CODE \
  -H "Authorization: Bearer $CHARLIE_TOKEN" | jq '.'
```

---

### Step 4: Start the Room

```bash
curl -X POST http://localhost:8080/api/rooms/$ROOM_CODE/start \
  -H "Authorization: Bearer $ALICE_TOKEN" | jq '.'
```

**Expected:** Room status changes to "ACTIVE"

---

### Step 5: Get All Players and Save Player IDs

```bash
curl -X GET http://localhost:8080/api/room-players/room/$ROOM_CODE | jq '.'
```

**Expected Response:**
```json
[
  {
    "id": "uuid-alice",
    "username": "alice", 
    "chipBalance": 1000,
    "position": 0,
    "status": "ACTIVE"
  },
  {
    "id": "uuid-bob",
    "username": "bob",
    "chipBalance": 1000,
    "position": 1,
    "status": "ACTIVE"
  },
  {
    "id": "uuid-charlie",
    "username": "charlie",
    "chipBalance": 1000,
    "position": 2,
    "status": "ACTIVE"
  }
]
```

**Save player IDs:**
```bash
export ALICE_PLAYER_ID="<paste-alice-player-id>"
export BOB_PLAYER_ID="<paste-bob-player-id>"
export CHARLIE_PLAYER_ID="<paste-charlie-player-id>"
```

---

## Test Suite 1: Complete Round Flow

### Test 1.1: Start a New Round

```bash
curl -X POST "http://localhost:8080/api/poker/round/start?roomCode=$ROOM_CODE" | jq '.'
```

**Expected Response:**
```json
{
  "roundId": "round-uuid-1",
  "roundNumber": 1,
  "potAmount": 0,
  "currentBet": 0,
  "players": [
    {
      "playerId": "...",
      "username": "alice",
      "chipBalance": 1000,
      "currentHandBet": 0,
      "status": "ACTIVE",
      "isAllIn": false
    },
    // ... bob and charlie
  ],
  "roundStatus": "ACTIVE"
}
```

**Save the round ID:**
```bash
export ROUND_ID="<paste-round-id>"
```

✅ **Verify:**
- roundNumber = 1
- potAmount = 0
- currentBet = 0
- All players have chipBalance = 1000
- All currentHandBet = 0

---

### Test 1.2: Alice Opens with a BET ($50)

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID"'",
    "playerId": "'"$ALICE_PLAYER_ID"'",
    "action": "BET",
    "amount": 50
  }' | jq '.'
```

**Expected Response:**
```json
{
  "roundId": "...",
  "roundNumber": 1,
  "potAmount": 50,
  "currentBet": 50,
  "players": [
    {
      "playerId": "...",
      "username": "alice",
      "chipBalance": 950,
      "currentHandBet": 50,
      "status": "ACTIVE",
      "isAllIn": false
    },
    // ... others unchanged
  ],
  "roundStatus": "ACTIVE"
}
```

✅ **Verify:**
- potAmount = 50
- currentBet = 50
- Alice's chipBalance = 950
- Alice's currentHandBet = 50
- Bob and Charlie unchanged

---

### Test 1.3: Bob Calls ($50)

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID"'",
    "playerId": "'"$BOB_PLAYER_ID"'",
    "action": "CALL"
  }' | jq '.'
```

**Expected Response:**
```json
{
  "potAmount": 100,
  "currentBet": 50,
  "players": [
    {
      "username": "alice",
      "chipBalance": 950,
      "currentHandBet": 50
    },
    {
      "username": "bob",
      "chipBalance": 950,
      "currentHandBet": 50
    },
    {
      "username": "charlie",
      "chipBalance": 1000,
      "currentHandBet": 0
    }
  ]
}
```

✅ **Verify:**
- potAmount = 100 (50 + 50)
- currentBet = 50 (unchanged)
- Bob's chipBalance = 950
- Bob's currentHandBet = 50

---

### Test 1.4: Charlie Raises to $150

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID"'",
    "playerId": "'"$CHARLIE_PLAYER_ID"'",
    "action": "RAISE",
    "amount": 150
  }' | jq '.'
```

**Expected Response:**
```json
{
  "potAmount": 250,
  "currentBet": 150,
  "players": [
    {
      "username": "charlie",
      "chipBalance": 850,
      "currentHandBet": 150
    }
  ]
}
```

✅ **Verify:**
- potAmount = 250 (100 + 150)
- currentBet = 150 (raised from 50)
- Charlie's chipBalance = 850
- Charlie's currentHandBet = 150

---

### Test 1.5: Alice Folds

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID"'",
    "playerId": "'"$ALICE_PLAYER_ID"'",
    "action": "FOLD"
  }' | jq '.'
```

**Expected Response:**
```json
{
  "potAmount": 250,
  "players": [
    {
      "username": "alice",
      "chipBalance": 950,
      "currentHandBet": 50,
      "status": "FOLDED"
    }
  ]
}
```

✅ **Verify:**
- Alice's status = "FOLDED"
- Alice's chipBalance = 950 (unchanged - she already bet $50)
- potAmount = 250 (unchanged)

---

### Test 1.6: Bob Calls the Raise (Additional $100)

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID"'",
    "playerId": "'"$BOB_PLAYER_ID"'",
    "action": "CALL"
  }' | jq '.'
```

**Expected Response:**
```json
{
  "potAmount": 350,
  "currentBet": 150,
  "players": [
    {
      "username": "bob",
      "chipBalance": 850,
      "currentHandBet": 150
    }
  ]
}
```

✅ **Verify:**
- potAmount = 350 (250 + 100)
- Bob's chipBalance = 850 (1000 - 150 total)
- Bob's currentHandBet = 150

---

### Test 1.7: End Round - Charlie Wins

```bash
curl -X POST "http://localhost:8080/api/poker/round/$ROUND_ID/end?winnerId=$CHARLIE_PLAYER_ID" | jq '.'
```

**Expected Response:**
```json
{
  "roundId": "...",
  "roundNumber": 1,
  "potAmount": 0,
  "currentBet": 150,
  "players": [
    {
      "username": "charlie",
      "chipBalance": 1200,
      "currentHandBet": 150
    }
  ],
  "roundStatus": "COMPLETE"
}
```

✅ **Verify:**
- roundStatus = "COMPLETE"
- potAmount = 0 (distributed)
- Charlie's chipBalance = 1200 (850 + 350 pot)

---

### Test 1.8: Verify Total Chips Conservation

```bash
curl -X GET http://localhost:8080/api/room-players/room/$ROOM_CODE | jq '[.[] | .chipBalance] | add'
```

**Expected:** Total = 3000 (unchanged from start)

Breakdown:
- Alice: 950
- Bob: 850
- Charlie: 1200
- **Total: 3000 ✅**

---

### Test 1.9: Start Round 2 - Players Should Reset

```bash
curl -X POST "http://localhost:8080/api/poker/round/start?roomCode=$ROOM_CODE" | jq '.'
```

**Save new round ID:**
```bash
export ROUND_ID_2="<paste-new-round-id>"
```

✅ **Verify:**
- roundNumber = 2
- potAmount = 0
- currentBet = 0
- All currentHandBet = 0
- Alice's status = "ACTIVE" (not "FOLDED" anymore)
- Charlie's status = "ACTIVE" (not winner status)
- Chip balances maintained: Alice=950, Bob=850, Charlie=1200

---

## Test Suite 2: CHECK Action

### Test 2.1: Alice Checks (No Bet Yet)

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_2"'",
    "playerId": "'"$ALICE_PLAYER_ID"'",
    "action": "CHECK"
  }' | jq '.'
```

✅ **Verify:**
- Action succeeds (no error)
- potAmount = 0
- currentBet = 0
- Alice's chipBalance unchanged

---

### Test 2.2: Bob Bets $25

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_2"'",
    "playerId": "'"$BOB_PLAYER_ID"'",
    "action": "BET",
    "amount": 25
  }' | jq '.'
```

✅ **Verify:**
- potAmount = 25
- currentBet = 25
- Bob's chipBalance = 825 (850 - 25)

---

### Test 2.3: Charlie Tries to CHECK (Should Fail)

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_2"'",
    "playerId": "'"$CHARLIE_PLAYER_ID"'",
    "action": "CHECK"
  }' | jq '.'
```

**Expected Error:**
```json
{
  "message": "Cannot check - must call 25 or fold",
  "status": 400
}
```

✅ **Verify:** Error returned with clear message

---

## Test Suite 3: ALL_IN Action

### Test 3.1: Start Round 3

```bash
curl -X POST "http://localhost:8080/api/poker/round/start?roomCode=$ROOM_CODE" | jq '.'
export ROUND_ID_3="<paste-new-round-id>"
```

---

### Test 3.2: Alice Bets Big ($500)

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_3"'",
    "playerId": "'"$ALICE_PLAYER_ID"'",
    "action": "BET",
    "amount": 500
  }' | jq '.'
```

✅ **Verify:**
- potAmount = 500
- currentBet = 500
- Alice's chipBalance = 450 (950 - 500)

---

### Test 3.3: Bob Goes ALL_IN (He has less than $500)

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_3"'",
    "playerId": "'"$BOB_PLAYER_ID"'",
    "action": "ALL_IN"
  }' | jq '.'
```

**Expected Response:**
```json
{
  "potAmount": 1325,
  "currentBet": 500,
  "players": [
    {
      "username": "bob",
      "chipBalance": 0,
      "currentHandBet": 825,
      "status": "ALL_IN",
      "isAllIn": true
    }
  ]
}
```

✅ **Verify:**
- Bob's chipBalance = 0
- Bob's status = "ALL_IN"
- Bob's isAllIn = true
- potAmount = 1325 (500 + 825)
- Bob's currentHandBet = 825 (all his chips)

---

### Test 3.4: Charlie Calls ($500)

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_3"'",
    "playerId": "'"$CHARLIE_PLAYER_ID"'",
    "action": "CALL"
  }' | jq '.'
```

✅ **Verify:**
- potAmount = 1825 (1325 + 500)
- Charlie's chipBalance = 700 (1200 - 500)

---

## Test Suite 4: Error Handling & Validation

### Test 4.1: BET When Already a Bet

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_3"'",
    "playerId": "'"$ALICE_PLAYER_ID"'",
    "action": "BET",
    "amount": 100
  }' | jq '.'
```

**Expected Error:**
```json
{
  "message": "Cannot bet - there is already a bet. Use raise instead."
}
```

---

### Test 4.2: RAISE Lower Than Current Bet

```bash
curl -X POST "http://localhost:8080/api/poker/round/start?roomCode=$ROOM_CODE" | jq '.'
export ROUND_ID_4="<paste-round-id>"

# Alice bets $100
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_4"'",
    "playerId": "'"$ALICE_PLAYER_ID"'",
    "action": "BET",
    "amount": 100
  }' | jq '.'

# Bob tries to raise to $80 (lower than $100)
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_4"'",
    "playerId": "'"$BOB_PLAYER_ID"'",
    "action": "RAISE",
    "amount": 80
  }' | jq '.'
```

**Expected Error:**
```json
{
  "message": "Raise must be higher than current bet of 100"
}
```

---

### Test 4.3: Insufficient Chips for Action

```bash
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_4"'",
    "playerId": "'"$BOB_PLAYER_ID"'",
    "action": "RAISE",
    "amount": 5000
  }' | jq '.'
```

**Expected Error:**
```json
{
  "message": "Insufficient chips for raise"
}
```

---

### Test 4.4: Folded Player Cannot Act

```bash
# Alice folds
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_4"'",
    "playerId": "'"$ALICE_PLAYER_ID"'",
    "action": "FOLD"
  }' | jq '.'

# Alice tries to bet (should fail)
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_4"'",
    "playerId": "'"$ALICE_PLAYER_ID"'",
    "action": "BET",
    "amount": 50
  }' | jq '.'
```

**Expected Error:**
```json
{
  "message": "Player has folded"
}
```

---

### Test 4.5: CALL When No Bet to Call

```bash
curl -X POST "http://localhost:8080/api/poker/round/start?roomCode=$ROOM_CODE" | jq '.'
export ROUND_ID_5="<paste-round-id>"

curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_5"'",
    "playerId": "'"$ALICE_PLAYER_ID"'",
    "action": "CALL"
  }' | jq '.'
```

**Expected Error:**
```json
{
  "message": "No bet to call"
}
```

---

## Test Suite 5: Round State Queries

### Test 5.1: Get Current Round State

```bash
curl -X GET "http://localhost:8080/api/poker/round/$ROUND_ID_5/state" | jq '.'
```

✅ **Verify:** Returns complete round state with all players

---

### Test 5.2: Get Current Active Round for Room

```bash
curl -X GET "http://localhost:8080/api/poker/room/$ROOM_CODE/current-round" | jq '.'
```

✅ **Verify:** Returns the active round (Round 5)

---

### Test 5.3: Get Room Player Leaderboard

```bash
curl -X GET "http://localhost:8080/api/room-players/room/$ROOM_CODE/leaderboard" | jq '.'
```

✅ **Verify:** Players sorted by chipBalance descending

---

## Test Suite 6: Transaction History

### Test 6.1: Get All Room Transactions

```bash
curl -X GET "http://localhost:8080/api/transactions/room/$ROOM_CODE" | jq '.'
```

✅ **Verify:**
- Shows all transactions
- Includes BET, CALL, RAISE, FOLD, ALL_IN, WIN types
- fromPlayer is null for WIN transactions
- toPlayer is null for BET/CALL/RAISE/FOLD transactions

---

### Test 6.2: Get Transactions by Type

```bash
# Get all BET transactions
curl -X GET "http://localhost:8080/api/transactions/room/$ROOM_CODE/type/BET" | jq '.'

# Get all FOLD transactions
curl -X GET "http://localhost:8080/api/transactions/room/$ROOM_CODE/type/FOLD" | jq '.'

# Get all WIN transactions
curl -X GET "http://localhost:8080/api/transactions/room/$ROOM_CODE/type/WIN" | jq '.'
```

---

### Test 6.3: Get Player Statistics

```bash
curl -X GET "http://localhost:8080/api/transactions/player/$ALICE_PLAYER_ID/stats" | jq '.'
```

**Expected Response:**
```json
{
  "totalSent": 650,
  "totalReceived": 0,
  "netGain": -650,
  "transactionCount": 5
}
```

---

## Test Suite 7: Room Statistics

### Test 7.1: Get Room Stats

```bash
curl -X GET "http://localhost:8080/api/room-players/room/<ROOM_ID>/stats" | jq '.'
```

**Expected Response:**
```json
{
  "totalChips": 3000,
  "averageChips": 1000.0,
  "playerCount": 3
}
```

✅ **Verify:** totalChips = 3000 (chip conservation)

---

### Test 7.2: Get Eliminated Players

```bash
curl -X GET "http://localhost:8080/api/room-players/room/<ROOM_ID>/eliminated" | jq '.'
```

✅ **Verify:** Shows players with chipBalance = 0 (Bob in Round 3)

---

## Test Suite 8: Edge Cases

### Test 8.1: Minimum Bet Amount

```bash
curl -X POST "http://localhost:8080/api/poker/round/start?roomCode=$ROOM_CODE" | jq '.'
export ROUND_ID_6="<paste-round-id>"

# Try to bet $0
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_6"'",
    "playerId": "'"$ALICE_PLAYER_ID"'",
    "action": "BET",
    "amount": 0
  }' | jq '.'
```

**Expected Error:**
```json
{
  "message": "Bet amount must be positive"
}
```

---

### Test 8.2: Acting on Completed Round

```bash
# End the round first
curl -X POST "http://localhost:8080/api/poker/round/$ROUND_ID_6/end?winnerId=$ALICE_PLAYER_ID" | jq '.'

# Try to bet on completed round
curl -X POST http://localhost:8080/api/poker/action \
  -H "Content-Type: application/json" \
  -d '{
    "roundId": "'"$ROUND_ID_6"'",
    "playerId": "'"$BOB_PLAYER_ID"'",
    "action": "BET",
    "amount": 50
  }' | jq '.'
```

**Expected Error:**
```json
{
  "message": "Round is not active"
}
```

---

## Summary Checklist

After running all tests, verify:

- [ ] Players can BET, CALL, RAISE, CHECK, FOLD, ALL_IN
- [ ] Pot accumulates correctly
- [ ] Current bet updates on BET and RAISE
- [ ] CHECK fails when there's a bet to call
- [ ] BET fails when there's already a bet
- [ ] RAISE must be higher than current bet
- [ ] Folded players cannot act
- [ ] ALL_IN sets player chips to 0 and status to ALL_IN
- [ ] Winner receives entire pot
- [ ] Round reset clears currentHandBet and unfolds players
- [ ] Total chips in room always equals 3000
- [ ] Transaction history tracks all actions
- [ ] fromPlayer is null for WIN transactions
- [ ] toPlayer is null for pot contributions

---

## Quick Test Script

Save this as `test-poker.sh`:

```bash
#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080"

echo "Starting Poker Service Tests..."

# Test 1: Register users
echo -e "\n${GREEN}Test 1: Registering users${NC}"
ALICE=$(curl -s -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@test.com","password":"pass123"}' | jq -r '.token')

BOB=$(curl -s -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"bob","email":"bob@test.com","password":"pass123"}' | jq -r '.token')

CHARLIE=$(curl -s -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"charlie","email":"charlie@test.com","password":"pass123"}' | jq -r '.token')

if [ -n "$ALICE" ] && [ -n "$BOB" ] && [ -n "$CHARLIE" ]; then
    echo -e "${GREEN}✓ Users registered successfully${NC}"
else
    echo -e "${RED}✗ User registration failed${NC}"
    exit 1
fi

# Test 2: Create room
echo -e "\n${GREEN}Test 2: Creating room${NC}"
ROOM_CODE=$(curl -s -X POST $BASE_URL/api/rooms \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ALICE" \
  -d '{"name":"Test","maxPlayers":6,"startingChips":1000}' | jq -r '.code')

echo "Room code: $ROOM_CODE"

# Add more tests as needed...
echo -e "\n${GREEN}All tests completed!${NC}"
```

Make it executable and run:
```bash
chmod +x test-poker.sh
./test-poker.sh
```

---

## Troubleshooting

**Issue:** "Room not found"
- Verify the room code is correct
- Check room is in ACTIVE status

**Issue:** "Player not found"
- Ensure players have joined the room
- Verify player IDs are correct UUIDs

**Issue:** "Transaction type error"
- Ensure transaction type is one of: BET, CALL, RAISE, CHECK, FOLD, ALL_IN
- Check spelling and case sensitivity

**Issue:** Chip totals don't add up to 3000
- Check for transaction recording errors
- Verify no chips were lost in pot distribution
- Review transaction history for anomalies
