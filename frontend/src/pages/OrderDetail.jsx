import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import orderService from '../services/orderService';
import { FaArrowLeft, FaBox, FaMapMarkerAlt, FaCalendarAlt, FaMoneyBillWave } from 'react-icons/fa';
import '../styles/orderDetail.css';

const OrderDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchOrder();
    }, [id]);

    const fetchOrder = async () => {
        try {
            setLoading(true);
            const data = await orderService.getOrderById(id);
            setOrder(data);
            setLoading(false);
        } catch (err) {
            console.error(err);
            setError('Sipariş detayları yüklenirken hata oluştu.');
            setLoading(false);
        }
    };

    const getStatusLabel = (status) => {
        switch (status) {
            case 'PENDING': return 'Beklemede';
            case 'SHIPPED': return 'Kargolandı';
            case 'DELIVERED': return 'Teslim Edildi';
            case 'CANCELLED': return 'İptal Edildi';
            default: return status;
        }
    };

    if (loading) return <div className="loading-container">Yükleniyor...</div>;
    if (error) return <div className="error-container">{error} <button onClick={() => navigate('/orders')}>Geri Dön</button></div>;
    if (!order) return <div className="error-container">Sipariş bulunamadı.</div>;

    return (
        <div className="order-detail-container">
            <button className="btn-back" onClick={() => navigate('/orders')}>
                <FaArrowLeft /> Siparişlere Dön
            </button>

            <div className="order-header">
                <div>
                    <h1>Sipariş #{order.orderId}</h1>
                    <span className={`status-badge status-${order.status.toLowerCase()}`}>
                        {getStatusLabel(order.status)}
                    </span>
                </div>
                <div className="order-meta">
                    <div className="meta-item">
                        <FaCalendarAlt />
                        <span>{new Date(order.createdAt).toLocaleDateString()} {new Date(order.createdAt).toLocaleTimeString()}</span>
                    </div>
                </div>
            </div>

            <div className="order-content">
                <div className="order-items-section">
                    <h2><FaBox /> Ürünler</h2>
                    <div className="items-list">
                        {order.items.map((item, index) => (
                            <div key={index} className="order-item-card">
                                <div className="item-image-container">
                                    {item.imageUrl ? (
                                        <img src={`http://localhost:8080${item.imageUrl}`} alt={item.productName} className="order-item-img" />
                                    ) : (
                                        <div className="order-item-img-placeholder">Resim Yok</div>
                                    )}
                                </div>
                                <div className="item-info">
                                    <Link to={`/product/${item.productId}`} className="item-name">
                                        {item.productName}
                                    </Link>
                                    <div className="item-meta">
                                        Adet: {item.quantity}
                                    </div>
                                </div>
                                <div className="item-price">
                                    ${item.unitPriceSnapshot.toFixed(2)}
                                </div>
                                <div className="item-total">
                                    ${(item.quantity * item.unitPriceSnapshot).toFixed(2)}
                                </div>
                            </div>
                        ))}
                    </div>
                    <div className="order-summary">
                        <div className="summary-row total">
                            <span>Toplam Tutar</span>
                            <span>${order.totalPrice.toFixed(2)}</span>
                        </div>
                    </div>
                </div>

                <div className="order-sidebar">
                    <div className="sidebar-card address-card">
                        <h3><FaMapMarkerAlt /> Teslimat Adresi</h3>
                        {order.address ? (
                            <div className="address-details">
                                <strong>{order.address.title}</strong>
                                <p>{order.address.addressLine}</p>
                                <p>{order.address.city}, {order.address.state}</p>
                                <p>{order.address.postalCode}</p>
                                <p>{order.address.country}</p>
                            </div>
                        ) : (
                            <p className="no-address">Adres bilgisi yok.</p>
                        )}
                    </div>

                    <div className="sidebar-card customer-card">
                        <h3>Müşteri Bilgileri</h3>
                        <p><strong>Ad:</strong> {order.userName}</p>
                        <p><strong>E-posta:</strong> {order.email}</p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default OrderDetail;
