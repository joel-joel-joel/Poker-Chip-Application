/**
 * TypeScript types matching backend DTOs
 */

// ============================================================================
// Authentication Types
// ============================================================================

export interface AuthResponse {
  token: string;
  tokenType: string;
  username: string;
  email: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

// ============================================================================
// User Types
// ============================================================================

export interface UserDTO {
  id: string;
  username: string;
  email: string;
  createdAt: string;
}

// ============================================================================
// Room Types
// ============================================================================

export enum RoomStatus {
  WAITING = 'WAITING',
  IN_PROGRESS = 'IN_PROGRESS',
  FINISHED = 'FINISHED'
}

export interface RoomDTO {
  id: string;
  roomCode: string;
  name: string;
  hostId: string;
  maxPlayers: number;
  startingChips: number;
  status: RoomStatus;
  currentPlayerCount: number;
  createdAt: string;
  startedAt?: string;
  endedAt?: string;
}

export interface CreateRoomRequest {
  name: string;
  maxPlayers: number;
  startingChips: number;
}

export interface RoomStateDTO {
  room: RoomDTO;
  players: RoomPlayerDTO[];
  currentRound?: PokerRoundDTO;
}

// ============================================================================
// Room Player Types
// ============================================================================

export interface RoomPlayerDTO {
  id: string;
  userId: string;
  username: string;
  roomId: string;
  chipBalance: number;
  isActive: boolean;
  isEliminated: boolean;
  joinedAt: string;
  eliminatedAt?: string;
}

export interface RoomStatsDTO {
  totalChips: number;
  averageChips: number;
  playerCount: number;
}

// ============================================================================
// Poker Game Types
// ============================================================================

export enum PokerAction {
  BET = 'BET',
  CALL = 'CALL',
  RAISE = 'RAISE',
  CHECK = 'CHECK',
  FOLD = 'FOLD',
  ALL_IN = 'ALL_IN'
}

export enum RoundStatus {
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export interface PokerRoundDTO {
  id: string;
  roomId: string;
  roundNumber: number;
  pot: number;
  currentBet: number;
  status: RoundStatus;
  startedAt: string;
  endedAt?: string;
  winnerId?: string;
}

export interface PokerActionRequest {
  roundId: string;
  playerId: string;
  action: PokerAction;
  amount?: number;
}

export interface PokerActionResponse {
  round: PokerRoundDTO;
  player: RoomPlayerDTO;
  success: boolean;
  message?: string;
}

export interface PlayerBetDTO {
  playerId: string;
  username: string;
  currentBet: number;
  totalBetThisRound: number;
  chipBalance: number;
  hasFolded: boolean;
  isAllIn: boolean;
}

export interface RoundStateDTO {
  round: PokerRoundDTO;
  playerBets: PlayerBetDTO[];
  currentPlayerTurn?: string;
  minRaise: number;
}

// ============================================================================
// Transaction Types
// ============================================================================

export enum TransactionType {
  INITIAL_CHIPS = 'INITIAL_CHIPS',
  BET = 'BET',
  CALL = 'CALL',
  RAISE = 'RAISE',
  WIN = 'WIN',
  TRANSFER = 'TRANSFER',
  ALL_IN = 'ALL_IN',
  ANTE = 'ANTE',
  BLIND = 'BLIND'
}

export interface ChipTransactionDTO {
  id: string;
  roomId: string;
  fromPlayerId?: string;
  fromPlayerUsername?: string;
  toPlayerId?: string;
  toPlayerUsername?: string;
  amount: number;
  transactionType: TransactionType;
  roundId?: string;
  createdAt: string;
  description?: string;
}

export interface TransferChipsRequest {
  fromPlayerId: string;
  toPlayerId: string;
  amount: number;
  type: TransactionType;
}

export interface PlayerStatsDTO {
  totalWon: number;
  totalLost: number;
  netProfit: number;
  totalBets: number;
  transactionCount: number;
}

export interface TransactionActivityDTO {
  hour: string;
  totalAmount: number;
  transactionCount: number;
}

// ============================================================================
// Pagination Types
// ============================================================================

export interface PageRequest {
  page?: number;
  size?: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// ============================================================================
// Error Types
// ============================================================================

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
