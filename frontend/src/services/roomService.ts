/**
 * Room Management Service
 * Handles room creation, joining, listing, and state management
 */

import { apiClient } from './api';
import {
  RoomDTO,
  CreateRoomRequest,
  RoomStateDTO,
  RoomPlayerDTO,
  RoomStatsDTO,
  PageResponse,
  PageRequest
} from '../types/api.types';

export const roomService = {
  /**
   * Create a new room
   * POST /api/rooms
   */
  async createRoom(data: CreateRoomRequest): Promise<RoomDTO> {
    return apiClient.post<RoomDTO>('/api/rooms', data, true);
  },

  /**
   * Get room details by room code
   * GET /api/rooms/{roomCode}
   */
  async getRoomByCode(roomCode: string): Promise<RoomDTO> {
    return apiClient.get<RoomDTO>(`/api/rooms/${roomCode}`);
  },

  /**
   * Get room state (room + players + current round)
   * GET /api/rooms/{roomCode}/state
   */
  async getRoomState(roomCode: string): Promise<RoomStateDTO> {
    return apiClient.get<RoomStateDTO>(`/api/rooms/${roomCode}/state`);
  },

  /**
   * List all available rooms
   * GET /api/rooms/available
   */
  async getAvailableRooms(): Promise<RoomDTO[]> {
    return apiClient.get<RoomDTO[]>('/api/rooms/available');
  },

  /**
   * List waiting rooms (paginated)
   * GET /api/rooms/waiting?page=0&size=20
   */
  async getWaitingRooms(params?: PageRequest): Promise<PageResponse<RoomDTO>> {
    return apiClient.get<PageResponse<RoomDTO>>('/api/rooms/waiting', params as any);
  },

  /**
   * Get active rooms by minimum players
   * GET /api/rooms/active?minPlayers=2
   */
  async getActiveRooms(minPlayers?: number): Promise<RoomDTO[]> {
    return apiClient.get<RoomDTO[]>('/api/rooms/active', minPlayers ? { minPlayers } : undefined);
  },

  /**
   * Get current user's rooms
   * GET /api/rooms/my-rooms
   */
  async getMyRooms(): Promise<RoomDTO[]> {
    return apiClient.get<RoomDTO[]>('/api/rooms/my-rooms', undefined, true);
  },

  /**
   * Start a room (host only)
   * POST /api/rooms/{roomCode}/start
   */
  async startRoom(roomCode: string): Promise<RoomDTO> {
    return apiClient.post<RoomDTO>(`/api/rooms/${roomCode}/start`, undefined, true);
  },

  /**
   * End a room (host only)
   * POST /api/rooms/{roomCode}/end
   */
  async endRoom(roomCode: string): Promise<RoomDTO> {
    return apiClient.post<RoomDTO>(`/api/rooms/${roomCode}/end`, undefined, true);
  },

  /**
   * Get count of active rooms
   * GET /api/rooms/stats/active-count
   */
  async getActiveRoomCount(): Promise<number> {
    const response = await apiClient.get<{ count: number }>('/api/rooms/stats/active-count');
    return response.count;
  },

  /**
   * Join a room
   * POST /api/room-players/join/{roomCode}
   */
  async joinRoom(roomCode: string): Promise<RoomPlayerDTO> {
    return apiClient.post<RoomPlayerDTO>(`/api/room-players/join/${roomCode}`, undefined, true);
  },

  /**
   * Leave a room
   * DELETE /api/room-players/room/{roomId}
   */
  async leaveRoom(roomId: string): Promise<void> {
    return apiClient.delete<void>(`/api/room-players/room/${roomId}`, true);
  },

  /**
   * Get all players in a room
   * GET /api/room-players/room/{roomCode}
   */
  async getRoomPlayers(roomCode: string): Promise<RoomPlayerDTO[]> {
    return apiClient.get<RoomPlayerDTO[]>(`/api/room-players/room/${roomCode}`);
  },

  /**
   * Get room leaderboard
   * GET /api/room-players/room/{roomCode}/leaderboard
   */
  async getRoomLeaderboard(roomCode: string): Promise<RoomPlayerDTO[]> {
    return apiClient.get<RoomPlayerDTO[]>(`/api/room-players/room/${roomCode}/leaderboard`);
  },

  /**
   * Get top N players in room
   * GET /api/room-players/room/{roomCode}/top?limit=5
   */
  async getTopPlayers(roomCode: string, limit: number = 5): Promise<RoomPlayerDTO[]> {
    return apiClient.get<RoomPlayerDTO[]>(`/api/room-players/room/${roomCode}/top`, { limit });
  },

  /**
   * Get eliminated players
   * GET /api/room-players/room/{roomId}/eliminated
   */
  async getEliminatedPlayers(roomId: string): Promise<RoomPlayerDTO[]> {
    return apiClient.get<RoomPlayerDTO[]>(`/api/room-players/room/${roomId}/eliminated`);
  },

  /**
   * Get user's joined rooms
   * GET /api/room-players/my-rooms
   */
  async getMyJoinedRooms(): Promise<RoomPlayerDTO[]> {
    return apiClient.get<RoomPlayerDTO[]>('/api/room-players/my-rooms', undefined, true);
  },

  /**
   * Get room statistics
   * GET /api/room-players/room/{roomId}/stats
   */
  async getRoomStats(roomId: string): Promise<RoomStatsDTO> {
    return apiClient.get<RoomStatsDTO>(`/api/room-players/room/${roomId}/stats`);
  }
};

export default roomService;
