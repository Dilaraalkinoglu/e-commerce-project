import api from './api';

const addressService = {
    getAddresses: async () => {
        const response = await api.get('/user/me/address');
        return response.data;
    },

    addAddress: async (addressData) => {
        const response = await api.post('/user/me/address', addressData);
        return response.data;
    },

    updateAddress: async (id, addressData) => {
        const response = await api.put(`/user/me/address/${id}`, addressData);
        return response.data;
    },

    deleteAddress: async (id) => {
        await api.delete(`/user/me/address/${id}`);
    }
};

export default addressService;
