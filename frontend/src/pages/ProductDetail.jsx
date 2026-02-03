import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import productService from '../services/productService';
import api, { BASE_URL } from '../services/api';
import { useCart } from '../context/CartContext';
import '../styles/product-detail.css';
import { FaShoppingCart, FaArrowLeft } from 'react-icons/fa';

const ProductDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [quantity, setQuantity] = useState(1);

    const { addToCart } = useCart();

    const [activeCoupons, setActiveCoupons] = useState([]);

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const data = await productService.getProductById(id);
                setProduct(data);
                setLoading(false);
            } catch (err) {
                console.error(err);
                setError('Product not found or error loading details.');
                setLoading(false);
            }
        };

        const fetchCoupons = async () => {
            try {
                const res = await api.get('/coupons/active');
                setActiveCoupons(res.data);
            } catch (err) {
                console.error("Failed to load coupons", err);
            }
        };

        if (id) {
            fetchProduct();
            fetchCoupons();
        }
    }, [id]);

    const getBestCoupon = () => {
        if (!product || !activeCoupons.length) return null;

        let bestCoupon = null;
        let maxDiscountVal = -1;

        activeCoupons.forEach(coupon => {
            let isApplicable = false;
            const hasCategoryRestriction = coupon.applicableCategoryIds && coupon.applicableCategoryIds.length > 0;
            const hasProductRestriction = coupon.applicableProductIds && coupon.applicableProductIds.length > 0;

            if (!hasCategoryRestriction && !hasProductRestriction) {
                isApplicable = true;
            } else {
                if (hasCategoryRestriction && product.categoryIds) {
                    if (product.categoryIds.some(catId => coupon.applicableCategoryIds.includes(catId))) {
                        isApplicable = true;
                    }
                }
                if (hasProductRestriction && coupon.applicableProductIds.includes(product.id)) {
                    isApplicable = true;
                }
            }

            if (isApplicable) {
                let currentVal = coupon.discountValue;
                if (coupon.discountType === 'PERCENTAGE') {
                    currentVal = (product.price * coupon.discountValue) / 100;
                }
                if (currentVal > maxDiscountVal) {
                    maxDiscountVal = currentVal;
                    bestCoupon = coupon;
                }
            }
        });
        return bestCoupon;
    };

    const bestCoupon = getBestCoupon();

    const handleQuantityChange = (val) => {
        if (val < 1) return;
        if (product && val > product.stock) return;
        setQuantity(val);
    };

    const handleAddToCart = () => {
        // ... (existing code)
        if (!product) return;
        addToCart(product.id, quantity);
    };

    if (loading) return <div className="loading">Detaylar yükleniyor...</div>;

    if (error || !product) {
        // ... (existing error handling)
        return (
            <div className="product-detail-container">
                <div style={{ textAlign: 'center', marginTop: '50px' }}>
                    <h2>Ürün bulunamadı</h2>
                    <button onClick={() => navigate('/')} className="qty-btn" style={{ marginTop: '20px', width: 'auto', padding: '10px 20px', background: 'var(--primary-color)', color: 'white' }}>Ana Sayfaya Dön</button>
                </div>
            </div>
        );
    }

    const isOutOfStock = product.stock <= 0;

    return (
        <div className="product-detail-container">
            <button onClick={() => navigate(-1)} style={{ background: 'none', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '5px', marginBottom: '20px', fontSize: '1rem', color: 'var(--text-secondary)' }}>
                <FaArrowLeft /> Geri
            </button>

            <div className="product-detail-grid">
                <div className="product-image-section">
                    <img
                        src={(product.images && product.images.length > 0)
                            ? (product.images[0].imageUrl.startsWith('http') ? product.images[0].imageUrl : `${BASE_URL}${product.images[0].imageUrl}`)
                            : 'https://placehold.co/600x400?text=Resim+Yok'}
                        alt={product.name}
                        className="product-detail-image"
                        onError={(e) => {
                            e.target.onerror = null;
                            e.target.src = 'https://placehold.co/600x400?text=Resim+Yok';
                        }}
                    />
                </div>

                <div className="product-info-section">
                    <h1 className="product-title">{product.name}</h1>
                    <div className="product-price">${product.price.toFixed(2)}</div>

                    {bestCoupon && (
                        <div style={{
                            marginTop: '10px',
                            padding: '10px',
                            backgroundColor: '#e3f2fd',
                            border: '1px solid #90caf9',
                            borderRadius: '4px',
                            color: '#0d47a1',
                            display: 'flex',
                            flexDirection: 'column',
                            gap: '5px'
                        }}>
                            <span style={{ fontWeight: 'bold' }}>🎉 Fırsat!</span>
                            <span>Bu üründe geçerli kupon kodu: <strong>{bestCoupon.code}</strong></span>
                            <span style={{ fontSize: '0.9em' }}>
                                ({bestCoupon.discountType === 'PERCENTAGE' ? `%${bestCoupon.discountValue} İndirim` : `${bestCoupon.discountValue} TL İndirim`})
                            </span>
                        </div>
                    )}

                    <div className={isOutOfStock ? "stock-status out-of-stock" : "stock-status in-stock"}>
                        {isOutOfStock ? "Stokta Yok" : "Stokta Var"}
                    </div>

                    <div className="product-description-container">
                        <div className="product-description-title">Açıklama</div>
                        <p className="product-description">
                            {product.description || "Bu ürün için açıklama bulunmuyor."}
                        </p>
                    </div>

                    <div className="add-to-cart-section">
                        <div className="quantity-selector">
                            <button
                                className="qty-btn"
                                onClick={() => handleQuantityChange(quantity - 1)}
                                disabled={isOutOfStock || quantity <= 1}
                            >-</button>
                            <div className="qty-display">{quantity}</div>
                            <button
                                className="qty-btn"
                                onClick={() => handleQuantityChange(quantity + 1)}
                                disabled={isOutOfStock || quantity >= product.stock}
                            >+</button>
                        </div>

                        <button
                            className="add-to-cart-btn-large"
                            onClick={handleAddToCart}
                            disabled={isOutOfStock}
                        >
                            <FaShoppingCart />
                            {isOutOfStock ? 'Stokta Yok' : 'Sepete Ekle'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProductDetail;
