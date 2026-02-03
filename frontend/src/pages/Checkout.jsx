import React, { useState, useEffect } from 'react';
import { useCart } from '../context/CartContext';
import addressService from '../services/addressService';
import checkoutService from '../services/checkoutService';
import api from '../services/api';
import { useNavigate } from 'react-router-dom';
import '../styles/checkout.css';

const Checkout = () => {
    const { cart, fetchCart, loading: cartLoading } = useCart();
    const [addresses, setAddresses] = useState([]);
    const [selectedAddressId, setSelectedAddressId] = useState(null);
    const [paymentMethod, setPaymentMethod] = useState('CREDIT_CARD'); // Default
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [couponCode, setCouponCode] = useState('');
    const [discount, setDiscount] = useState(0);
    const [appliedCoupon, setAppliedCoupon] = useState('');
    const navigate = useNavigate();

    const [activeCoupons, setActiveCoupons] = useState([]);

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
            }

            try {
                const res = await api.get('/coupons/active');
                setActiveCoupons(res.data);
            } catch (err) {
                console.error("Failed to load coupons", err);
            }

            setLoading(false);
        };
        init();
    }, []);

    const getApplicableCoupons = () => {
        if (!cart || !cart.items || !activeCoupons.length) return [];

        const applicableSet = new Set();
        const coupons = [];

        cart.items.forEach(item => {
            activeCoupons.forEach(coupon => {
                let isApplicable = false;
                const hasCategoryRestriction = coupon.applicableCategoryIds && coupon.applicableCategoryIds.length > 0;
                const hasProductRestriction = coupon.applicableProductIds && coupon.applicableProductIds.length > 0;

                if (!hasCategoryRestriction && !hasProductRestriction) {
                    isApplicable = true;
                } else if (hasProductRestriction && coupon.applicableProductIds.includes(item.productId)) {
                    isApplicable = true;
                }

                if (isApplicable && !applicableSet.has(coupon.id)) {
                    applicableSet.add(coupon.id);
                    coupons.push(coupon);
                }
            });
        });
        return coupons;
    };

    const applicableCoupons = getApplicableCoupons();

    const handleApplyCoupon = async () => {
        if (!couponCode.trim()) return;

        try {
            const response = await api.get('/checkout/validate-coupon', {
                params: {
                    code: couponCode
                }
            });

            setDiscount(response.data.discount);
            setAppliedCoupon(couponCode);
            alert(`Kupon uygulandı! İndirim: ${response.data.discount} TL`);
        } catch (err) {
            console.error(err);
            setDiscount(0);
            setAppliedCoupon('');
            alert('Kupon uygulanamadı: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleCheckout = async () => {
        if (!selectedAddressId) {
            alert('Lütfen bir teslimat adresi seçin.');
            return;
        }

        setSubmitting(true);
        try {
            await checkoutService.checkout({
                addressId: selectedAddressId,
                paymentMethod: paymentMethod,
                couponCode: appliedCoupon ? appliedCoupon : null
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

                        {discount > 0 && (
                            <div className="summary-item discount" style={{ color: 'green', fontWeight: 'bold' }}>
                                <span>İndirim</span>
                                <span>-${discount.toFixed(2)}</span>
                            </div>
                        )}

                        <div className="summary-item total">
                            <span>Toplam</span>
                            <span>${(Math.max(0, cart.total - discount)).toFixed(2)}</span>
                        </div>
                    </div>

                    <div className="coupon-container" style={{ marginTop: '20px', padding: '15px', background: '#f9f9f9', borderRadius: '8px', border: '1px solid #eee' }}>
                        <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold', fontSize: '0.9rem' }}>Kupon Kodu</label>
                        <div style={{ display: 'flex', gap: '8px' }}>
                            <input
                                type="text"
                                value={couponCode}
                                onChange={(e) => setCouponCode(e.target.value)}
                                placeholder="Varsa kupon kodunuzu girin"
                                style={{ flex: 1, padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
                            />
                            <button
                                onClick={handleApplyCoupon}
                                disabled={!couponCode || (appliedCoupon === couponCode && discount > 0)}
                                style={{
                                    padding: '8px 15px',
                                    backgroundColor: '#28a745',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '4px',
                                    cursor: 'pointer',
                                    fontSize: '0.9rem',
                                    whiteSpace: 'nowrap'
                                }}
                            >
                                Uygula
                            </button>
                        </div>

                        {/* Available Coupons List */}
                        {applicableCoupons.length > 0 && (
                            <div style={{ marginTop: '10px', padding: '10px', background: '#fff3cd', borderRadius: '5px', fontSize: '0.85rem', color: '#856404', border: '1px solid #ffeeba' }}>
                                <div style={{ fontWeight: 'bold', marginBottom: '5px' }}>Kullanılabilir Kuponlar:</div>
                                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}>
                                    {applicableCoupons.map(c => (
                                        <span
                                            key={c.id}
                                            onClick={() => setCouponCode(c.code)}
                                            style={{ cursor: 'pointer', background: '#fff', padding: '2px 6px', border: '1px solid #d3d3d3', borderRadius: '3px' }}
                                            title="Tıkla ve kullan"
                                        >
                                            <strong>{c.code}</strong> ({c.discountType === 'PERCENTAGE' ? `%${c.discountValue}` : `${c.discountValue}TL`})
                                        </span>
                                    ))}
                                </div>
                            </div>
                        )}

                        {appliedCoupon && discount > 0 && (
                            <small style={{ color: 'green', display: 'block', marginTop: '5px' }}>
                                ✔ {appliedCoupon} kuponu aktif.
                            </small>
                        )}
                        <small style={{ color: '#666', fontSize: '0.8rem', display: 'block', marginTop: '5px' }}>
                            Kupon indirimi uygulanmış toplam tutar üzerinden işlem yapılacaktır.
                        </small>
                    </div>

                    <button
                        className="place-order-btn"
                        onClick={handleCheckout}
                        disabled={submitting || !selectedAddressId}
                        style={{ marginTop: '20px' }}
                    >
                        {submitting ? 'İşleniyor...' : 'Siparişi Onayla'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Checkout;
