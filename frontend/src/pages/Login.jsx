
import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/auth.css';

const Login = () => {
    const [formData, setFormData] = useState({ userName: '', password: '' });
    const [error, setError] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const success = await login(formData.userName, formData.password);
            if (success) {
                navigate('/');
            } else {
                setError('Giriş başarısız. Lütfen bilgilerinizi kontrol edin.');
            }
        } catch (err) {
            console.error("Login Error:", err);
            // Check if it is an Axios error response
            if (err.response && err.response.data) {
                // Try to grab the message from standard Spring Boot error bodies or custom ones
                // Usually: { "status": 401, "error": "Unauthorized", "message": "Bad credentials", ... }
                const msg = err.response.data.message || err.response.data.error || 'Sunucu hatası';
                setError(`Giriş başarısız: ${msg}`);
            } else if (err.request) {
                setError('Bağlantı hatası. Sunucuya ulaşılamıyor.');
            } else {
                setError(`Hata: ${err.message}`);
            }
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <h2>Giriş Yap</h2>
                {error && <div className="error-message">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Kullanıcı Adı</label>
                        <input
                            type="text"
                            name="userName"
                            value={formData.userName}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Şifre</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <button type="submit" className="btn-primary">Giriş Yap</button>
                </form>
                <p className="auth-footer">
                    Hesabınız yok mu? <Link to="/register">Kayıt Ol</Link>
                </p>
            </div>
        </div>
    );
};

export default Login;
