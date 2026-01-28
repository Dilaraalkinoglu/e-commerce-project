
import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/auth.css';

const Register = () => {
    const [formData, setFormData] = useState({ userName: '', email: '', password: '' });
    const [error, setError] = useState('');
    const { register } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const success = await register(formData);
            if (success) {
                // Redirect to login after successful registration
                navigate('/login');
            } else {
                setError('Kayıt başarısız.');
            }
        } catch (err) {
            setError('Kayıt sırasında bir hata oluştu. Başka bir kullanıcı adı deneyin.');
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <h2>Kayıt Ol</h2>
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
                            minLength={3}
                        />
                    </div>
                    <div className="form-group">
                        <label>E-posta</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
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
                            minLength={5}
                        />
                    </div>
                    <button type="submit" className="btn-primary">Kayıt Ol</button>
                </form>
                <p className="auth-footer">
                    Zaten hesabınız var mı? <Link to="/login">Giriş Yap</Link>
                </p>
            </div>
        </div>
    );
};

export default Register;
