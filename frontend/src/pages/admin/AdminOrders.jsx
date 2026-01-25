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
            alert('Failed to update status');
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'PENDING': return '#f59e0b'; // Orange
            case 'SHIPPED': return '#3b82f6'; // Blue
            case 'DELIVERED': return '#10b981'; // Green
            case 'CANCELLED': return '#ef4444'; // Red
            default: return '#6b7280'; // Gray
        }
    };

    if (loading) return <div>Loading orders...</div>;

    return (
        <div>
            <div className="admin-header">
                <h1 className="admin-title">Orders Management</h1>
            </div>

            <div className="admin-table-container">
                <table className="admin-table">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Customer</th>
                            <th>Date</th>
                            <th>Total</th>
                            <th>Items</th>
                            <th>Status</th>
                            <th>Actions</th>
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
                                    {order.items.length} items
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
                                        {order.status}
                                    </span>
                                </td>
                                <td>
                                    <select
                                        value={order.status}
                                        onChange={(e) => handleStatusChange(order.orderId, e.target.value)}
                                        className="form-select"
                                        style={{ fontSize: '0.9rem', padding: '5px' }}
                                    >
                                        <option value="PENDING">PENDING</option>
                                        <option value="SHIPPED">SHIPPED</option>
                                        <option value="DELIVERED">DELIVERED</option>
                                        <option value="CANCELLED">CANCELLED</option>
                                    </select>
                                </td>
                            </tr>
                        ))}
                        {orders.length === 0 && (
                            <tr>
                                <td colSpan="7" style={{ textAlign: 'center' }}>No orders found.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AdminOrders;
