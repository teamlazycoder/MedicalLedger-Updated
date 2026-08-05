import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { SearchIcon } from '../components/Icons';
import toast from 'react-hot-toast';

export default function DoctorDashboard() {
  const { user } = useAuth();
  const [consents, setConsents] = useState([]);
  const [patientRecords, setPatientRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchPatientId, setSearchPatientId] = useState('');
  const [viewingPatient, setViewingPatient] = useState(null);
  const [doctorId, setDoctorId] = useState(null);

  useEffect(() => {
    fetchDoctorProfile();
  }, []);

  const fetchDoctorProfile = async () => {
    setLoading(true);
    try {
      // Try to get doctor by USER ID first
      const response = await api.get(`/doctors/by-user/${user?.id}`);
      if (response.data?.success && response.data?.data) {
        setDoctorId(response.data.data.id);
        fetchConsents(response.data.data.id);
        return;
      }
    } catch (e) {
      console.log('by-user endpoint failed, trying direct ID');
    }

    try {
      // Fallback: try direct doctor ID
      const response = await api.get(`/doctors/${user?.id}`);
      if (response.data?.success && response.data?.data) {
        setDoctorId(response.data.data.id);
        fetchConsents(response.data.data.id);
        return;
      }
    } catch (e) {
      console.error('Failed to fetch doctor profile');
      toast.error('Could not load your doctor profile');
    }
    setLoading(false);
  };

  const fetchConsents = async (docId) => {
    try {
      const response = await api.get(`/doctors/${docId}/consents`);
      setConsents(response.data?.data || []);
    } catch (error) {
      console.error('Error fetching consents:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleViewPatientRecords = async () => {
    if (!searchPatientId.trim()) { toast.error('Enter Patient ID'); return; }
    try {
      setLoading(true);
      const response = await api.get(`/patients/${searchPatientId}/records`);
      setPatientRecords(response.data?.data || []);
      setViewingPatient(searchPatientId);
    } catch (error) {
      if (error.response?.status === 403) toast.error('Access denied. No consent.');
      else if (error.response?.status === 404) toast.error('Patient not found');
      else toast.error('Failed to load records');
      setPatientRecords([]);
    } finally { setLoading(false); }
  };

  if (loading) return <div className="loading-container"><div className="spinner"></div></div>;

  const activeConsents = consents.filter(c => c.status === 'ACTIVE').length;

  return (
    <div>
      <div className="flex-between mb-20">
        <div>
          <h1 style={{ fontSize: '1.8em', fontWeight: 800, color: '#0F172A' }}>Doctor Dashboard</h1>
          <p style={{ color: '#475569' }}>
            Welcome, Dr. {user?.username} | Doctor ID: <strong style={{ color: '#0D9488' }}>{doctorId}</strong>
          </p>
        </div>
        <button className="btn btn-outline btn-sm" onClick={fetchDoctorProfile}>🔄 Refresh</button>
      </div>

      <div className="stats-row">
        <div className="stat-card"><div className="stat-value">{activeConsents}</div><div className="stat-label">Active Consents</div></div>
        <div className="stat-card"><div className="stat-value">{consents.length}</div><div className="stat-label">Total Patients</div></div>
      </div>

      <div className="card mb-20">
        <h3 className="card-title">📋 Patients Who Granted Me Access</h3>
        {consents.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 40, color: '#94A3B8' }}>
            <p>No patients yet. Share your Doctor ID: <strong>{doctorId}</strong></p>
          </div>
        ) : (
          <div className="table-wrap">
            <table className="table">
              <thead><tr><th>Patient ID</th><th>Patient Name</th><th>Status</th><th>Start</th><th>End</th><th>Purpose</th><th>Action</th></tr></thead>
              <tbody>
                {consents.map(c => (
                  <tr key={c.id}>
                    <td><span style={{ fontWeight: 700, color: '#0D9488', cursor: 'pointer' }} onClick={() => { setSearchPatientId(c.patientId); handleViewPatientRecords(); }}>#{c.patientId}</span></td>
                    <td><strong>{c.patientName || 'Unknown'}</strong></td>
                    <td><span className={`badge ${c.status === 'ACTIVE' ? 'badge-success' : c.status === 'EXPIRED' ? 'badge-warning' : 'badge-danger'}`}>{c.status}</span></td>
                    <td>{c.startDate ? new Date(c.startDate).toLocaleDateString() : 'N/A'}</td>
                    <td>{c.endDate ? new Date(c.endDate).toLocaleDateString() : 'N/A'}</td>
                    <td>{c.purpose || '—'}</td>
                    <td>{c.status === 'ACTIVE' && <button className="btn btn-sm btn-primary" onClick={() => { setSearchPatientId(c.patientId); handleViewPatientRecords(); }}>View Records</button>}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <div className="card mb-20">
        <h3 className="card-title">🔍 View Patient Records</h3>
        <div style={{ display: 'flex', gap: 12 }}>
          <input className="form-input" type="number" value={searchPatientId} onChange={e => setSearchPatientId(e.target.value)} placeholder="Enter Patient ID" style={{ flex: 1 }} />
          <button className="btn btn-primary" onClick={handleViewPatientRecords}><SearchIcon size={16} /> View Records</button>
        </div>
      </div>

      {viewingPatient && (
        <div className="card">
          <div className="card-header"><h3>Records for Patient #{viewingPatient} ({patientRecords.length})</h3><button className="btn btn-sm btn-outline" onClick={() => { setPatientRecords([]); setViewingPatient(null); }}>Clear</button></div>
          {patientRecords.length === 0 ? <p style={{ textAlign: 'center', padding: 40, color: '#94A3B8' }}>No records found</p> :
            <div className="table-wrap"><table className="table"><thead><tr><th>ID</th><th>Type</th><th>File</th><th>Description</th><th>Date</th><th>Blockchain</th></tr></thead><tbody>
              {patientRecords.map(r => (<tr key={r.id}><td>#{r.id}</td><td><span className="badge badge-info">{r.recordType?.replace(/_/g, ' ')}</span></td><td>{r.fileName}</td><td>{r.description || '—'}</td><td>{r.createdAt ? new Date(r.createdAt).toLocaleDateString() : '—'}</td><td>{r.blockchainTxId ? <span className="badge badge-success">✓ Verified</span> : <span className="badge badge-warning">Pending</span>}</td></tr>))}
            </tbody></table></div>
          }
        </div>
      )}
    </div>
  );
}