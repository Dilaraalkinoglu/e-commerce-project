
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
                <div className="navbar-links">
                    <Link to="/" className="nav-link">Home</Link>
                    {user ? (
                        <>
                            <span className="nav-user">Hello, {user.userName || user.sub}</span>
                            <button onClick={handleLogout} className="btn-logout">Logout</button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="nav-link">Login</Link>
                            <Link to="/register" className="nav-link btn-register">Register</Link>
                        </>
                    )}
                    <Link to="/cart" className="nav-icon">
                        <FaShoppingCart />
                    </Link>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
