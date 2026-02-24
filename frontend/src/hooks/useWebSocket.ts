/**
 * useWebSocket Hook
 * Manages WebSocket connection and subscriptions
 */

import { useState, useEffect, useRef } from 'react';
import { websocketService } from '../services/websocket';

type MessageHandler = (message: any) => void;

export const useWebSocket = () => {
  const [isConnected, setIsConnected] = useState(false);
  const hasAttemptedConnection = useRef(false);

  useEffect(() => {
    if (!hasAttemptedConnection.current) {
      hasAttemptedConnection.current = true;

      websocketService.connect(
        () => {
          console.log('WebSocket connected successfully');
          setIsConnected(true);
        },
        (error) => {
          console.error('WebSocket connection error:', error);
          setIsConnected(false);
        }
      );
    }

    return () => {
      websocketService.disconnect();
      setIsConnected(false);
      hasAttemptedConnection.current = false;
    };
  }, []);

  return {
    isConnected,
    subscribe: (topic: string, handler: MessageHandler) => {
      websocketService.subscribe(topic, handler);
    },
    unsubscribe: (topic: string) => {
      websocketService.unsubscribe(topic);
    },
    send: (destination: string, body: any) => {
      websocketService.send(destination, body);
    },
    subscribeToRoom: (roomCode: string, handler: MessageHandler) => {
      websocketService.subscribeToRoom(roomCode, handler);
    },
    subscribeToRound: (roundId: string, handler: MessageHandler) => {
      websocketService.subscribeToRound(roundId, handler);
    },
    subscribeToPlayer: (playerId: string, handler: MessageHandler) => {
      websocketService.subscribeToPlayer(playerId, handler);
    },
  };
};

export default useWebSocket;
