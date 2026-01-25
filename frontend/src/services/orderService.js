import api from './api';

const orderService = {
    getMyOrders: async () => {
        const response = await api.get('/orders/my-orders');
        return response.data;
    },

    getOrderById: async (id) => {
        const response = await api.get(`/orders/${id}`); // Assuming we might add this later for user or admin
        return response.data;
    }
};

export default orderService;
