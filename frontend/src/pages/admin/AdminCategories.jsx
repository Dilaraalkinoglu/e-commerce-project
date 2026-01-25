import React, { useState, useEffect } from 'react';
import adminService from '../../services/adminService';

const AdminCategories = () => {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isEditing, setIsEditing] = useState(false);

    // Form state
    const [formData, setFormData] = useState({ id: null, name: '', parentId: '' });
    const [showForm, setShowForm] = useState(false);

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        try {
            setLoading(true);
            const data = await adminService.getAllCategories();
            setCategories(data);
            setLoading(false);
        } catch (err) {
            console.error(err);
            setError('Failed to load categories');
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const payload = {
                name: formData.name,
                parentId: formData.parentId ? parseInt(formData.parentId) : null
            };

            if (isEditing) {
                await adminService.updateCategory(formData.id, payload);
            } else {
                await adminService.createCategory(payload);
            }
            fetchCategories();
            resetForm();
        } catch (err) {
            console.error(err);
            alert('Failed to save category');
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this category?')) {
            try {
                await adminService.deleteCategory(id);
                fetchCategories();
            } catch (err) {
                console.error(err);
                alert('Failed to delete category. It might be in use.');
            }
        }
    };

    const handleEdit = (category) => {
        setFormData({
            id: category.id,
            name: category.name,
            parentId: category.parentId || ''
        });
        setIsEditing(true);
        setShowForm(true);
    };

    const resetForm = () => {
        setFormData({ id: null, name: '', parentId: '' });
        setIsEditing(false);
        setShowForm(false);
    };

    if (loading && categories.length === 0) return <div>Loading...</div>;

    return (
        <div>
            <div className="admin-header">
                <h1 className="admin-title">Categories</h1>
                {!showForm && (
                    <button className="btn-admin-action" onClick={() => setShowForm(true)}>
                        + Add Category
                    </button>
                )}
            </div>

            {error && <div className="error">{error}</div>}

            {showForm && (
                <div className="admin-form-container" style={{ marginBottom: '30px' }}>
                    <h2>{isEditing ? 'Edit Category' : 'New Category'}</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label className="form-label">Category Name</label>
                            <input
                                type="text"
                                className="form-input"
                                value={formData.name}
                                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Parent Category (Optional)</label>
                            <select
                                className="form-select"
                                value={formData.parentId}
                                onChange={(e) => setFormData({ ...formData, parentId: e.target.value })}
                            >
                                <option value="">-- None --</option>
                                {categories
                                    .filter(cat => cat.id !== formData.id) // Don't allow selecting self as parent
                                    .map(cat => (
                                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                                    ))
                                }
                            </select>
                        </div>
                        <div className="form-actions">
                            <button type="button" className="btn-cancel" onClick={resetForm}>Cancel</button>
                            <button type="submit" className="btn-save">{isEditing ? 'Update' : 'Create'}</button>
                        </div>
                    </form>
                </div>
            )}

            <div className="admin-table-container">
                <table className="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Parent Category</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {categories.map(cat => (
                            <tr key={cat.id}>
                                <td>{cat.id}</td>
                                <td>{cat.name}</td>
                                <td>{cat.parentName || '-'}</td>
                                <td>
                                    <div className="action-buttons">
                                        <button className="btn-edit" onClick={() => handleEdit(cat)}>Edit</button>
                                        <button className="btn-delete" onClick={() => handleDelete(cat.id)}>Delete</button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        {categories.length === 0 && (
                            <tr>
                                <td colSpan="4" style={{ textAlign: 'center' }}>No categories found.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AdminCategories;
