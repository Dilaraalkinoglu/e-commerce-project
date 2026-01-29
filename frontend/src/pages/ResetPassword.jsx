import React, { useState } from 'react';
import '../styles/auth.css';
import { useSearchParams, useNavigate } from 'react-router-dom';
// import api from '../services/api'; // Enable when backend is ready

const ResetPassword = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');

    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            setError("Şifreler eşleşmiyor");
            return;
        }

        setMessage('');
        setError('');
        setLoading(true);

        try {
            // TBD: Update backend to handle reset-password with token
            // await api.post('/auth/reset-password', { token, newPassword: password });

            // Mocking behavior for now as per current backend status
            console.log("Reset password for token:", token, "New Password:", password);

            setTimeout(() => {
                setMessage('Şifreniz başarıyla sıfırlandı. Giriş sayfasına yönlendiriliyorsunuz...');
                setTimeout(() => navigate('/login'), 2000);
            }, 1000);

        } catch (err) {
            setError(err.response?.data?.message || 'Bir hata oluştu.');
        } finally {
            setLoading(false);
        }
    };

    if (!token) {
        return <div className="auth-container"><div className="auth-card"><p>Geçersiz bağlantı.</p></div></div>;
    }

    return (
        <div className="auth-container">
            <div className="auth-card">
                <h2>Yeni Şifre Belirle</h2>
                {message && <div className="success-message" style={{ color: 'green', marginBottom: '10px' }}>{message}</div>}
                {error && <div className="error-message">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Yeni Şifre</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Şifre Tekrar</label>
                        <input
                            type="password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="btn-primary" disabled={loading}>
                        {loading ? 'Güncelle' : 'Şifreyi Güncelle'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ResetPassword;
