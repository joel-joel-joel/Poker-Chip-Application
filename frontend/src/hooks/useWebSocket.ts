/**
 * useWebSocket Hook
 * Manages WebSocket connection and subscriptions
 */

import { useEffect, useRef } from 'react';
import { websocketService } from '../services/websocket';

type MessageHandler = (message: any) => void;

export const useWebSocket = () => {
  const isConnectedRef = useRef(false);

  useEffect(() => {
    // Connect on mount
    if (!isConnectedRef.current) {
      websocketService.connect(
        () => {
          isConnectedRef.current = true;
        },
        (error) => {
          console.error('WebSocket connection error:', error);
        }
      );
    }

    // Disconnect on unmount
    return () => {
      if (isConnectedRef.current) {
        websocketService.disconnect();
        isConnectedRef.current = false;
      }
    };
  }, []);

  return {
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
    isConnected: () => websocketService.isWebSocketConnected()
  };
};

export default useWebSocket;
