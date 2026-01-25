
import api from './api';

const userService = {
    getProfile: async () => {
        const response = await api.get('/user/me');
        return response.data;
    },

    updateProfile: async (data) => {
        const response = await api.put('/user/me', data);
        return response.data;
    },

    updatePassword: async (data) => {
        const response = await api.patch('/user/password', data);
        return response.data;
    }
};

export default userService;
