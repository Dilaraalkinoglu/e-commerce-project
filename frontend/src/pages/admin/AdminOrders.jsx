import React, { useState, useEffect } from 'react';
import adminService from '../../services/adminService';
import { useNavigate } from 'react-router-dom';

const AdminOrders = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        try {
            setLoading(true);
            const data = await adminService.getAllOrders();
            // Sort by ID desc (newest first)
            data.sort((a, b) => b.orderId - a.orderId);
            setOrders(data);
            setLoading(false);
        } catch (err) {
            console.error(err);
            setLoading(false);
        }
    };

    const handleStatusChange = async (id, newStatus) => {
        try {
            await adminService.updateOrderStatus(id, newStatus);
            // Refresh orders to show updated status
            fetchOrders();
        } catch (err) {
            console.error(err);
            alert('Durum güncellenemedi');
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'PENDING': return '#f59e0b'; // Turuncu
            case 'SHIPPED': return '#3b82f6'; // Mavi
            case 'DELIVERED': return '#10b981'; // Yeşil
            case 'CANCELLED': return '#ef4444'; // Kırmızı
            default: return '#6b7280'; // Gri
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

    if (loading) return <div>Siparişler yükleniyor...</div>;

    return (
        <div>
            <div className="admin-header">
                <h1 className="admin-title">Sipariş Yönetimi</h1>
            </div>

            <div className="admin-table-container">
                <table className="admin-table">
                    <thead>
                        <tr>
                            <th>Sipariş No</th>
                            <th>Müşteri</th>
                            <th>Tarih</th>
                            <th>Tutar</th>
                            <th>Ürünler</th>
                            <th>Durum</th>
                            <th>İşlemler</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map(order => (
                            <tr key={order.orderId}>
                                <td>#{order.orderId}</td>
                                <td>
                                    <div>{order.userName}</div>
                                    <small style={{ color: '#666' }}>{order.email}</small>
                                </td>
                                <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                                <td>${order.totalPrice.toFixed(2)}</td>
                                <td>
                                    {order.items.length} ürün
                                    <div style={{ fontSize: '0.8rem', color: '#666' }}>
                                        {order.items.map(item => (
                                            <div key={item.productName}>{item.productName} (x{item.quantity})</div>
                                        ))}
                                    </div>
                                </td>
                                <td>
                                    <span style={{
                                        backgroundColor: getStatusColor(order.status) + '20',
                                        color: getStatusColor(order.status),
                                        padding: '4px 8px',
                                        borderRadius: '4px',
                                        fontWeight: 'bold',
                                        fontSize: '0.85rem'
                                    }}>
                                        {getStatusLabel(order.status)}
                                    </span>
                                </td>
                                <td>
                                    <select
                                        value={order.status}
                                        onChange={(e) => handleStatusChange(order.orderId, e.target.value)}
                                        className="form-select"
                                        style={{ fontSize: '0.9rem', padding: '5px' }}
                                    >
                                        <option value="PENDING">Beklemede</option>
                                        <option value="SHIPPED">Kargolandı</option>
                                        <option value="DELIVERED">Teslim Edildi</option>
                                        <option value="CANCELLED">İptal Edildi</option>
                                    </select>
                                </td>
                            </tr>
                        ))}
                        {orders.length === 0 && (
                            <tr>
                                <td colSpan="7" style={{ textAlign: 'center' }}>Sipariş bulunamadı.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AdminOrders;
