import React, { useState, useEffect } from 'react';
import { useCart } from '../context/CartContext';
import addressService from '../services/addressService';
import checkoutService from '../services/checkoutService';
import { useNavigate } from 'react-router-dom';
import '../styles/checkout.css';

const Checkout = () => {
    const { cart, fetchCart, loading: cartLoading } = useCart();
    const [addresses, setAddresses] = useState([]);
    const [selectedAddressId, setSelectedAddressId] = useState(null);
    const [paymentMethod, setPaymentMethod] = useState('CREDIT_CARD'); // Default
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const init = async () => {
            await fetchCart();
            try {
                const addrs = await addressService.getAddresses();
                setAddresses(addrs);
                // Select default address if exists, or the first one
                const defaultAddr = addrs.find(a => a.defaultAddress);
                if (defaultAddr) {
                    setSelectedAddressId(defaultAddr.id);
                } else if (addrs.length > 0) {
                    setSelectedAddressId(addrs[0].id);
                }
            } catch (err) {
                console.error("Error fetching addresses", err);
            } finally {
                setLoading(false);
            }
        };
        init();
    }, []);

    const handleCheckout = async () => {
        if (!selectedAddressId) {
            alert('Lütfen bir teslimat adresi seçin.');
            return;
        }

        setSubmitting(true);
        try {
            await checkoutService.checkout({
                addressId: selectedAddressId,
                paymentMethod: paymentMethod
            });
            alert('Sipariş başarıyla alındı!');
            fetchCart(); // Clear cart in context (if backend clears it)
            navigate('/'); // Redirect to home or orders page
        } catch (err) {
            console.error(err);
            alert('Sipariş oluşturulamadı: ' + (err.response?.data?.message || err.message));
        } finally {
            setSubmitting(false);
        }
    };

    if (loading || cartLoading) return <div className="loading">Yükleniyor...</div>;

    if (!cart || !cart.items || cart.items.length === 0) {
        return (
            <div className="checkout-container">
                <div className="no-items-msg">
                    <h2>Sepetiniz boş</h2>
                    <button onClick={() => navigate('/')}>Alışverişe Dön</button>
                </div>
            </div>
        );
    }

    return (
        <div className="checkout-container">
            <h1 className="checkout-title">Siparişi Tamamla</h1>

            <div className="checkout-grid">
                <div className="checkout-left">
                    <section className="checkout-section">
                        <h2 className="section-title">1. Teslimat Adresi</h2>
                        {addresses.length === 0 ? (
                            <div>
                                <p>Adres bulunamadı.</p>
                                <button onClick={() => navigate('/addresses')}>Yeni Adres Ekle</button>
                            </div>
                        ) : (
                            <div className="address-selection-grid">
                                {addresses.map(addr => (
                                    <div
                                        key={addr.id}
                                        className={`address-option ${selectedAddressId === addr.id ? 'selected' : ''}`}
                                        onClick={() => setSelectedAddressId(addr.id)}
                                    >
                                        <input
                                            type="radio"
                                            name="address"
                                            checked={selectedAddressId === addr.id}
                                            onChange={() => setSelectedAddressId(addr.id)}
                                        />
                                        <div className="address-option-title"><strong>{addr.title}</strong></div>
                                        <div className="address-option-details">
                                            {addr.addressLine}<br />
                                            {addr.city}, {addr.state} {addr.postalCode}<br />
                                            {addr.country}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                        <div style={{ marginTop: '15px', textAlign: 'right' }}>
                            <button onClick={() => navigate('/addresses')} style={{ color: 'var(--primary-color)', background: 'none', border: 'none', cursor: 'pointer' }}>Adresleri Yönet</button>
                        </div>
                    </section>

                    <section className="checkout-section">
                        <h2 className="section-title">2. Ödeme Yöntemi</h2>
                        <div className="payment-methods">
                            <label className={`payment-method-option ${paymentMethod === 'CREDIT_CARD' ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="paymentParams"
                                    value="CREDIT_CARD"
                                    checked={paymentMethod === 'CREDIT_CARD'}
                                    onChange={(e) => setPaymentMethod(e.target.value)}
                                />
                                Kredi Kartı
                            </label>
                            <label className={`payment-method-option ${paymentMethod === 'PAYPAL' ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="paymentParams"
                                    value="PAYPAL"
                                    checked={paymentMethod === 'PAYPAL'}
                                    onChange={(e) => setPaymentMethod(e.target.value)}
                                />
                                PayPal
                            </label>
                            <label className={`payment-method-option ${paymentMethod === 'CASH_ON_DELIVERY' ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="paymentParams"
                                    value="CASH_ON_DELIVERY"
                                    checked={paymentMethod === 'CASH_ON_DELIVERY'}
                                    onChange={(e) => setPaymentMethod(e.target.value)}
                                />
                                Kapıda Ödeme
                            </label>
                        </div>
                    </section>
                </div>

                <div className="checkout-right">
                    <div className="order-summary">
                        <h2 className="section-title">Sipariş Özeti</h2>
                        {cart.items.map(item => (
                            <div key={item.id} className="summary-item">
                                <span>{item.quantity} x {item.productName}</span>
                                <span>${item.subTotal.toFixed(2)}</span>
                            </div>
                        ))}

                        <div className="summary-item total">
                            <span>Toplam</span>
                            <span>${cart.total.toFixed(2)}</span>
                        </div>

                        <button
                            className="place-order-btn"
                            onClick={handleCheckout}
                            disabled={submitting || !selectedAddressId}
                        >
                            {submitting ? 'İşleniyor...' : 'Siparişi Onayla'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Checkout;
