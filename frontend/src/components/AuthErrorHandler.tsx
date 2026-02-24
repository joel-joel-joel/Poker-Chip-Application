import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authEvents } from '../utils/authEvents';
import { useAuth } from '../hooks/useAuth';

export const AuthErrorHandler: React.FC = () => {
  const navigate = useNavigate();
  const { logout } = useAuth();

  useEffect(() => {
    const unsubscribe = authEvents.subscribe(() => {
      logout();
      navigate('/login', { replace: true });
    });

    return unsubscribe;
  }, [navigate, logout]);

  return null;
};
