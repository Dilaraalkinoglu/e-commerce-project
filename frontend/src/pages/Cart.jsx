
import React, { useEffect } from 'react';
import { useCart } from '../context/CartContext';
import { Link } from 'react-router-dom';
import '../styles/cart.css';
import { FaTrash } from 'react-icons/fa';

const Cart = () => {
    const { cart, removeFromCart, fetchCart, loading } = useCart();

    useEffect(() => {
        fetchCart();
    }, []);

    if (loading) return <div className="loading">Loading cart...</div>;

    if (!cart || !cart.items || cart.items.length === 0) {
        return (
            <div className="cart-empty">
                <h2>Your Cart is Empty</h2>
                <p>Looks like you haven't added anything to your cart yet.</p>
                <Link to="/" className="btn-primary">Start Shopping</Link>
            </div>
        );
    }

    return (
        <div className="cart-container">
            <h2>Shopping Cart</h2>

            <div className="cart-content">
                <div className="cart-items">
                    {cart.items.map((item) => (
                        <div key={item.id} className="cart-item">
                            <div className="item-info">
                                <h3>{item.productName}</h3>
                                <p className="item-price">${item.unitPrice.toFixed(2)} x {item.quantity}</p>
                            </div>
                            <div className="item-total">
                                <p>${item.subTotal.toFixed(2)}</p>
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
                    <h3>Order Summary</h3>
                    <div className="summary-row">
                        <span>Subtotal</span>
                        <span>${cart.total.toFixed(2)}</span>
                    </div>
                    <div className="summary-row total">
                        <span>Total</span>
                        <span>${cart.total.toFixed(2)}</span>
                    </div>
                    <button className="btn-checkout">Proceed to Checkout</button>
                </div>
            </div>
        </div>
    );
};

export default Cart;
