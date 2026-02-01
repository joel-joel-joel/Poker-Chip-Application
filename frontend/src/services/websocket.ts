/**
 * WebSocket Service
 * Handles real-time game updates using SockJS and STOMP
 */

import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { tokenUtils } from '../utils/token';

type MessageHandler = (message: any) => void;

class WebSocketService {
  private client: Client | null = null;
  private subscriptions: Map<string, any> = new Map();
  private isConnected = false;

  /**
   * Connect to WebSocket server
   */
  connect(onConnect?: () => void, onError?: (error: any) => void): void {
    if (this.isConnected) {
      console.log('WebSocket already connected');
      return;
    }

    // Create SockJS connection
    const socket = new SockJS('http://localhost:8080/ws');

    // Create STOMP client
    this.client = new Client({
      webSocketFactory: () => socket as any,
      connectHeaders: {
        Authorization: tokenUtils.getAuthHeader() || ''
      },
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    // Set up callbacks
    this.client.onConnect = () => {
      console.log('WebSocket connected');
      this.isConnected = true;
      if (onConnect) onConnect();
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP error:', frame);
      this.isConnected = false;
      if (onError) onError(frame);
    };

    this.client.onWebSocketClose = () => {
      console.log('WebSocket closed');
      this.isConnected = false;
    };

    // Activate connection
    this.client.activate();
  }

  /**
   * Disconnect from WebSocket server
   */
  disconnect(): void {
    if (this.client) {
      // Unsubscribe from all topics
      this.subscriptions.forEach((subscription) => {
        subscription.unsubscribe();
      });
      this.subscriptions.clear();

      // Deactivate client
      this.client.deactivate();
      this.isConnected = false;
      console.log('WebSocket disconnected');
    }
  }

  /**
   * Subscribe to a topic
   */
  subscribe(topic: string, handler: MessageHandler): void {
    if (!this.client || !this.isConnected) {
      console.error('Cannot subscribe: WebSocket not connected');
      return;
    }

    // Unsubscribe if already subscribed to this topic
    if (this.subscriptions.has(topic)) {
      this.subscriptions.get(topic).unsubscribe();
    }

    // Subscribe to topic
    const subscription = this.client.subscribe(topic, (message: IMessage) => {
      try {
        const data = JSON.parse(message.body);
        handler(data);
      } catch (error) {
        console.error('Error parsing WebSocket message:', error);
      }
    });

    this.subscriptions.set(topic, subscription);
    console.log(`Subscribed to ${topic}`);
  }

  /**
   * Unsubscribe from a topic
   */
  unsubscribe(topic: string): void {
    const subscription = this.subscriptions.get(topic);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(topic);
      console.log(`Unsubscribed from ${topic}`);
    }
  }

  /**
   * Send a message to a destination
   */
  send(destination: string, body: any): void {
    if (!this.client || !this.isConnected) {
      console.error('Cannot send message: WebSocket not connected');
      return;
    }

    this.client.publish({
      destination,
      body: JSON.stringify(body)
    });
  }

  /**
   * Subscribe to room updates
   */
  subscribeToRoom(roomCode: string, handler: MessageHandler): void {
    this.subscribe(`/topic/room/${roomCode}`, handler);
  }

  /**
   * Subscribe to round updates
   */
  subscribeToRound(roundId: string, handler: MessageHandler): void {
    this.subscribe(`/topic/round/${roundId}`, handler);
  }

  /**
   * Subscribe to player updates
   */
  subscribeToPlayer(playerId: string, handler: MessageHandler): void {
    this.subscribe(`/topic/player/${playerId}`, handler);
  }

  /**
   * Check if connected
   */
  isWebSocketConnected(): boolean {
    return this.isConnected;
  }
}

// Export singleton instance
export const websocketService = new WebSocketService();
export default websocketService;
