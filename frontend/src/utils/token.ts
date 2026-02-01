/**
 * JWT Token Management Utilities
 */

const TOKEN_KEY = 'poker_auth_token';

export const tokenUtils = {
  /**
   * Store JWT token in localStorage
   */
  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  },

  /**
   * Retrieve JWT token from localStorage
   */
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  },

  /**
   * Remove JWT token from localStorage
   */
  removeToken(): void {
    localStorage.removeItem(TOKEN_KEY);
  },

  /**
   * Check if user has a valid token
   */
  hasToken(): boolean {
    return !!this.getToken();
  },

  /**
   * Get Authorization header value
   */
  getAuthHeader(): string | null {
    const token = this.getToken();
    return token ? `Bearer ${token}` : null;
  }
};
