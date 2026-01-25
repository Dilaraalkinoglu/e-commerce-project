
import React, { createContext, useState, useContext, useEffect } from 'react';
import cartService from '../services/cartService';
import { useAuth } from './AuthContext';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(false);
    const { user } = useAuth();

    useEffect(() => {
        if (user) {
            fetchCart();
        } else {
            setCart(null);
        }
    }, [user]);

    const fetchCart = async () => {
        try {
            const data = await cartService.getCart();
            setCart(data);
        } catch (error) {
            console.error("Failed to fetch cart", error);
        }
    };

    const addToCart = async (productId, quantity = 1) => {
        try {
            setLoading(true);
            const updatedCart = await cartService.addItem(productId, quantity);
            setCart(updatedCart);
            alert("Product added to cart!"); // Simple feedback for now
        } catch (error) {
            console.error("Failed to add to cart", error);
            alert("Failed to add product to cart.");
        } finally {
            setLoading(false);
        }
    };

    const removeFromCart = async (productId) => {
        try {
            const updatedCart = await cartService.removeItem(productId);
            setCart(updatedCart);
        } catch (error) {
            console.error("Failed to remove item", error);
        }
    };

    const updateQuantity = async (productId, quantity) => {
        try {
            const updatedCart = await cartService.updateItem(productId, quantity);
            setCart(updatedCart);
        } catch (error) {
            console.error("Failed to update item quantity", error);
        }
    };

    return (
        <CartContext.Provider value={{ cart, addToCart, removeFromCart, updateQuantity, fetchCart, loading }}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => useContext(CartContext);
