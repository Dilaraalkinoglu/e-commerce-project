import api from './api';

const productService = {
    getAllProducts: async () => {
        const response = await api.get('/products');
        return response.data;
    },

    getProductById: async (id) => {
        const response = await api.get(`/products/${id}`);
        return response.data;
    },

    searchProducts: async (params) => {
        // params: { name, categoryId, minPrice, maxPrice, sortBy, direction, page, size }
        const response = await api.get('/products/search', { params });
        return response.data;
    },
};

export default productService;
