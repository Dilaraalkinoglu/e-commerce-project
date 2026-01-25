
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/navbar.css';
import { FaShoppingCart, FaUser } from 'react-icons/fa';

const Navbar = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="navbar-logo">
                    E-Commerce
                </Link>
                <div className="navbar-center-links">
                    <Link to="/" className="nav-link">Home</Link>
                    {user && <Link to="/addresses" className="nav-link">My Addresses</Link>}
                    {user && <Link to="/orders" className="nav-link">My Orders</Link>}
                    {(user?.role === 'ADMIN' || user?.role?.includes('ADMIN')) && (
                        <Link to="/admin" className="nav-link" style={{ color: 'var(--primary-color)', fontWeight: 'bold' }}>Admin Panel</Link>
                    )}
                </div>

                <div className="navbar-right-actions">
                    {user ? (
                        <>
                            <Link to="/profile" className="user-info" style={{ textDecoration: 'none', cursor: 'pointer' }}>
                                <FaUser className="user-icon" />
                                <span className="user-name">{user.userName || user.sub}</span>
                            </Link>
                            <button onClick={handleLogout} className="btn-logout">Logout</button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="nav-link">Login</Link>
                            <Link to="/register" className="nav-link btn-register">Register</Link>
                        </>
                    )}
                    <Link to="/cart" className="nav-icon-cart">
                        <FaShoppingCart />
                    </Link>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
