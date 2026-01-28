
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/navbar.css';
import { FaShoppingCart, FaUser } from 'react-icons/fa';

const Navbar = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [isMenuOpen, setIsMenuOpen] = React.useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="navbar-logo">
                    Omnis
                </Link>
                <div className="navbar-center-links">
                    <Link to="/" className="nav-link">Ana Sayfa</Link>
                    {user && <Link to="/addresses" className="nav-link">Adreslerim</Link>}
                    {user && <Link to="/orders" className="nav-link">Siparişlerim</Link>}
                </div>

                <div className="navbar-right-actions">
                    {user ? (
                        <div className="user-dropdown-container">
                            <div
                                className="user-info"
                                onClick={() => setIsMenuOpen(!isMenuOpen)}
                                style={{ cursor: 'pointer', position: 'relative' }}
                            >
                                <FaUser className="user-icon" />
                                <span className="user-name">{user.userName || user.sub}</span>
                            </div>

                            {isMenuOpen && (
                                <div className="dropdown-menu">
                                    <Link
                                        to="/profile"
                                        className="dropdown-item"
                                        onClick={() => setIsMenuOpen(false)}
                                    >
                                        Profilim
                                    </Link>

                                    {(user?.role === 'ADMIN' || user?.role?.includes('ADMIN')) && (
                                        <Link
                                            to="/admin"
                                            className="dropdown-item admin-link"
                                            onClick={() => setIsMenuOpen(false)}
                                        >
                                            Yönetici Paneli
                                        </Link>
                                    )}

                                    <button
                                        onClick={() => {
                                            setIsMenuOpen(false);
                                            handleLogout();
                                        }}
                                        className="dropdown-item logout-btn"
                                    >
                                        Çıkış
                                    </button>
                                </div>
                            )}
                        </div>
                    ) : (
                        <>
                            <Link to="/login" className="nav-link">Giriş Yap</Link>
                            <Link to="/register" className="nav-link btn-register">Kayıt Ol</Link>
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
