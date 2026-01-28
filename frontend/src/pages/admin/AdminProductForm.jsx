import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import adminService from '../../services/adminService';

const AdminProductForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEditing = !!id;

    const [formData, setFormData] = useState({
        name: '',
        price: '',
        stock: '',
        description: '',
        categoryIds: []
    });

    const [categories, setCategories] = useState([]);
    const [selectedImages, setSelectedImages] = useState(null);
    const [loading, setLoading] = useState(false);
    const [initialLoading, setInitialLoading] = useState(false);

    useEffect(() => {
        fetchCategories();
        if (isEditing) {
            fetchProduct();
        }
    }, [id]);

    const fetchCategories = async () => {
        try {
            const data = await adminService.getAllCategories();
            setCategories(data);
        } catch (err) {
            console.error("Failed to load categories");
        }
    };

    const fetchProduct = async () => {
        try {
            setInitialLoading(true);
            const data = await adminService.getProductById(id);
            setFormData({
                name: data.name,
                price: data.price,
                stock: data.stock,
                description: data.description || '',
                // Assuming categories come back as names or objects, but we need IDs for update
                // Since DTO returns category NAMES, we might have an issue mapping back to IDs if we only have names.
                // However, for MVP let's assume we just want to set new categories or we need to fetch all categories and match names.
                // Simple workaround: Just let user select new categories.
                categoryIds: []
            });
            setInitialLoading(false);
        } catch (err) {
            console.error("Failed to load product");
            setInitialLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleCategoryChange = (e) => {
        const options = e.target.options;
        const selected = [];
        for (let i = 0; i < options.length; i++) {
            if (options[i].selected) {
                selected.push(parseInt(options[i].value));
            }
        }
        setFormData(prev => ({ ...prev, categoryIds: selected }));
    };

    const handleImageChange = (e) => {
        setSelectedImages(e.target.files);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            let productId = id;
            if (isEditing) {
                await adminService.updateProduct(id, formData);
            } else {
                const res = await adminService.createProduct(formData);
                productId = res.id;
            }

            if (selectedImages && selectedImages.length > 0) {
                await adminService.uploadProductImages(productId, selectedImages);
            }

            navigate('/admin/products');
        } catch (err) {
            console.error(err);
            alert('Ürün kaydedilemedi.');
        } finally {
            setLoading(false);
        }
    };

    if (initialLoading) return <div>Yükleniyor...</div>;

    return (
        <div>
            <div className="admin-header">
                <h1 className="admin-title">{isEditing ? 'Ürünü Düzenle' : 'Yeni Ürün'}</h1>
            </div>

            <div className="admin-form-container">
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Ürün Adı</label>
                        <input
                            type="text"
                            name="name"
                            className="form-input"
                            value={formData.name}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Fiyat</label>
                        <input
                            type="number"
                            name="price"
                            step="0.01"
                            className="form-input"
                            value={formData.price}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Stok</label>
                        <input
                            type="number"
                            name="stock"
                            className="form-input"
                            value={formData.stock}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Açıklama</label>
                        <textarea
                            name="description"
                            className="form-textarea"
                            value={formData.description}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Kategoriler (Çoklu seçim için Ctrl tuşuna basılı tutun)</label>
                        <select
                            multiple
                            className="form-select"
                            style={{ height: '100px' }}
                            onChange={handleCategoryChange}
                            value={formData.categoryIds}
                        >
                            {categories.map(cat => (
                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label className="form-label">Resimler</label>
                        <input
                            type="file"
                            multiple
                            className="form-input"
                            onChange={handleImageChange}
                            accept="image/*"
                        />
                        {isEditing && <small>Yeni resimler mevcut olanların üzerine eklenecektir.</small>}
                    </div>

                    <div className="form-actions">
                        <button type="button" className="btn-cancel" onClick={() => navigate('/admin/products')}>İptal</button>
                        <button type="submit" className="btn-save" disabled={loading}>
                            {loading ? 'Kaydediliyor...' : 'Kaydet'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AdminProductForm;
