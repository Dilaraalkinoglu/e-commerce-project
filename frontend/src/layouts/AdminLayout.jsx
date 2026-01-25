import React, { useEffect } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/admin.css';

const AdminLayout = () => {
    const { user, loading } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!loading && (!user || !user.role || !user.role.includes('ADMIN'))) {
            navigate('/');
        }
    }, [user, loading, navigate]);

    if (loading) return <div className="loading">Checking permissions...</div>;

    // Double check to prevent flash of content
    if (!user || (user.role && !user.role.includes('ADMIN') && user.role !== 'ADMIN')) return null;

    return (
        <div className="admin-container">
            <aside className="admin-sidebar">
                <nav>
                    <NavLink to="/admin/dashboard" className={({ isActive }) => `admin-sidebar-link ${isActive ? 'active' : ''}`}>
                        Dashboard
                    </NavLink>
                    <NavLink to="/admin/orders" className={({ isActive }) => `admin-sidebar-link ${isActive ? 'active' : ''}`}>
                        Orders
                    </NavLink>
                    <NavLink to="/admin/products" className={({ isActive }) => `admin-sidebar-link ${isActive ? 'active' : ''}`}>
                        Products
                    </NavLink>
                    <NavLink to="/admin/categories" className={({ isActive }) => `admin-sidebar-link ${isActive ? 'active' : ''}`}>
                        Categories
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
