import api from './api';

const adminService = {
    // Categories
    getAllCategories: async () => {
        const response = await api.get('/admin/categories');
        return response.data;
    },

    createCategory: async (data) => {
        const response = await api.post('/admin/categories', data);
        return response.data;
    },

    updateCategory: async (id, data) => {
        const response = await api.put(`/admin/categories/${id}`, data);
        return response.data;
    },

    deleteCategory: async (id) => {
        await api.delete(`/admin/categories/${id}`);
    },

    // Products
    getAllProducts: async (page = 0, size = 10) => {
        const response = await api.get(`/admin/products/paginated?page=${page}&size=${size}`);
        return response.data;
    },

    getProductById: async (id) => {
        const response = await api.get(`/admin/products/${id}`);
        return response.data;
    },

    createProduct: async (data) => {
        const response = await api.post('/admin/products', data);
        return response.data;
    },

    updateProduct: async (id, data) => {
        const response = await api.put(`/admin/products/${id}`, data);
        return response.data;
    },

    deleteProduct: async (id) => {
        await api.delete(`/admin/products/${id}`);
    },

    // Orders
    getAllOrders: async () => {
        const response = await api.get('/admin/orders');
        return response.data;
    },

    updateOrderStatus: async (id, status) => {
        const response = await api.put(`/admin/orders/${id}/status?status=${status}`);
        return response.data;
    },

    uploadProductImages: async (id, files) => {
        const formData = new FormData();
        for (let i = 0; i < files.length; i++) {
            formData.append('files', files[i]);
        }

        const response = await api.post(`/admin/products/${id}/images`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return response.data;
    },

    // Metrics
    getUserMetrics: async () => {
        const response = await api.get('/admin/metrics/users');
        return response.data;
    },

    getProductMetrics: async () => {
        const response = await api.get('/admin/metrics/products');
        return response.data;
    },

    getRequestMetrics: async () => {
        const response = await api.get('/admin/metrics/requests');
        return response.data;
    }
};

export default adminService;
