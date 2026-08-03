import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import api from '../api/axios';
import toast from 'react-hot-toast';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    // Initialize from localStorage
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  // Fetch user profile only if token exists
  useEffect(() => {
    if (token) {
      fetchUserProfile();
    } else {
      setLoading(false);
    }
  }, []); // Only run once on mount, not on every token change

  const fetchUserProfile = async () => {
    try {
      const response = await api.get('/auth/me');
      if (response.data.success && response.data.data) {
        const userData = response.data.data;
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
      } else {
        throw new Error('Invalid response');
      }
    } catch (error) {
      console.error('Failed to fetch user profile:', error);
      // Only logout if it's a 401 (token expired/invalid)
      if (error.response?.status === 401) {
        performLogout();
      }
      // For other errors (network, 500), keep the user from localStorage
    } finally {
      setLoading(false);
    }
  };

  const register = async (userData) => {
    try {
      const response = await api.post('/auth/register', userData);
      if (response.data.success && response.data.data) {
        const { token: newToken, user: newUser } = response.data.data;
        localStorage.setItem('token', newToken);
        localStorage.setItem('user', JSON.stringify(newUser));
        setToken(newToken);
        setUser(newUser);
        toast.success('Registration successful!');
        return true;
      }
      return false;
    } catch (error) {
      const message = error.response?.data?.message || 'Registration failed';
      toast.error(message);
      return false;
    }
  };

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      if (response.data.success && response.data.data) {
        const { token: newToken, user: newUser } = response.data.data;
        localStorage.setItem('token', newToken);
        localStorage.setItem('user', JSON.stringify(newUser));
        setToken(newToken);
        setUser(newUser);
        toast.success('Login successful!');
        return true;
      }
      toast.error('Login failed: Invalid response');
      return false;
    } catch (error) {
      const message = error.response?.data?.message || 'Login failed';
      toast.error(message);
      return false;
    }
  };

  const performLogout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  }, []);

  const logout = () => {
    performLogout();
    toast.success('Logged out successfully');
  };

  const value = {
    user,
    token,
    loading,
    register,
    login,
    logout,
    isAuthenticated: !!token && !!user,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};