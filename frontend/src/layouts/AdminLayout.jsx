import React, { useEffect } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/admin.css';

const AdminLayout = () => {
    const { user, loading } = useAuth();
    const navigate = useNavigate();

    // No longer redirecting in useEffect, but handling it in the render logic
    useEffect(() => {
        // This useEffect can be removed or kept for other side effects if needed.
        // For now, it's empty as the access check is handled in the render.
    }, [user, loading, navigate]);

    if (loading) return <div className="loading">Yetkiler kontrol ediliyor...</div>;

    // Show access denied message if user is not an admin
    if (!user || !user.role || !user.role.includes('ADMIN')) {
        return (
            <div style={{ padding: '50px', textAlign: 'center' }}>
                <h2 style={{ color: '#ef4444' }}>Erişim Reddedildi</h2>
                <p>Bu sayfayı görüntülemek için yetkiniz bulunmamaktadır.</p>
                <button
                    onClick={() => navigate('/')}
                    style={{
                        marginTop: '20px',
                        padding: '10px 20px',
                        backgroundColor: '#3b82f6',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer'
                    }}
                >
                    Ana Sayfaya Dön
                </button>
            </div>
        );
    }

    return (
        <div className="admin-container">
            <aside className="admin-sidebar">
                <nav>
                    <NavLink to="/admin/dashboard" className={({ isActive }) => `admin-sidebar-link ${isActive ? 'active' : ''}`}>
                        Kontrol Paneli
                    </NavLink>
                    <NavLink to="/admin/orders" className={({ isActive }) => `admin-sidebar-link ${isActive ? 'active' : ''}`}>
                        Siparişler
                    </NavLink>
                    <NavLink to="/admin/products" className={({ isActive }) => `admin-sidebar-link ${isActive ? 'active' : ''}`}>
                        Ürünler
                    </NavLink>
                    <NavLink to="/admin/categories" className={({ isActive }) => `admin-sidebar-link ${isActive ? 'active' : ''}`}>
                        Kategoriler
                    </NavLink>
                    <NavLink to="/admin/coupons" className={({ isActive }) => `admin-sidebar-link ${isActive ? 'active' : ''}`}>
                        Kuponlar
                    </NavLink>
                </nav>
            </aside>
            <main className="admin-content">
                <Outlet />
            </main>
        </div>
    );
};

export default AdminLayout;
