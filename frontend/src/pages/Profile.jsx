
import React, { useState, useEffect } from 'react';
import userService from '../services/userService';
import { useAuth } from '../context/AuthContext';
import '../styles/profile.css'; // We will create this or use inline/main css

const Profile = () => {
    const { user: authUser } = useAuth(); // getting initial state if needed, but better fetch fresh
    const [formData, setFormData] = useState({
        userName: '',
        email: ''
    });
    const [passwordData, setPasswordData] = useState({ currentPassword: '', newPassword: '' });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [passwordSaving, setPasswordSaving] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            const data = await userService.getProfile();
            setFormData({
                userName: data.userName,
                email: data.email
            });
            setLoading(false);
        } catch (err) {
            console.error(err);
            setMessage({ type: 'error', text: 'Profil yüklenemedi' });
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);
        setMessage({ type: '', text: '' });

        try {
            await userService.updateProfile(formData);
            setMessage({ type: 'success', text: 'Profil başarıyla güncellendi!' });
        } catch (err) {
            console.error(err);
            setMessage({ type: 'error', text: 'Profil güncellenemedi. E-posta kullanımda olabilir.' });
        } finally {
            setSaving(false);
        }
    };

    const handlePasswordSubmit = async (e) => {
        e.preventDefault();
        setPasswordSaving(true);
        setMessage({ type: '', text: '' });

        try {
            await userService.updatePassword(passwordData);
            setMessage({ type: 'success', text: 'Şifre başarıyla güncellendi!' });
            setPasswordData({ currentPassword: '', newPassword: '' });
        } catch (err) {
            console.error(err);
            setMessage({ type: 'error', text: 'Şifre güncellenemedi. Lütfen mevcut şifrenizi kontrol edin.' });
        } finally {
            setPasswordSaving(false);
        }
    };

    if (loading) return <div className="loading-container">Yükleniyor...</div>;

    return (
        <div className="profile-container">
            <h1 className="profile-title">Profilim</h1>

            {message.text && (
                <div className={`message-alert ${message.type}`}>
                    {message.text}
                </div>
            )}

            <div className="profile-card">
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Kullanıcı Adı</label>
                        <input
                            type="text"
                            name="userName"
                            className="form-input"
                            value={formData.userName}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">E-posta</label>
                        <input
                            type="email"
                            name="email"
                            className="form-input"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Rol</label>
                        <input
                            type="text"
                            className="form-input disabled"
                            value={authUser?.role || 'USER'} // Display only
                            disabled
                        />
                        <small className="form-hint">Rol değiştirilemez.</small>
                    </div>

                    <button type="submit" className="btn-save" disabled={saving}>
                        {saving ? 'Kaydediliyor...' : 'Profili Güncelle'}
                    </button>
                </form>
            </div>

            <div className="profile-card" style={{ marginTop: '20px' }}>
                <h2 style={{ fontSize: '1.2rem', marginBottom: '20px' }}>Şifre Değiştir</h2>
                <form onSubmit={handlePasswordSubmit}>
                    <div className="form-group">
                        <label className="form-label">Mevcut Şifre</label>
                        <input
                            type="password"
                            className="form-input"
                            value={passwordData.currentPassword}
                            onChange={(e) => setPasswordData({ ...passwordData, currentPassword: e.target.value })}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">Yeni Şifre</label>
                        <input
                            type="password"
                            className="form-input"
                            value={passwordData.newPassword}
                            onChange={(e) => setPasswordData({ ...passwordData, newPassword: e.target.value })}
                            minLength={6}
                            required
                        />
                    </div>
                    <button type="submit" className="btn-save" disabled={passwordSaving} style={{ backgroundColor: '#4b5563' }}>
                        {passwordSaving ? 'Güncelleniyor...' : 'Şifreyi Güncelle'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Profile;
