
import React, { useEffect, useState } from 'react';
import { useCart } from '../context/CartContext';
import { Link } from 'react-router-dom';
import api from '../services/api';
import '../styles/cart.css';
import { FaTrash, FaTicketAlt } from 'react-icons/fa';

const Cart = () => {
    const { cart, removeFromCart, updateQuantity, fetchCart, loading } = useCart();
    const [activeCoupons, setActiveCoupons] = useState([]);

    useEffect(() => {
        fetchCart();
        fetchActiveCoupons();
    }, []);

    const fetchActiveCoupons = async () => {
        try {
            const res = await api.get('/coupons/active');
            setActiveCoupons(res.data);
        } catch (err) {
            console.error("Kuponlar yüklenemedi", err);
        }
    };

    if (loading) return <div className="loading">Sepet yükleniyor...</div>;

    if (!cart || !cart.items || cart.items.length === 0) {
        return (
            <div className="cart-empty">
                <h2>Sepetiniz Boş</h2>
                <p>Henüz sepetinize bir ürün eklemediniz.</p>
                <Link to="/" className="btn-primary">Alışverişe Başla</Link>
            </div>
        );
    }

    // Sepetteki ürünlere uygun kuponları bul
    const getApplicableCouponsForCart = () => {
        if (!cart.items || !activeCoupons.length) return [];

        const applicableSet = new Set();
        const coupons = [];

        cart.items.forEach(item => {
            activeCoupons.forEach(coupon => {
                let isApplicable = false;
                const hasCategoryRestriction = coupon.applicableCategoryIds && coupon.applicableCategoryIds.length > 0;
                const hasProductRestriction = coupon.applicableProductIds && coupon.applicableProductIds.length > 0;

                // Simple check: we don't have full product details (like categoryIds) in cart item usually, 
                // but let's assume we might or we just check product ID restriction if available.
                // NOTE: Cart item usually has limited info. If we need category check, we might miss it if cart item doesn't have categoryIds.
                // However, fetching full product details for every cart item might be overkill.
                // Let's assume for now we check product ID if restriction exists, or if no restriction it's global.
                // If category restriction exists, we might not be able to validate easily without category info.

                // For better UX, let's list GLOBAL coupons and PRODUCT specific coupons that match.

                if (!hasCategoryRestriction && !hasProductRestriction) {
                    isApplicable = true;
                } else if (hasProductRestriction && coupon.applicableProductIds.includes(item.productId)) {
                    isApplicable = true;
                }
                // (Skip category check in cart for simplicity unless we add categoryId to cart items)

                if (isApplicable && !applicableSet.has(coupon.id)) {
                    applicableSet.add(coupon.id);
                    coupons.push(coupon);
                }
            });
        });
        return coupons;
    };

    const applicableCoupons = getApplicableCouponsForCart();

    return (
        <div className="cart-container">
            <h2>Alışveriş Sepeti</h2>

            <div className="cart-content">
                <div className="cart-items">
                    {cart.items.map((item) => (
                        <div key={item.id} className="cart-item">
                            <div className="item-info">
                                <Link to={`/product/${item.productId}`} className="product-title-link">
                                    <h3>{item.productName}</h3>
                                </Link>
                                <p className="item-price">${item.unitPrice.toFixed(2)} x {item.quantity}</p>
                            </div>
                            <div className="item-total">
                                <div className="quantity-controls">
                                    <button
                                        className="btn-quantity"
                                        onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                                        disabled={loading}
                                    >-</button>
                                    <span className="quantity-display">{item.quantity}</span>
                                    <button
                                        className="btn-quantity"
                                        onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                                        disabled={loading}
                                    >+</button>
                                </div>
                                <p className="item-subtotal">${item.subTotal.toFixed(2)}</p>
                                <button
                                    className="btn-remove"
                                    onClick={() => removeFromCart(item.productId)}
                                >
                                    <FaTrash />
                                </button>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="cart-summary">
                    {applicableCoupons.length > 0 && (
                        <div className="available-coupons" style={{ marginBottom: '15px', padding: '10px', background: '#f0f4c3', borderRadius: '5px' }}>
                            <h4 style={{ margin: '0 0 5px 0', display: 'flex', alignItems: 'center', gap: '5px' }}>
                                <FaTicketAlt /> Kullanılabilir Kuponlar
                            </h4>
                            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                                {applicableCoupons.map(coupon => (
                                    <li key={coupon.id} style={{ fontSize: '0.9rem', marginBottom: '3px' }}>
                                        <strong>{coupon.code}</strong>:
                                        {coupon.discountType === 'PERCENTAGE' ? ` %${coupon.discountValue} indirim` : ` ${coupon.discountValue} TL indirim`}
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}

                    <h3>Sipariş Özeti</h3>
                    <div className="summary-row">
                        <span>Ara Toplam</span>
                        <span>${cart.total.toFixed(2)}</span>
                    </div>
                    <div className="summary-row total">
                        <span>Toplam</span>
                        <span>${cart.total.toFixed(2)}</span>
                    </div>
                    <Link to="/checkout" className="btn-checkout" style={{ display: 'block', textAlign: 'center' }}>Siparişi Tamamla</Link>
                </div>
            </div>
        </div>
    );
};

export default Cart;
