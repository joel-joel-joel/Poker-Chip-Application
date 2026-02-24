/**
 * Authentication Context Provider
 * Manages global authentication state
 */

import React, { createContext, useState, useEffect, ReactNode } from 'react';
import { authService } from '../services/authService';
import { UserDTO } from '../types/api.types';

interface AuthContextType {
  user: UserDTO | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (token: string) => Promise<void>;
  logout: () => void;
  refreshUser: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<UserDTO | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  /**
   * Load user on mount if token exists
   */
  useEffect(() => {
    const loadUser = async () => {
      try {
        if (authService.isAuthenticated()) {
          try {
            const userData = await authService.getCurrentUser();
            setUser(userData);
          } catch (error) {
            console.error('Failed to load user:', error);
            authService.logout();
          }
        }
      } catch (error) {
        console.error('Error in auth check:', error);
      } finally {
        setIsLoading(false);
      }
    };

    loadUser();
  }, []);

  /**
   * Login user with token
   */
  const login = async (token: string) => {
    try {
      const userData = await authService.getCurrentUser();
      setUser(userData);
    } catch (error) {
      console.error('Failed to login:', error);
      throw error;
    }
  };

  /**
   * Logout user
   */
  const logout = () => {
    authService.logout();
    setUser(null);
  };

  /**
   * Refresh user data
   */
  const refreshUser = async () => {
    try {
      const userData = await authService.getCurrentUser();
      setUser(userData);
    } catch (error) {
      console.error('Failed to refresh user:', error);
      logout();
    }
  };

  const value: AuthContextType = {
    user,
    isAuthenticated: !!user,
    isLoading,
    login,
    logout,
    refreshUser
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
