/**
 * Lobby Page - Room Browser and Creation
 */

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { roomService } from '../services/roomService';
import { RoomDTO, RoomStatus } from '../types/api.types';
import { Button } from '../components/ui/button';

export const LobbyPage: React.FC = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const [rooms, setRooms] = useState<RoomDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [error, setError] = useState<string>('');

  // Create room form state
  const [createFormData, setCreateFormData] = useState({
    name: '',
    maxPlayers: 6,
    startingChips: 1000
  });

  /**
   * Load available rooms on mount
   */
  useEffect(() => {
    loadRooms();
  }, []);

  const loadRooms = async () => {
    try {
      setIsLoading(true);
      const availableRooms = await roomService.getAvailableRooms();
      setRooms(availableRooms);
    } catch (err: any) {
      setError('Failed to load rooms');
      console.error('Error loading rooms:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateRoom = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      const newRoom = await roomService.createRoom(createFormData);
      setShowCreateForm(false);
      // Host is automatically added as a player on the backend
      // Navigate directly to the room
      navigate(`/room/${newRoom.roomCode}`);
    } catch (err: any) {
      setError(err.message || 'Failed to create room');
    }
  };

  const handleJoinRoom = async (roomCode: string) => {
    try {
      await roomService.joinRoom(roomCode);
      navigate(`/room/${roomCode}`);
    } catch (err: any) {
      setError(err.message || 'Failed to join room');
    }
  };

  const getRoomStatusBadge = (status: RoomStatus) => {
    if (!status) return null;

    const colors = {
      WAITING: 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30',
      IN_PROGRESS: 'bg-green-500/20 text-green-400 border-green-500/30',
      FINISHED: 'bg-gray-500/20 text-gray-400 border-gray-500/30'
    };

    return (
      <span className={`px-2 py-1 text-xs rounded-md border ${colors[status]}`}>
        {status.replace('_', ' ')}
      </span>
    );
  };

  return (
    <div className="min-h-screen bg-[#181717]">
      {/* Header */}
      <header className="bg-[#1a1a1a] border-b border-[#2a2a2a] px-6 py-4">
        <div className="max-w-7xl mx-auto flex justify-between items-center">
          <h1 className="text-2xl font-bold text-white">Poker Chips Lobby</h1>
          <div className="flex items-center gap-4">
            <span className="text-gray-400">Welcome, {user?.username}</span>
            <Button
              onClick={logout}
              className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md"
            >
              Logout
            </Button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        {/* Actions Bar */}
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-semibold text-white">Available Rooms</h2>
          <div className="flex gap-3">
            <Button
              onClick={loadRooms}
              className="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded-md"
            >
              Refresh
            </Button>
            <Button
              onClick={() => setShowCreateForm(!showCreateForm)}
              className="bg-teal-600 hover:bg-teal-700 text-white px-4 py-2 rounded-md"
            >
              {showCreateForm ? 'Cancel' : 'Create Room'}
            </Button>
          </div>
        </div>

        {/* Error Message */}
        {error && (
          <div className="mb-6 p-4 bg-red-500/10 border border-red-500/20 rounded-md">
            <p className="text-red-400">{error}</p>
          </div>
        )}

        {/* Create Room Form */}
        {showCreateForm && (
          <div className="mb-6 p-6 bg-[#1a1a1a] border border-[#2a2a2a] rounded-lg">
            <h3 className="text-lg font-semibold text-white mb-4">Create New Room</h3>
            <form onSubmit={handleCreateRoom} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">
                    Room Name
                  </label>
                  <input
                    type="text"
                    value={createFormData.name}
                    onChange={(e) => setCreateFormData({ ...createFormData, name: e.target.value })}
                    className="w-full px-3 py-2 bg-[#2a2a2a] border border-[#3a3a3a] rounded-md text-white"
                    placeholder="Enter room name"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">
                    Max Players
                  </label>
                  <input
                    type="number"
                    min="2"
                    max="10"
                    value={createFormData.maxPlayers}
                    onChange={(e) => setCreateFormData({ ...createFormData, maxPlayers: parseInt(e.target.value) })}
                    className="w-full px-3 py-2 bg-[#2a2a2a] border border-[#3a3a3a] rounded-md text-white"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">
                    Starting Chips
                  </label>
                  <input
                    type="number"
                    min="100"
                    step="100"
                    value={createFormData.startingChips}
                    onChange={(e) => setCreateFormData({ ...createFormData, startingChips: parseInt(e.target.value) })}
                    className="w-full px-3 py-2 bg-[#2a2a2a] border border-[#3a3a3a] rounded-md text-white"
                  />
                </div>
              </div>
              <Button
                type="submit"
                className="bg-teal-600 hover:bg-teal-700 text-white px-6 py-2 rounded-md"
              >
                Create Room
              </Button>
            </form>
          </div>
        )}

        {/* Room List */}
        {isLoading ? (
          <div className="text-center py-12">
            <p className="text-gray-400 text-lg">Loading rooms...</p>
          </div>
        ) : rooms.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-400 text-lg">No rooms available. Create one to get started!</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {rooms.map((room) => (
              <div
                key={room.id}
                className="bg-[#1a1a1a] border border-[#2a2a2a] rounded-lg p-6 hover:border-teal-500/30 transition-colors"
              >
                <div className="flex justify-between items-start mb-4">
                  <h3 className="text-lg font-semibold text-white">{room.name}</h3>
                  {getRoomStatusBadge(room.status)}
                </div>

                <div className="space-y-2 mb-4">
                  <p className="text-sm text-gray-400">
                    Room Code: <span className="text-white font-mono">{room.roomCode}</span>
                  </p>
                  <p className="text-sm text-gray-400">
                    Players: <span className="text-white">{room.currentPlayerCount} / {room.maxPlayers}</span>
                  </p>
                  <p className="text-sm text-gray-400">
                    Starting Chips: <span className="text-white">${room.startingChips}</span>
                  </p>
                </div>

                <Button
                  onClick={() => handleJoinRoom(room.roomCode)}
                  disabled={room.currentPlayerCount >= room.maxPlayers || room.status !== RoomStatus.WAITING}
                  className="w-full bg-teal-600 hover:bg-teal-700 text-white px-4 py-2 rounded-md disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {room.currentPlayerCount >= room.maxPlayers ? 'Full' : 'Join Room'}
                </Button>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
};

export default LobbyPage;
