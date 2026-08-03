import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import Modal from '../components/Modal';

const PatientRecords = () => {
  const { user } = useAuth();
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showUpload, setShowUpload] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [uploadData, setUploadData] = useState({
    patientId: user?.id || '',
    recordType: 'LAB_RESULT',
    description: '',
    diagnosis: '',
    treatment: '',
    file: null
  });

  useEffect(() => {
    fetchRecords();
  }, []);

  const fetchRecords = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/patients/${user.id}/records`);
      setRecords(response.data.data || []);
    } catch (error) {
      toast.error('Failed to fetch records');
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!uploadData.file) {
      toast.error('Please select a file');
      return;
    }

    const formData = new FormData();
    formData.append('file', uploadData.file);
    formData.append('patientId', user.id);
    formData.append('recordType', uploadData.recordType);
    formData.append('description', uploadData.description);
    formData.append('diagnosis', uploadData.diagnosis);
    formData.append('treatment', uploadData.treatment);

    try {
      await api.post('/records/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      toast.success('Record uploaded!');
      setShowUpload(false);
      setUploadData({ patientId: user.id, recordType: 'LAB_RESULT', description: '', diagnosis: '', treatment: '', file: null });
      fetchRecords();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Upload failed');
    }
  };

  const handleDownload = async (recordId) => {
    try {
      const response = await api.get(`/records/${recordId}/download`, { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `record-${recordId}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
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
      fetchRecords();
    } catch (error) {
      toast.error('Delete failed');
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
        <h1>Medical Records</h1>
        <button className="btn" onClick={() => setShowUpload(!showUpload)}>
          {showUpload ? 'Cancel' : 'Upload Record'}
        </button>
      </div>

      {showUpload && (
        <div className="card mb-20">
          <h3>Upload Medical Record</h3>
          <form onSubmit={handleUpload}>
            <div className="grid grid-2">
              <div className="form-group">
                <label>Record Type</label>
                <select value={uploadData.recordType} onChange={(e) => setUploadData({...uploadData, recordType: e.target.value})}>
                  <option value="LAB_RESULT">Lab Result</option>
                  <option value="IMAGING">Imaging</option>
                  <option value="PRESCRIPTION">Prescription</option>
                  <option value="SURGERY_REPORT">Surgery Report</option>
                  <option value="DISCHARGE_SUMMARY">Discharge Summary</option>
                </select>
              </div>
              <div className="form-group">
                <label>File</label>
                <input type="file" onChange={(e) => setUploadData({...uploadData, file: e.target.files[0]})} />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea value={uploadData.description} onChange={(e) => setUploadData({...uploadData, description: e.target.value})} rows="2" />
              </div>
              <div className="form-group">
                <label>Diagnosis</label>
                <textarea value={uploadData.diagnosis} onChange={(e) => setUploadData({...uploadData, diagnosis: e.target.value})} rows="2" />
              </div>
            </div>
            <button type="submit" className="btn mt-20">Upload</button>
          </form>
        </div>
      )}

      <div className="card">
        <h3>All Records ({records.length})</h3>
        {loading ? (
          <div className="text-center" style={{ padding: '40px' }}><div className="spinner"></div></div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Type</th>
                <th>File</th>
                <th>Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {records.map(record => (
                <tr key={record.id}>
                  <td>#{record.id}</td>
                  <td><span className="badge badge-info">{record.recordType}</span></td>
                  <td>{record.fileName}</td>
                  <td>{new Date(record.createdAt).toLocaleDateString()}</td>
                  <td>
                    <button className="btn btn-sm" onClick={() => setSelectedRecord(record)}>👁️</button>
                    <button className="btn btn-sm btn-secondary" onClick={() => handleDownload(record.id)} style={{ marginLeft: '5px' }}>📥</button>
                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(record.id)} style={{ marginLeft: '5px' }}>🗑️</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <Modal isOpen={!!selectedRecord} onClose={() => setSelectedRecord(null)} title="Record Details">
        {selectedRecord && (
          <div>
            <p><strong>ID:</strong> #{selectedRecord.id}</p>
            <p><strong>Type:</strong> {selectedRecord.recordType}</p>
            <p><strong>File:</strong> {selectedRecord.fileName}</p>
            <p><strong>Description:</strong> {selectedRecord.description || 'N/A'}</p>
            <p><strong>Diagnosis:</strong> {selectedRecord.diagnosis || 'N/A'}</p>
            <p><strong>Treatment:</strong> {selectedRecord.treatment || 'N/A'}</p>
            <p><strong>IPFS Hash:</strong> <small>{selectedRecord.ipfsHash}</small></p>
            <p><strong>Blockchain TX:</strong> <small>{selectedRecord.blockchainTxId}</small></p>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default PatientRecords;
