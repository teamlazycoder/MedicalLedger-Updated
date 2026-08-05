import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function PatientRecords() {
  const { user } = useAuth();
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [showUpload, setShowUpload] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [patientId, setPatientId] = useState(null);
  const [uploadForm, setUploadForm] = useState({
    recordType: 'LAB_RESULT', description: '', diagnosis: '', treatment: '', file: null
  });

  useEffect(() => {
    fetchPatientId();
  }, []);

  // First, get the correct Patient ID
  const fetchPatientId = async () => {
    setLoading(true);
    try {
      // Try to get patient details using user.id (which is User ID, not Patient ID)
      const response = await api.get(`/patients/${user?.id}`);
      if (response.data?.success && response.data?.data) {
        // The response contains the patient data with the correct Patient ID
        setPatientId(response.data.data.id);
        fetchRecords(response.data.data.id);
      }
    } catch (error) {
      console.error('Error fetching patient profile:', error);
      // Fallback: try using user.id directly
      setPatientId(user?.id);
      fetchRecords(user?.id);
    }
  };

  const fetchRecords = async (pId) => {
    const id = pId || patientId || user?.id;
    try {
      const response = await api.get(`/patients/${id}/records`);
      setRecords(response.data?.data || []);
    } catch (error) {
      console.error('Error fetching records:', error);
      toast.error('Failed to load records');
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!uploadForm.file) { toast.error('Please select a file'); return; }
    setUploading(true);
    try {
      const formData = new FormData();
      formData.append('file', uploadForm.file);
      formData.append('patientId', patientId || user?.id);
      formData.append('recordType', uploadForm.recordType);
      formData.append('description', uploadForm.description);
      formData.append('diagnosis', uploadForm.diagnosis);
      formData.append('treatment', uploadForm.treatment);

      await api.post('/records/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      toast.success('Record uploaded successfully!');
      setShowUpload(false);
      setUploadForm({ recordType: 'LAB_RESULT', description: '', diagnosis: '', treatment: '', file: null });
      fetchRecords(patientId);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Upload failed');
    } finally {
      setUploading(false);
    }
  };

  const handleDownload = async (recordId) => {
    try {
      const response = await api.get(`/records/${recordId}/download`, { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.download = `record-${recordId}`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      toast.success('Download started');
    } catch (error) {
      toast.error('Download failed');
    }
  };

  const handleDelete = async (recordId) => {
    if (!window.confirm('Delete this record?')) return;
    try {
      await api.delete(`/records/${recordId}`);
      toast.success('Record deleted');
      fetchRecords(patientId);
    } catch (error) {
      toast.error('Delete failed');
    }
  };

  const handleVerify = async (recordId) => {
    try {
      const response = await api.get(`/records/${recordId}/verify`);
      toast.success('Record verified on blockchain!');
      setSelectedRecord(response.data?.data);
    } catch (error) {
      toast.error('Verification failed');
    }
  };

  if (loading) return <div className="loading-container"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="flex-between mb-20">
        <div>
          <h1 style={{ fontSize: '1.8em', fontWeight: 800, color: '#0F172A' }}>📋 My Medical Records</h1>
          <p style={{ color: '#475569', marginTop: 4 }}>
            View, upload, and manage your medical records securely | Patient ID: <strong>{patientId}</strong>
          </p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowUpload(!showUpload)}>
          {showUpload ? '✕ Cancel' : '📤 Upload New Record'}
        </button>
      </div>

      <div className="info-box">
        <span>💡</span>
        <div>
          <strong>How It Works:</strong> Upload your medical files. They are encrypted with AES-256 and stored securely on IPFS. Every access is logged on the blockchain.
        </div>
      </div>

      {showUpload && (
        <div className="card mb-20">
          <h3 className="card-title">Upload Medical Record</h3>
          <form onSubmit={handleUpload}>
            <div className="grid grid-2">
              <div className="form-group">
                <label className="form-label">Record Type</label>
                <select className="form-select" value={uploadForm.recordType} onChange={e => setUploadForm({...uploadForm, recordType: e.target.value})}>
                  <option value="LAB_RESULT">Lab Result</option>
                  <option value="IMAGING">Imaging</option>
                  <option value="PRESCRIPTION">Prescription</option>
                  <option value="SURGERY_REPORT">Surgery Report</option>
                  <option value="DISCHARGE_SUMMARY">Discharge Summary</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">File *</label>
                <input type="file" className="form-input" onChange={e => setUploadForm({...uploadForm, file: e.target.files[0]})} required />
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea className="form-textarea" value={uploadForm.description} onChange={e => setUploadForm({...uploadForm, description: e.target.value})} rows="2" />
              </div>
              <div className="form-group">
                <label className="form-label">Diagnosis</label>
                <input className="form-input" value={uploadForm.diagnosis} onChange={e => setUploadForm({...uploadForm, diagnosis: e.target.value})} />
              </div>
            </div>
            <button type="submit" className="btn btn-primary w-full mt-20" disabled={uploading}>
              {uploading ? 'Uploading...' : 'Upload Record'}
            </button>
          </form>
        </div>
      )}

      <div className="card">
        <div className="card-header">
          <h3>All Records ({records.length})</h3>
          <button className="btn btn-outline btn-sm" onClick={() => fetchRecords(patientId)}>🔄 Refresh</button>
        </div>
        {records.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 40, color: '#94A3B8' }}>
            <p>No records found. Upload your first medical record.</p>
          </div>
        ) : (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr><th>ID</th><th>Type</th><th>File</th><th>Description</th><th>Size</th><th>Date</th><th>Blockchain</th><th>Actions</th></tr>
              </thead>
              <tbody>
                {records.map(r => (
                  <tr key={r.id}>
                    <td>#{r.id}</td>
                    <td><span className="badge badge-info">{r.recordType?.replace(/_/g, ' ')}</span></td>
                    <td>{r.fileName || 'N/A'}</td>
                    <td style={{ maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{r.description || '—'}</td>
                    <td>{r.fileSize ? `${(r.fileSize / 1024).toFixed(1)} KB` : '—'}</td>
                    <td>{r.createdAt ? new Date(r.createdAt).toLocaleDateString() : '—'}</td>
                    <td>{r.blockchainTxId ? <span className="badge badge-success">✓ Verified</span> : <span className="badge badge-warning">Pending</span>}</td>
                    <td>
                      <button className="btn btn-sm btn-outline" onClick={() => handleDownload(r.id)}>📥</button>
                      <button className="btn btn-sm btn-outline" onClick={() => handleVerify(r.id)} style={{ marginLeft: 4 }}>🔍</button>
                      <button className="btn btn-sm btn-danger" onClick={() => handleDelete(r.id)} style={{ marginLeft: 4 }}>🗑️</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}