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
        if (window.confirm('Bu ürünü silmek istediğinize emin misiniz?')) {
            try {
                await adminService.deleteProduct(id);
                fetchProducts();
            } catch (err) {
                console.error(err);
                alert('Ürün silinemedi.');
            }
        }
    };

    if (loading && products.length === 0) return <div>Yükleniyor...</div>;

    return (
        <div>
            <div className="admin-header">
                <h1 className="admin-title">Ürünler</h1>
                <button className="btn-admin-action" onClick={() => navigate('/admin/products/new')}>
                    + Ürün Ekle
                </button>
            </div>

            <div className="admin-table-container">
                <table className="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Resim</th>
                            <th>Ad</th>
                            <th>Fiyat</th>
                            <th>Stok</th>
                            <th>İşlemler</th>
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
                                        <button className="btn-edit" onClick={() => navigate(`/admin/products/edit/${product.id}`)}>Düzenle</button>
                                        <button className="btn-delete" onClick={() => handleDelete(product.id)}>Sil</button>
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
                    Önceki
                </button>
                <span>Sayfa {page + 1} / {totalPages}</span>
                <button
                    disabled={page >= totalPages - 1}
                    onClick={() => setPage(p => p + 1)}
                    className="btn-cancel"
                >
                    Sonraki
                </button>
            </div>
        </div>
    );
};

export default AdminProducts;
