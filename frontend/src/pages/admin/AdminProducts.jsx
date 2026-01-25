import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import adminService from '../../services/adminService';
import { BASE_URL } from '../../services/api';

const AdminProducts = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const navigate = useNavigate();

    useEffect(() => {
        fetchProducts();
    }, [page]);

    const fetchProducts = async () => {
        try {
            setLoading(true);
            // Default size 10
            const data = await adminService.getAllProducts(page, 10);
            setProducts(data.content);
            setTotalPages(data.totalPages);
            setLoading(false);
        } catch (err) {
            console.error(err);
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this product?')) {
            try {
                await adminService.deleteProduct(id);
                fetchProducts();
            } catch (err) {
                console.error(err);
                alert('Failed to delete product.');
            }
        }
    };

    if (loading && products.length === 0) return <div>Loading...</div>;

    return (
        <div>
            <div className="admin-header">
                <h1 className="admin-title">Products</h1>
                <button className="btn-admin-action" onClick={() => navigate('/admin/products/new')}>
                    + Add Product
                </button>
            </div>

            <div className="admin-table-container">
                <table className="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Image</th>
                            <th>Name</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.map(product => (
                            <tr key={product.id}>
                                <td>{product.id}</td>
                                <td>
                                    {product.images && product.images.length > 0 ? (
                                        <img src={`${BASE_URL}${product.images[0].imageUrl}`} alt={product.name} style={{ width: '40px', height: '40px', objectFit: 'cover', borderRadius: '4px' }} />
                                    ) : (
                                        <div style={{ width: '40px', height: '40px', background: '#eee', borderRadius: '4px' }}></div>
                                    )}
                                </td>
                                <td>{product.name}</td>
                                <td>${product.price.toFixed(2)}</td>
                                <td>{product.stock}</td>
                                <td>
                                    <div className="action-buttons">
                                        <button className="btn-edit" onClick={() => navigate(`/admin/products/edit/${product.id}`)}>Edit</button>
                                        <button className="btn-delete" onClick={() => handleDelete(product.id)}>Delete</button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <div style={{ marginTop: '20px', display: 'flex', gap: '10px', alignItems: 'center', justifyContent: 'center' }}>
                <button
                    disabled={page === 0}
                    onClick={() => setPage(p => p - 1)}
                    className="btn-cancel"
                >
                    Previous
                </button>
                <span>Page {page + 1} of {totalPages}</span>
                <button
                    disabled={page >= totalPages - 1}
                    onClick={() => setPage(p => p + 1)}
                    className="btn-cancel"
                >
                    Next
                </button>
            </div>
        </div>
    );
};

export default AdminProducts;
