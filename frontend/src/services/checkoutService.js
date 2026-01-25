import api from './api';

const checkoutService = {
    checkout: async (checkoutData) => {
        // checkoutData: { addressId: Long, paymentMethod: String }
        const response = await api.post('/checkout', checkoutData);
        return response.data;
    }
};

export default checkoutService;
