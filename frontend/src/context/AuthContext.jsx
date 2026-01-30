
import React, { createContext, useState, useEffect, useContext } from 'react';
import api from '../services/api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check if user is logged in
        const token = localStorage.getItem('token');
        if (token) {
            // Decode token or fetch user profile (optional, for now just expecting state persisted)
            // For simplicity, we might just assume valid if token exists, or call /me endpoint
            // Let's try to fetch user details to validate token
            checkUserLoggedIn();
        } else {
            setLoading(false);
        }
    }, []);

    const checkUserLoggedIn = async () => {
        try {
            const response = await api.get('/user/me');
            setUser(response.data);
        } catch (error) {
            console.error("Auth check failed", error);
            localStorage.removeItem('token');
        } finally {
            setLoading(false);
        }
    };

    const login = async (username, password) => {
        const response = await api.post('/auth/login', { userName: username, password });
        // Assuming backend returns { token: "..." } or similar
        // Based on previous API docs, login returns: { "additionalProp1": "string" } which is usually token map
        // Let's assume response.data is the token string map or plain text. 
        // Wait, the documentation said: type: "object", additionalProperties: "string".
        // Usually it is like { "token": "jwt..." }

        // We will verify this structure later. For now let's store response.data.token if object, or response.data if string.
        const token = response.data.token || response.data.accessToken || Object.values(response.data)[0];
        const refreshToken = response.data.refreshToken;

        if (token) {
            localStorage.setItem('token', token);
            if (refreshToken) localStorage.setItem('refreshToken', refreshToken);
            await checkUserLoggedIn();
            return true;
        }
        return false;
    };

    const register = async (userData) => {
        // userData: { userName, email, password }
        await api.post('/auth/register', userData);
        return true; // Register success
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
