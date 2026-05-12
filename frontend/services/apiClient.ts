import axios from 'axios';

const BACKEND_URL = process.env.EXPO_PUBLIC_BACKEND_URL || 'http://10.0.2.2:8080';

export const apiClient = axios.create({
  baseURL: BACKEND_URL,
  headers: {
    'Content-Type': 'application/json',
    // Ngrok header for dev environment, safely ignored in production
    'ngrok-skip-browser-warning': 'true',
  },
  timeout: 30000, // 30 seconds max timeout for long operations like OCR/AI
});

// Optional: Add request interceptors here for Auth Tokens
apiClient.interceptors.request.use(
  (config) => {
    // Example: Add token if required
    // const token = await AsyncStorage.getItem('token');
    // if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

// Optional: Add response interceptors for global error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);
