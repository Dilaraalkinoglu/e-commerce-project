
import axios from 'axios';

export const BASE_URL = 'http://localhost:8080';

const api = axios.create({
    baseURL: `${BASE_URL}/api`, // Backend base URL
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor to add JWT token to requests
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default api;
