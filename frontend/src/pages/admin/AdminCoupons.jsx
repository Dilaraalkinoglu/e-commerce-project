import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import '../../styles/admin.css';

const AdminCoupons = () => {
    const { user } = useAuth();
    const [coupons, setCoupons] = useState([]);
    const [categories, setCategories] = useState([]);
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const [formData, setFormData] = useState({
        code: '',
        discountType: 'PERCENTAGE',
        discountValue: '',
        usageLimit: '',
        validFrom: '',
        validTo: '',
        applicableCategoryIds: [],
        applicableProductIds: []
    });

    useEffect(() => {
        fetchCoupons();
        fetchCategories();
        fetchProducts();
    }, []);

    const fetchCoupons = async () => {
        try {
            const response = await api.get('/admin/coupons');
            setCoupons(response.data);
        } catch (err) {
            console.error('Kuponlar yüklenirken hata', err);
        } finally {
            setLoading(false);
        }
    };

    const fetchCategories = async () => {
        try {
            const response = await api.get('/categories');
            setCategories(response.data);
        } catch (err) {
            console.error('Kategoriler yüklenemedi', err);
        }
    };

    const fetchProducts = async () => {
        try {
            const response = await api.get('/products');
            // Pagination varsa response.data.content olabilir
            setProducts(Array.isArray(response.data) ? response.data : (response.data.content || []));
        } catch (err) {
            console.error('Ürünler yüklenemedi', err);
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleMultiSelectChange = (e) => {
        const { name, options } = e.target;
        const selectedValues = [];
        for (let i = 0; i < options.length; i++) {
            if (options[i].selected) {
                selectedValues.push(Number(options[i].value));
            }
        }
        setFormData({ ...formData, [name]: selectedValues });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            // Tarihleri Instant formatına çevir (ISO string)
            const payload = {
                ...formData,
                validFrom: new Date(formData.validFrom).toISOString(),
                validTo: new Date(formData.validTo).toISOString()
            };

            await api.post('/admin/coupons', payload);
            setSuccess('Kupon başarıyla oluşturuldu');
            setFormData({
                code: '',
                discountType: 'PERCENTAGE',
                discountValue: '',
                usageLimit: '',
                validFrom: '',
                validTo: '',
                applicableCategoryIds: [],
                applicableProductIds: []
            });
            fetchCoupons();
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || 'Kupon oluşturulamadı');
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Bu kuponu silmek istediğinize emin misiniz?')) {
            try {
                await api.delete(`/admin/coupons/${id}`);
                setSuccess('Kupon silindi');
                fetchCoupons();
            } catch (err) {
                setError('Silme işlemi başarısız');
            }
        }
    };

    if (loading) return <div>Yükleniyor...</div>;

    return (
        <div className="admin-page">
            <h2 className="admin-title">Kupon Yönetimi</h2>

            {error && <div className="error-message">{error}</div>}
            {success && <div className="success-message">{success}</div>}

            <div className="coupon-form-container" style={{ marginBottom: '30px', padding: '20px', background: '#f8f9fa', borderRadius: '8px' }}>
                <h3>Yeni Kupon Ekle</h3>
                <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>

                    <div>
                        <label>Kupon Kodu</label>
                        <input type="text" name="code" value={formData.code} onChange={handleChange} required className="form-control" />
                    </div>

                    <div>
                        <label>İndirim Tipi</label>
                        <select name="discountType" value={formData.discountType} onChange={handleChange} className="form-control">
                            <option value="PERCENTAGE">Yüzde (%)</option>
                            <option value="FIXED_AMOUNT">Sabit Tutar</option>
                        </select>
                    </div>

                    <div>
                        <label>İndirim Değeri</label>
                        <input type="number" name="discountValue" value={formData.discountValue} onChange={handleChange} required min="0" className="form-control" />
                    </div>

                    <div>
                        <label>Kullanım Limiti</label>
                        <input type="number" name="usageLimit" value={formData.usageLimit} onChange={handleChange} required min="1" className="form-control" />
                    </div>

                    <div>
                        <label>Başlangıç Tarihi</label>
                        <input type="datetime-local" name="validFrom" value={formData.validFrom} onChange={handleChange} required className="form-control" />
                    </div>

                    <div>
                        <label>Bitiş Tarihi</label>
                        <input type="datetime-local" name="validTo" value={formData.validTo} onChange={handleChange} required className="form-control" />
                    </div>

                    <div style={{ gridColumn: '1 / -1' }}>
                        <label>Geçerli Kategoriler (Çoklu seçim için CTRL'ye basılı tutun)</label>
                        <select
                            multiple
                            name="applicableCategoryIds"
                            className="form-control"
                            onChange={handleMultiSelectChange}
                            value={formData.applicableCategoryIds}
                            style={{ height: '100px' }}
                        >
                            {categories.map(cat => (
                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                            ))}
                        </select>
                        <small className="form-text text-muted">Seçim yapmazsanız tüm kategorilerde geçerli olur.</small>
                    </div>

                    <div style={{ gridColumn: '1 / -1' }}>
                        <label>Geçerli Ürünler (Çoklu seçim için CTRL'ye basılı tutun)</label>
                        <select
                            multiple
                            name="applicableProductIds"
                            className="form-control"
                            onChange={handleMultiSelectChange}
                            value={formData.applicableProductIds}
                            style={{ height: '150px' }}
                        >
                            {products.map(prod => (
                                <option key={prod.id} value={prod.id}>{prod.name} ({prod.price} TL)</option>
                            ))}
                        </select>
                        <small className="form-text text-muted">Seçim yapmazsanız tüm ürünlerde geçerli olur (Kategori kısıtlaması yoksa).</small>
                    </div>

                    <div style={{ gridColumn: '1 / -1' }}>
                        <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>Kupon Oluştur</button>
                    </div>
                </form>
            </div>

            <table className="admin-table">
                <thead>
                    <tr>
                        <th>Kod</th>
                        <th>Tip</th>
                        <th>Değer</th>
                        <th>Kapsam</th>
                        <th>Kullanım</th>
                        <th>Geçerlilik</th>
                        <th>İşlem</th>
                    </tr>
                </thead>
                <tbody>
                    {coupons.map(coupon => {
                        let scopeText = "Global";
                        if (coupon.applicableCategoryIds && coupon.applicableCategoryIds.length > 0) {
                            scopeText = `${coupon.applicableCategoryIds.length} Kategori`;
                        } else if (coupon.applicableProductIds && coupon.applicableProductIds.length > 0) {
                            scopeText = `${coupon.applicableProductIds.length} Ürün`;
                        }

                        return (
                            <tr key={coupon.id}>
                                <td><strong>{coupon.code}</strong></td>
                                <td>{coupon.discountType === 'PERCENTAGE' ? 'Yüzde' : 'Sabit'}</td>
                                <td>{coupon.discountValue} {coupon.discountType === 'PERCENTAGE' ? '%' : 'TL'}</td>
                                <td>{scopeText}</td>
                                <td>{coupon.usageCount} / {coupon.usageLimit}</td>
                                <td>
                                    {new Date(coupon.validFrom).toLocaleDateString()} - {new Date(coupon.validTo).toLocaleDateString()}
                                </td>
                                <td>
                                    <button onClick={() => handleDelete(coupon.id)} className="btn btn-danger btn-sm">Sil</button>
                                </td>
                            </tr>
                        );
                    })}
                    {coupons.length === 0 && (
                        <tr>
                            <td colSpan="7" style={{ textAlign: 'center' }}>Kupon bulunamadı.</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default AdminCoupons;
