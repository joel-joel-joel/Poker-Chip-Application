/**
 * Authentication Service
 * Handles user registration, login, and token management
 */

import { apiClient } from './api';
import { tokenUtils } from '../utils/token';
import { AuthResponse, RegisterRequest, UserDTO } from '../types/api.types';

export const authService = {
  /**
   * Register a new user
   * POST /api/auth/register
   */
  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/api/auth/register', data);

    // Store token automatically after successful registration
    if (response.token) {
      tokenUtils.setToken(response.token);
    }

    return response;
  },

  /**
   * Check if username is available
   * GET /api/auth/check-username?username=X
   */
  async checkUsernameAvailability(username: string): Promise<boolean> {
    try {
      const response = await apiClient.get<{ available: boolean }>(
        '/api/auth/check-username',
        { username }
      );
      return response.available;
    } catch (error) {
      console.error('Error checking username:', error);
      return false;
    }
  },

  /**
   * Check if email is available
   * GET /api/auth/check-email?email=X
   */
  async checkEmailAvailability(email: string): Promise<boolean> {
    try {
      const response = await apiClient.get<{ available: boolean }>(
        '/api/auth/check-email',
        { email }
      );
      return response.available;
    } catch (error) {
      console.error('Error checking email:', error);
      return false;
    }
  },

  /**
   * Get current user profile
   * GET /api/user/me
   */
  async getCurrentUser(): Promise<UserDTO> {
    return apiClient.get<UserDTO>('/api/user/me', undefined, true);
  },

  /**
   * Logout user (clear token)
   */
  logout(): void {
    tokenUtils.removeToken();
  },

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return tokenUtils.hasToken();
  }
};

export default authService;
