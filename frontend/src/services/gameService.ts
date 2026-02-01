/**
 * Poker Game Service
 * Handles poker actions, rounds, and game state
 */

import { apiClient } from './api';
import {
  PokerRoundDTO,
  PokerActionRequest,
  PokerActionResponse,
  RoundStateDTO
} from '../types/api.types';

export const gameService = {
  /**
   * Start a new poker round
   * POST /api/poker/round/start
   */
  async startRound(roomId: string): Promise<PokerRoundDTO> {
    return apiClient.post<PokerRoundDTO>('/api/poker/round/start', { roomId }, true);
  },

  /**
   * Perform a poker action (BET, CALL, RAISE, CHECK, FOLD, ALL_IN)
   * POST /api/poker/action
   */
  async performAction(action: PokerActionRequest): Promise<PokerActionResponse> {
    return apiClient.post<PokerActionResponse>('/api/poker/action', action, true);
  },

  /**
   * End a round with a winner
   * POST /api/poker/round/{roundId}/end
   */
  async endRound(roundId: string, winnerId: string): Promise<PokerRoundDTO> {
    return apiClient.post<PokerRoundDTO>(
      `/api/poker/round/${roundId}/end`,
      { winnerId },
      true
    );
  },

  /**
   * Get round state (round info + player bets)
   * GET /api/poker/round/{roundId}/state
   */
  async getRoundState(roundId: string): Promise<RoundStateDTO> {
    return apiClient.get<RoundStateDTO>(`/api/poker/round/${roundId}/state`);
  },

  /**
   * Get current active round for a room
   * GET /api/poker/room/{roomCode}/current-round
   */
  async getCurrentRound(roomCode: string): Promise<PokerRoundDTO | null> {
    try {
      return await apiClient.get<PokerRoundDTO>(`/api/poker/room/${roomCode}/current-round`);
    } catch (error: any) {
      // If no active round, return null instead of throwing
      if (error.status === 404) {
        return null;
      }
      throw error;
    }
  }
};

export default gameService;
