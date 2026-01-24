
import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { useCart } from '../context/CartContext';
import '../styles/home.css';

const Home = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { addToCart } = useCart();

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            // Endpoint based on backend API docs: /api/products
            const response = await api.get('/products');
            setProducts(response.data);
            setLoading(false);
        } catch (err) {
            console.error("Failed to fetch products", err);
            setError('Failed to load products.');
            setLoading(false);
        }
    };

    if (loading) return <div className="loading">Loading products...</div>;
    if (error) return <div className="error">{error}</div>;

    return (
        <div className="home-container">
            <header className="hero">
                <h1>Welcome to Our Store</h1>
                <p>Discover the best products for you.</p>
            </header>

            <section className="product-grid">
                {products.map((product) => (
                    <div key={product.id} className="product-card">
                        <div className="product-image-placeholder">
                            {/* Use first image if available, else placeholder */}
                            {product.images && product.images.length > 0 ? (
                                <img src={product.images[0].imageUrl} alt={product.name} />
                            ) : (
                                <div className="placeholder-img">No Image</div>
                            )}
                        </div>
                        <div className="product-info">
                            <h3>{product.name}</h3>
                            <p className="price">${product.price.toFixed(2)}</p>
                            <button
                                className="btn-add-cart"
                                onClick={() => addToCart(product.id)}
                            >
                                Add to Cart
                            </button>
                        </div>
                    </div>
                ))}
            </section>
        </div>
    );
};

export default Home;
