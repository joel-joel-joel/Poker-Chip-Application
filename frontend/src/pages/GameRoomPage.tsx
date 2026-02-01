/**
 * Game Room Page - Poker Table Interface
 */

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Theme, Flex } from '@radix-ui/themes';
import { useAuth } from '../hooks/useAuth';
import { useWebSocket } from '../hooks/useWebSocket';
import { roomService } from '../services/roomService';
import { gameService } from '../services/gameService';
import {
  RoomDTO,
  RoomPlayerDTO,
  PokerRoundDTO,
  RoundStateDTO,
  PokerAction,
  RoomStatus
} from '../types/api.types';
import BetPanel from '../components/BetPanel';
import Button from '../components/ui/button';
import '../components/BetPanel.css';

export const GameRoomPage: React.FC = () => {
  const { roomCode } = useParams<{ roomCode: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { subscribeToRoom, subscribeToRound, unsubscribe } = useWebSocket();

  const [room, setRoom] = useState<RoomDTO | null>(null);
  const [players, setPlayers] = useState<RoomPlayerDTO[]>([]);
  const [currentRound, setCurrentRound] = useState<PokerRoundDTO | null>(null);
  const [roundState, setRoundState] = useState<RoundStateDTO | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [betAmount, setBetAmount] = useState(0);

  const currentPlayer = players.find(p => p.userId === user?.id);
  const isHost = room?.hostId === user?.id;

  /**
   * Load room data on mount and set up WebSocket subscriptions
   */
  useEffect(() => {
    if (!roomCode) return;
    loadRoomData();

    // Subscribe to room updates via WebSocket
    subscribeToRoom(roomCode, (message) => {
      console.log('Room update received:', message);
      // Reload room data when update is received
      loadRoomData();
    });

    // Clean up subscriptions on unmount
    return () => {
      unsubscribe(`/topic/room/${roomCode}`);
    };
  }, [roomCode]);

  /**
   * Subscribe to round updates when round changes
   */
  useEffect(() => {
    if (!currentRound) return;

    subscribeToRound(currentRound.id, (message) => {
      console.log('Round update received:', message);
      // Reload room data when round update is received
      loadRoomData();
    });

    // Clean up subscription when round changes
    return () => {
      unsubscribe(`/topic/round/${currentRound.id}`);
    };
  }, [currentRound?.id]);

  const loadRoomData = async () => {
    if (!roomCode) return;

    try {
      const [roomData, playersData, round] = await Promise.all([
        roomService.getRoomByCode(roomCode),
        roomService.getRoomPlayers(roomCode),
        gameService.getCurrentRound(roomCode)
      ]);

      setRoom(roomData);
      setPlayers(playersData);
      setCurrentRound(round);

      // Load round state if round exists
      if (round) {
        const state = await gameService.getRoundState(round.id);
        setRoundState(state);
      } else {
        setRoundState(null);
      }

      setIsLoading(false);
    } catch (err: any) {
      console.error('Error loading room data:', err);
      setError(err.message || 'Failed to load room data');
      setIsLoading(false);
    }
  };

  const handleStartGame = async () => {
    if (!roomCode || !isHost) return;

    try {
      await roomService.startRoom(roomCode);
      await loadRoomData();
    } catch (err: any) {
      setError(err.message || 'Failed to start game');
    }
  };

  const handleLeaveRoom = async () => {
    if (!room) return;

    try {
      await roomService.leaveRoom(room.id);
      navigate('/lobby');
    } catch (err: any) {
      setError(err.message || 'Failed to leave room');
    }
  };

  const handlePokerAction = async (action: PokerAction, amount?: number) => {
    if (!currentRound || !currentPlayer) return;

    try {
      setError('');
      await gameService.performAction({
        roundId: currentRound.id,
        playerId: currentPlayer.id,
        action,
        amount
      });
      // Reload room data to get updated state
      await loadRoomData();
    } catch (err: any) {
      setError(err.message || 'Failed to perform action');
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-[#181717]">
        <p className="text-white text-xl">Loading game room...</p>
      </div>
    );
  }

  if (!room) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-[#181717]">
        <p className="text-white text-xl">Room not found</p>
      </div>
    );
  }

  const yourBet = roundState?.playerBets.find(b => b.playerId === currentPlayer?.id)?.currentBet || 0;
  const currentBet = currentRound?.currentBet || 0;
  const pot = currentRound?.pot || 0;

  return (
    <Theme appearance="dark" accentColor="green" radius="large">
      <div className="min-h-screen bg-[#181717]">
        {/* Header */}
        <header className="bg-[#1a1a1a] border-b border-[#2a2a2a] px-6 py-4">
          <div className="max-w-7xl mx-auto flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold text-white">{room.name}</h1>
              <p className="text-sm text-gray-400">Room Code: {room.roomCode}</p>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-gray-400">
                Players: {room.currentPlayerCount} / {room.maxPlayers}
              </span>
              {isHost && room.status === RoomStatus.WAITING && (
                <Button
                  onClick={handleStartGame}
                  className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md"
                >
                  Start Game
                </Button>
              )}
              <Button
                onClick={handleLeaveRoom}
                className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md"
              >
                Leave Room
              </Button>
            </div>
          </div>
        </header>

        {/* Error Message */}
        {error && (
          <div className="max-w-7xl mx-auto px-6 mt-4">
            <div className="p-4 bg-red-500/10 border border-red-500/20 rounded-md">
              <p className="text-red-400">{error}</p>
            </div>
          </div>
        )}

        {/* Game Area */}
        <main className="max-w-7xl mx-auto px-6 py-8">
          {/* Players List */}
          <div className="mb-8 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {players.map((player) => (
              <div
                key={player.id}
                className={`p-4 rounded-lg border ${
                  player.userId === user?.id
                    ? 'bg-teal-500/10 border-teal-500/30'
                    : 'bg-[#1a1a1a] border-[#2a2a2a]'
                }`}
              >
                <p className="text-white font-semibold">{player.username}</p>
                <p className="text-sm text-gray-400">Chips: ${player.chipBalance}</p>
                {player.isEliminated && (
                  <span className="text-xs text-red-400">Eliminated</span>
                )}
              </div>
            ))}
          </div>

          {/* Poker Table */}
          {room.status === RoomStatus.IN_PROGRESS ? (
            <Flex
              direction="column"
              align="center"
              justify="center"
              gap="4"
              style={{ minHeight: '50vh' }}
            >
              {/* Betting Panel */}
              <BetPanel
                yourBet={yourBet}
                currentBet={currentBet}
                pot={pot}
                maxBet={currentPlayer?.chipBalance || 1000}
                onBetChange={setBetAmount}
              />

              {/* Action Buttons */}
              <Flex gap="3" justify="center" align="center">
                <Button
                  className="bet-button raise-button"
                  onClick={() => handlePokerAction(PokerAction.RAISE, betAmount)}
                  disabled={!currentRound || betAmount <= currentBet}
                >
                  Raise
                </Button>
                <Button
                  className="bet-button fold-button"
                  color="red"
                  onClick={() => handlePokerAction(PokerAction.FOLD)}
                  disabled={!currentRound}
                >
                  Fold
                </Button>
                <Button
                  className="bet-button call-button"
                  color="gray"
                  onClick={() => handlePokerAction(PokerAction.CALL)}
                  disabled={!currentRound}
                >
                  Call
                </Button>
                <Button
                  className="bet-button check-button"
                  color="gray"
                  onClick={() => handlePokerAction(PokerAction.CHECK)}
                  disabled={!currentRound}
                >
                  Check
                </Button>
              </Flex>

              {/* Round Info */}
              {currentRound && (
                <div className="text-center text-white mt-4">
                  <p className="text-lg">Round #{currentRound.roundNumber}</p>
                  <p className="text-2xl font-bold">Pot: ${pot}</p>
                </div>
              )}
            </Flex>
          ) : (
            <div className="text-center py-12">
              <p className="text-gray-400 text-xl">
                {room.status === RoomStatus.WAITING
                  ? 'Waiting for host to start the game...'
                  : 'Game has ended'}
              </p>
            </div>
          )}
        </main>
      </div>
    </Theme>
  );
};

export default GameRoomPage;
