import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FaBars, FaChevronRight } from 'react-icons/fa';
import api, { BASE_URL } from '../services/api';
import productService from '../services/productService';
import categoryService from '../services/categoryService';
import { useCart } from '../context/CartContext';
import '../styles/home.css';

const Home = () => {
    const [products, setProducts] = useState([]);
    const [allCategories, setAllCategories] = useState([]); // Store all categories for hierarchical lookup
    const [rootCategories, setRootCategories] = useState([]);
    const [subCategories, setSubCategories] = useState([]);

    // Filter States
    const [keyword, setKeyword] = useState('');
    const [selectedCategory, setSelectedCategory] = useState(null); // Actually selected for filtering
    const [activeParentCategory, setActiveParentCategory] = useState(null); // For showing subcats
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [sort, setSort] = useState('createdAt');
    const [direction, setDirection] = useState('DESC');

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isCategoryMenuOpen, setIsCategoryMenuOpen] = useState(false);
    const [activeSubCategory, setActiveSubCategory] = useState(null);
    const { addToCart } = useCart();

    useEffect(() => {
        fetchInitialData();
        fetchProducts();
    }, []);

    // Re-fetch when filters change (debouncing could be added for keyword)
    useEffect(() => {
        const delaySearch = setTimeout(() => {
            fetchProducts();
        }, 500);
        return () => clearTimeout(delaySearch);
    }, [keyword, selectedCategory, minPrice, maxPrice, sort, direction]);

    const fetchInitialData = async () => {
        try {
            const catData = await categoryService.getAllCategories();
            setAllCategories(catData); // Store all categories
            // Derive roots
            const roots = catData.filter(c => !c.parentId);
            setRootCategories(roots);
        } catch (err) {
            console.error("Failed to load categories");
        }
    };

    const fetchProducts = async () => {
        try {
            setLoading(true);
            const params = {};
            if (keyword) params.name = keyword;
            if (selectedCategory) params.categoryId = selectedCategory;
            if (minPrice) params.minPrice = minPrice;
            if (maxPrice) params.maxPrice = maxPrice;
            params.sortBy = sort;
            params.direction = direction;
            params.size = 20;

            // If no filters are active, we might want to use getAllProducts or just search with empty params
            // The backend /search endpoints works with empty params too (check backend ProductSpecification)

            const data = await productService.searchProducts(params);
            setProducts(data.content); // Page contents
            setLoading(false);
        } catch (err) {
            console.error("Failed to fetch products", err);
            setError('Failed to load products.');
            setLoading(false);
        }
    };

    const handleParentCategoryClick = (catId) => {
        // Find children
        const children = allCategories.filter(c => c.parentId === catId);

        if (activeParentCategory === catId) {
            // If user clicks the same parent category again, clear its active state and subcategories
            setActiveParentCategory(null);
            setSubCategories([]);
            setSelectedCategory(null); // Clear filter
        } else {
            setActiveParentCategory(catId);
            setSubCategories(children);
            // If it has no children, filter by it immediately. 
            // If it HAS children, ask user to select child OR filter by parent? 
            // Usually valid to filter by parent logic depends on backend. 
            // Let's assume selecting parent filters by parent (backend should handle finding subcat products if implemented, or just exact match).
            // For now, let's filter by this category.
            setSelectedCategory(catId);
        }
        // Don't close main menu yet to allow sub-selection? Or maybe close it?
        // Usually mega menus might stay open or close. Let's keep it open for sub-interaction.
    };

    const handleSubCategoryClick = (catId) => {
        setSelectedCategory(catId);
        setIsCategoryMenuOpen(false); // Close menu on selection
    };

    const clearFilters = () => {
        setKeyword('');
        setSelectedCategory(null);
        setActiveParentCategory(null);
        setSubCategories([]);
        setMinPrice('');
        setMaxPrice('');
        setSort('createdAt');
        setDirection('DESC');
    };

    // if (loading && products.length === 0) return <div className="loading">Loading products...</div>; // Keep seeing sidebar?
    // Better to show loading indicator over grid overlay

    return (
        <div className="home-container">
            <div className="filter-bar-container">
                <div className="filter-bar-top">
                    {/* Search and Category Group */}
                    <div className="search-group">
                        <input
                            type="text"
                            placeholder="Ürün ara..."
                            className="filter-input search-input"
                            value={keyword}
                            onChange={(e) => setKeyword(e.target.value)}
                        />

                        {/* Custom Category Mega Menu */}
                        <div className="category-mega-menu">
                            <button
                                className="category-trigger-btn"
                                onClick={() => setIsCategoryMenuOpen(!isCategoryMenuOpen)}
                            >
                                <FaBars /> Kategoriler
                            </button>

                            {isCategoryMenuOpen && (
                                <div className="mega-menu-content" style={{ display: 'block' }}>
                                    <ul className="root-cat-list">
                                        <li
                                            className={!selectedCategory ? 'active' : ''}
                                            onClick={() => {
                                                setSelectedCategory(null);
                                                setSubCategories([]);
                                                setActiveParentCategory(null);
                                                setIsCategoryMenuOpen(false);
                                            }}
                                        >
                                            Tüm Kategoriler
                                        </li>
                                        {rootCategories.map(cat => (
                                            <li
                                                key={cat.id}
                                                className="root-cat-item"
                                                onMouseEnter={() => {
                                                    // Show subcategories on hover
                                                    const children = allCategories.filter(c => c.parentId === cat.id);
                                                    setSubCategories(children);
                                                    setActiveParentCategory(cat.id);
                                                }}
                                                onClick={() => {
                                                    handleParentCategoryClick(cat.id);
                                                }}
                                            >
                                                <div className="cat-label">
                                                    {cat.name}
                                                    {allCategories.some(c => c.parentId === cat.id) && <FaChevronRight size={12} />}
                                                </div>

                                                {/* Submenu Panel */}
                                                {activeParentCategory === cat.id && subCategories.length > 0 && (
                                                    <div className="sub-cat-panel">
                                                        <h4>{cat.name}</h4>
                                                        <div className="sub-cat-grid">
                                                            {subCategories.map(sub => {
                                                                const grandChildren = allCategories.filter(c => c.parentId === sub.id);
                                                                const hasGrandChildren = grandChildren.length > 0;
                                                                return (
                                                                    <div
                                                                        key={sub.id}
                                                                        className="sub-cat-item-container"
                                                                        onMouseEnter={() => setActiveSubCategory(sub.id)}
                                                                    >
                                                                        <span
                                                                            className="sub-cat-link"
                                                                            onClick={(e) => {
                                                                                e.stopPropagation();
                                                                                handleSubCategoryClick(sub.id);
                                                                            }}
                                                                        >
                                                                            {sub.name}
                                                                            {hasGrandChildren && <FaChevronRight size={10} />}
                                                                        </span>

                                                                        {/* Level 3 Panel */}
                                                                        {hasGrandChildren && activeSubCategory === sub.id && (
                                                                            <div className="grand-child-panel">
                                                                                {grandChildren.map(grand => (
                                                                                    <span
                                                                                        key={grand.id}
                                                                                        className="grand-child-link"
                                                                                        onClick={(e) => {
                                                                                            e.stopPropagation();
                                                                                            handleSubCategoryClick(grand.id);
                                                                                        }}
                                                                                    >
                                                                                        {grand.name}
                                                                                    </span>
                                                                                ))}
                                                                            </div>
                                                                        )}
                                                                    </div>
                                                                );
                                                            })}
                                                        </div>
                                                    </div>
                                                )}
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            )}
                        </div>

                        {/* Sub Category Dropdown - Appears only when a parent category with children is active */}
                        {subCategories.length > 0 && (
                            <div style={{ display: 'flex', gap: '10px' }}>
                                <select
                                    className="filter-select"
                                    style={{ minWidth: '150px' }}
                                    value={selectedCategory && subCategories.some(s => s.id === selectedCategory) ? selectedCategory : activeParentCategory}
                                    onChange={(e) => {
                                        const val = e.target.value;
                                        if (val === activeParentCategory.toString()) {
                                            handleParentCategoryClick(activeParentCategory);
                                        } else {
                                            handleSubCategoryClick(Number(val));
                                        }
                                    }}
                                >
                                    <option value={activeParentCategory}>Tümü</option>
                                    {subCategories.map(sub => (
                                        <option key={sub.id} value={sub.id}>
                                            {sub.name}
                                        </option>
                                    ))}
                                </select>

                                {/* Level 3 Dropdown (New) */}
                                {(() => {
                                    // Find if currently selected category has children (i.e. if we selected a Level 2 item)
                                    const selectedL2 = subCategories.find(s => s.id === selectedCategory);
                                    const l3Children = selectedL2 ? allCategories.filter(c => c.parentId === selectedL2.id) : [];

                                    if (l3Children.length > 0) {
                                        return (
                                            <select
                                                className="filter-select"
                                                style={{ minWidth: '150px' }}
                                                value={selectedCategory} // If a child is actually selected, we might need more complex logic, but for now filtering by parent includes children
                                                onChange={(e) => {
                                                    const val = Number(e.target.value);
                                                    if (val !== selectedCategory) {
                                                        handleSubCategoryClick(val);
                                                    }
                                                }}
                                            >
                                                <option value={selectedCategory}>Tümü ({selectedL2.name})</option>
                                                {l3Children.map(child => (
                                                    <option key={child.id} value={child.id}>{child.name}</option>
                                                ))}
                                            </select>
                                        );
                                    }
                                    return null;
                                })()}
                            </div>
                        )}
                    </div>

                    <div className="filter-group right-filters">
                        <div className="price-inputs">
                            <input
                                type="number"
                                placeholder="En Az"
                                className="filter-input small"
                                value={minPrice}
                                onChange={(e) => setMinPrice(e.target.value)}
                            />
                            <span style={{ color: '#aaa' }}>-</span>
                            <input
                                type="number"
                                placeholder="En Çok"
                                className="filter-input small"
                                value={maxPrice}
                                onChange={(e) => setMaxPrice(e.target.value)}
                            />
                        </div>

                        <select
                            className="filter-select sort-select"
                            value={`${sort}-${direction}`}
                            onChange={(e) => {
                                const [s, d] = e.target.value.split('-');
                                setSort(s);
                                setDirection(d);
                            }}
                        >
                            <option value="createdAt-DESC">En Yeniler</option>
                            <option value="price-ASC">Fiyat (Artan)</option>
                            <option value="price-DESC">Fiyat (Azalan)</option>
                        </select>

                        <button className="btn-clear" onClick={clearFilters}>Temizle</button>
                    </div>
                </div>
            </div>

            <div className="main-content">
                {loading ? (
                    <div className="loading">Yükleniyor...</div>
                ) : (
                    <section className="product-grid">
                        {products.length === 0 && <div className="no-results">Ürün bulunamadı.</div>}
                        {products.map((product) => (
                            <div key={product.id} className="product-card">
                                <Link to={`/product/${product.id}`} className="product-link">
                                    <div className="product-image-placeholder">
                                        {product.images && product.images.length > 0 ? (
                                            <img src={`${BASE_URL}${product.images[0].imageUrl}`} alt={product.name} />
                                        ) : (
                                            <div className="placeholder-img">Resim Yok</div>
                                        )}
                                    </div>
                                </Link>
                                <div className="product-info">
                                    <Link to={`/product/${product.id}`} className="product-title-link">
                                        <h3>{product.name}</h3>
                                    </Link>
                                    <p className="price">${product.price.toFixed(2)}</p>
                                    <button
                                        className="btn-add-cart"
                                        onClick={() => addToCart(product.id)}
                                    >
                                        Sepete Ekle
                                    </button>
                                </div>
                            </div>
                        ))}
                    </section>
                )}
            </div>
        </div>
    );
};

export default Home;
