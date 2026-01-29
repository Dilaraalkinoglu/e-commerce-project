import React, { useState } from 'react';
import api from '../services/api';
import '../styles/auth.css';

const ForgotPassword = () => {
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        setError('');
        setLoading(true);

        try {
            await api.post('/auth/forgot-password', { email });
            setMessage('Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.');
        } catch (err) {
            setError(err.response?.data?.message || err.response?.data || 'Bir hata oluştu.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <h2>Şifremi Unuttum</h2>
                <p style={{ marginBottom: '1rem', color: '#666' }}>E-posta adresinizi girin, size sıfırlama bağlantısı gönderelim.</p>
                {message && <div className="success-message" style={{ color: 'green', marginBottom: '10px', padding: '10px', backgroundColor: '#e6ffe6', borderRadius: '4px' }}>{message}</div>}
                {error && <div className="error-message">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>E-posta</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="btn-primary" disabled={loading}>
                        {loading ? 'Gönderiliyor...' : 'Gönder'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ForgotPassword;
