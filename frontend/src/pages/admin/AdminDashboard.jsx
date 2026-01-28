import React, { useEffect, useState } from 'react';
import adminService from '../../services/adminService';
import { FaUsers, FaBoxOpen, FaChartLine } from 'react-icons/fa';
import '../../styles/adminDashboard.css';

const AdminDashboard = () => {
    const [metrics, setMetrics] = useState({
        userCount: 0,
        productCount: 0,
        requestStats: {}
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchMetrics();
    }, []);

    const fetchMetrics = async () => {
        try {
            const [userData, productData, requestData] = await Promise.all([
                adminService.getUserMetrics(),
                adminService.getProductMetrics(),
                adminService.getRequestMetrics()
            ]);

            // requestData is structured as: { "GET": { 200: 5, 404: 1 }, "POST": { 201: 3 } }
            // We need to flatten this to: { 200: 5, 404: 1, 201: 3 } aggregating counts
            const flattenedStats = {};
            if (requestData && typeof requestData === 'object') {
                Object.values(requestData).forEach(methodMap => {
                    if (methodMap && typeof methodMap === 'object') {
                        Object.entries(methodMap).forEach(([code, count]) => {
                            flattenedStats[code] = (flattenedStats[code] || 0) + count;
                        });
                    }
                });
            }

            setMetrics({
                userCount: userData.totalUserCount,
                productCount: productData.totalProductCount,
                requestStats: flattenedStats
            });
            setLoading(false);
        } catch (err) {
            console.error("Failed to load metrics", err);
            setLoading(false);
        }
    };

    if (loading) return <div className="loading">Panel yükleniyor...</div>;

    return (
        <div className="admin-dashboard-container">
            <h1 className="admin-title">Yönetici Paneli</h1>

            <div className="dashboard-stats-grid">
                {/* User Count Card */}
                <div className="stat-card">
                    <div className="stat-icon user-icon">
                        <FaUsers />
                    </div>
                    <div className="stat-info">
                        <h3>Toplam Kullanıcı</h3>
                        <p className="stat-value">{metrics.userCount}</p>
                    </div>
                </div>

                {/* Product Count Card */}
                <div className="stat-card">
                    <div className="stat-icon product-icon">
                        <FaBoxOpen />
                    </div>
                    <div className="stat-info">
                        <h3>Toplam Ürün</h3>
                        <p className="stat-value">{metrics.productCount}</p>
                    </div>
                </div>

                {/* Request Stats Card (Simple Summary) */}
                <div className="stat-card">
                    <div className="stat-icon chart-icon">
                        <FaChartLine />
                    </div>
                    <div className="stat-info">
                        <h3>Toplam İstek</h3>
                        {/* Summing up all request counts */}
                        <p className="stat-value">
                            {Object.values(metrics.requestStats).reduce((a, b) => a + b, 0)}
                        </p>
                    </div>
                </div>
            </div>

            <div className="dashboard-charts-section">
                <h2>Sistem Durumu (HTTP Durum Kodları)</h2>
                <div className="status-codes-grid">
                    {Object.entries(metrics.requestStats).map(([status, count]) => (
                        <div key={status} className={`status-code-item status-${status.charAt(0)}xx`}>
                            <span className="status-label">HTTP {status}</span>
                            <span className="status-count">{count}</span>
                        </div>
                    ))}
                    {Object.keys(metrics.requestStats).length === 0 && (
                        <p>Henüz istek verisi yok.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
