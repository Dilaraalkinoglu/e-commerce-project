
import api from './api';

const cartService = {
    getCart: async () => {
        const response = await api.get('/carts');
        return response.data;
    },

    addItem: async (productId, quantity = 1) => {
        const response = await api.post('/carts/items', { productId, quantity });
        return response.data;
    },

    removeItem: async (productId) => {
        const response = await api.delete(`/carts/items/${productId}`);
        return response.data;
    },

    updateItem: async (productId, quantity) => {
        const response = await api.put(`/carts/items/${productId}?quantity=${quantity}`);
        return response.data;
    },

    clearCart: async () => {
        const response = await api.delete('/carts/clear');
        return response.data;
    }
};

export default cartService;
