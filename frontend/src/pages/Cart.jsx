
import React, { useEffect } from 'react';
import { useCart } from '../context/CartContext';
import { Link } from 'react-router-dom';
import '../styles/cart.css';
import { FaTrash } from 'react-icons/fa';

const Cart = () => {
    const { cart, removeFromCart, updateQuantity, fetchCart, loading } = useCart();

    useEffect(() => {
        fetchCart();
    }, []);

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
