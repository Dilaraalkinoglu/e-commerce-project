import React, { useState, useEffect } from 'react';
import addressService from '../services/addressService';
import '../styles/addresses.css';

const Addresses = () => {
    const [addresses, setAddresses] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [formData, setFormData] = useState({
        title: '',
        addressLine: '',
        city: '',
        state: '',
        postalCode: '',
        country: '',
        defaultAddress: false
    });
    const [editingId, setEditingId] = useState(null); // Track which address is being edited
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchAddresses();
    }, []);

    const fetchAddresses = async () => {
        try {
            const data = await addressService.getAddresses();
            setAddresses(data);
            setLoading(false);
        } catch (err) {
            setError('Adresler yüklenirken bir hata oluştu.');
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingId) {
                await addressService.updateAddress(editingId, formData);
            } else {
                await addressService.addAddress(formData);
            }
            await fetchAddresses(); // Refresh list
            setShowModal(false);
            setEditingId(null);
            setFormData({
                title: '',
                addressLine: '',
                city: '',
                state: '',
                postalCode: '',
                country: '',
                defaultAddress: false
            });
        } catch (err) {
            alert('İşlem sırasında hata oluştu: ' + err.message);
        }
    };

    const handleEdit = (addr) => {
        setFormData({
            title: addr.title,
            addressLine: addr.addressLine,
            city: addr.city,
            state: addr.state,
            postalCode: addr.postalCode,
            country: addr.country,
            defaultAddress: addr.defaultAddress || false
        });
        setEditingId(addr.id);
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Bu adresi silmek istedğinize emin misiniz?')) {
            try {
                await addressService.deleteAddress(id);
                setAddresses(addresses.filter(addr => addr.id !== id));
            } catch (err) {
                alert('Silme işlemi başarısız.');
            }
        }
    };

    if (loading) return <div className="loading">Yükleniyor...</div>;

    return (
        <div className="addresses-container">
            <div className="addresses-header">
                <h1>Adreslerim</h1>
                <button className="add-address-btn" onClick={() => setShowModal(true)}>
                    + Yeni Adres Ekle
                </button>
            </div>

            {error && <div className="error-message">{error}</div>}

            <div className="addresses-grid">
                {addresses.map(addr => (
                    <div key={addr.id} className="address-card">
                        <div className="address-title">{addr.title}</div>
                        <div className="address-details">
                            <p>{addr.addressLine}</p>
                            <p>{addr.city}, {addr.state} {addr.postalCode}</p>
                            <p>{addr.country}</p>
                            {/* {addr.defaultAddress && <span className="default-badge">Varsayılan</span>} */}
                        </div>
                        <div className="address-actions">
                            <button className="edit-btn" onClick={() => handleEdit(addr)}>Düzenle</button>
                            <button className="delete-btn" onClick={() => handleDelete(addr.id)}>Sil</button>
                        </div>
                    </div>
                ))}
            </div>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h2>{editingId ? 'Adresi Düzenle' : 'Yeni Adres Ekle'}</h2>
                            <button className="close-btn" onClick={() => { setShowModal(false); setEditingId(null); setFormData({ title: '', addressLine: '', city: '', state: '', postalCode: '', country: '', defaultAddress: false }); }}>&times;</button>
                        </div>
                        <form onSubmit={handleSubmit} className="address-form">
                            <div className="form-group">
                                <label>Adres Başlığı</label>
                                <input
                                    type="text"
                                    name="title"
                                    value={formData.title}
                                    onChange={handleInputChange}
                                    placeholder="Ev, İş vb."
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>Adres Satırı</label>
                                <input
                                    type="text"
                                    name="addressLine"
                                    value={formData.addressLine}
                                    onChange={handleInputChange}
                                    placeholder="Cadde, sokak, no..."
                                    required
                                />
                            </div>
                            <div className="form-group" style={{ display: 'flex', gap: '10px' }}>
                                <div style={{ flex: 1 }}>
                                    <label>Şehir</label>
                                    <input
                                        type="text"
                                        name="city"
                                        value={formData.city}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <div style={{ flex: 1 }}>
                                    <label>Eyalet/İlçe</label>
                                    <input
                                        type="text"
                                        name="state"
                                        value={formData.state}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                            </div>
                            <div className="form-group" style={{ display: 'flex', gap: '10px' }}>
                                <div style={{ flex: 1 }}>
                                    <label>Posta Kodu</label>
                                    <input
                                        type="text"
                                        name="postalCode"
                                        value={formData.postalCode}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <div style={{ flex: 1 }}>
                                    <label>Ülke</label>
                                    <input
                                        type="text"
                                        name="country"
                                        value={formData.country}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                            </div>
                            {/* <div className="form-group">
                                <label>
                                    <input
                                        type="checkbox"
                                        name="defaultAddress"
                                        checked={formData.defaultAddress}
                                        onChange={handleInputChange}
                                        style={{width: 'auto', marginRight: '5px'}}
                                    />
                                    Varsayılan Adres Yap
                                </label>
                            </div> */}
                            <div className="form-actions">
                                <button type="button" className="cancel-btn" onClick={() => { setShowModal(false); setEditingId(null); setFormData({ title: '', addressLine: '', city: '', state: '', postalCode: '', country: '', defaultAddress: false }); }}>İptal</button>
                                <button type="submit" className="save-btn">Kaydet</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Addresses;
