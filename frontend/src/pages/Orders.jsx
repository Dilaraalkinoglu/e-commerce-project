import React, { useState, useEffect } from 'react';
import orderService from '../services/orderService';
import '../styles/orders.css';
import { useNavigate, Link } from 'react-router-dom';

const Orders = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        try {
            const data = await orderService.getMyOrders();
            // Sort by createdAt desc if not already
            const sorted = data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            setOrders(sorted);
            setLoading(false);
        } catch (err) {
            console.error(err);
            setError('Siparişler yüklenirken bir sorun oluştu.');
            setLoading(false);
        }
    };

    if (loading) return <div className="loading">Yükleniyor...</div>;

    if (!orders || orders.length === 0) {
        return (
            <div className="orders-container">
                <h1 className="orders-title">Siparişlerim</h1>
                <div style={{ textAlign: 'center', margin: '50px' }}>
                    <p>Henüz hiç siparişiniz yok.</p>
                    <button
                        onClick={() => navigate('/')}
                        style={{ marginTop: '20px', padding: '10px 20px', backgroundColor: 'var(--primary-color)', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                    >
                        Alışverişe Başla
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="orders-container">
            <h1 className="orders-title">Siparişlerim</h1>

            {error && <div className="error-message">{error}</div>}

            <div className="orders-list">
                {orders.map(order => (
                    <div key={order.orderId} className="order-card">
                        <div className="order-header">
                            <div>
                                <div className="order-id">Sipariş #{order.orderId}</div>
                                <div className="order-date">{new Date(order.createdAt).toLocaleDateString()} {new Date(order.createdAt).toLocaleTimeString()}</div>
                            </div>
                            <div className={`order-status ${order.status?.toLowerCase()}`}>
                                {order.status}
                            </div>
                        </div>

                        <div className="order-body">
                            <div className="order-items">
                                {order.items.map(item => (
                                    <div key={item.productId} className="order-item">
                                        <div className="item-name">
                                            <Link to={`/product/${item.productId}`} className="product-title-link">
                                                {item.productName}
                                            </Link>
                                        </div>
                                        <div className="item-meta">
                                            {item.quantity} x ${item.unitPriceSnapshot?.toFixed(2)}
                                        </div>
                                    </div>
                                ))}
                            </div>
                            <div className="order-summary-section">
                                <div className="total-label">Toplam Tutar</div>
                                <div className="total-price">${order.totalPrice?.toFixed(2)}</div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Orders;
