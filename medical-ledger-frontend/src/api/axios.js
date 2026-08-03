import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Handle response errors - DON'T auto-redirect, let AuthContext handle it
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Don't auto-logout here - AuthContext will handle it
    return Promise.reject(error);
  }
);

export default api;