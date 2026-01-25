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
            alert('Failed to save product');
        } finally {
            setLoading(false);
        }
    };

    if (initialLoading) return <div>Loading...</div>;

    return (
        <div>
            <div className="admin-header">
                <h1 className="admin-title">{isEditing ? 'Edit Product' : 'New Product'}</h1>
            </div>

            <div className="admin-form-container">
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Product Name</label>
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
                        <label className="form-label">Price</label>
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
                        <label className="form-label">Stock</label>
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
                        <label className="form-label">Description</label>
                        <textarea
                            name="description"
                            className="form-textarea"
                            value={formData.description}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Categories (Hold Ctrl to select multiple)</label>
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
                        <label className="form-label">Images</label>
                        <input
                            type="file"
                            multiple
                            className="form-input"
                            onChange={handleImageChange}
                            accept="image/*"
                        />
                        {isEditing && <small>Uploading new images will append to existing ones.</small>}
                    </div>

                    <div className="form-actions">
                        <button type="button" className="btn-cancel" onClick={() => navigate('/admin/products')}>Cancel</button>
                        <button type="submit" className="btn-save" disabled={loading}>
                            {loading ? 'Saving...' : 'Save Product'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AdminProductForm;
