import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function PatientConsents() {
  const { user } = useAuth();
  const [consents, setConsents] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [showGrant, setShowGrant] = useState(false);
  const [patientId, setPatientId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [grantForm, setGrantForm] = useState({
    doctorId: '',
    startDate: '',
    endDate: '',
    purpose: ''
  });

  useEffect(() => {
    fetchPatientId();
    fetchAllDoctors();
  }, []);

  const fetchPatientId = async () => {
    try {
      const response = await api.get(`/patients/${user?.id}`);
      if (response.data?.success && response.data?.data) {
        setPatientId(response.data.data.id);
        fetchConsents(response.data.data.id);
      }
    } catch (error) {
      console.error('Error fetching patient ID:', error);
      setPatientId(user?.id);
      fetchConsents(user?.id);
    } finally {
      setLoading(false);
    }
  };

  const fetchConsents = async (pId) => {
    const id = pId || patientId || user?.id;
    try {
      const response = await api.get(`/patients/${id}/consents`);
      setConsents(response.data?.data || []);
    } catch (error) {
      console.error('Error fetching consents:', error);
    }
  };

  const fetchAllDoctors = async () => {
    try {
      const response = await api.get('/doctors/search?q=');
      setDoctors(response.data?.data || []);
    } catch (error) {
      toast.error('Could not load doctors');
    }
  };

  const handleGrant = async (e) => {
    e.preventDefault();

    if (!grantForm.doctorId) {
      toast.error('Please select a doctor');
      return;
    }

    const pId = patientId || user?.id;

    try {
      await api.post(`/patients/${pId}/consents/grant`, {
        doctorId: parseInt(grantForm.doctorId),
        recordType: 'ALL',
        startDate: grantForm.startDate || new Date().toISOString(),
        endDate: grantForm.endDate || new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString(),
        purpose: grantForm.purpose
      });

      toast.success('Consent granted successfully!');
      setShowGrant(false);
      setGrantForm({ doctorId: '', startDate: '', endDate: '', purpose: '' });
      fetchConsents(pId);
    } catch (error) {
      const message = error.response?.data?.message || 'Failed to grant consent';
      toast.error(message);
    }
  };

  const handleRevoke = async (consentId) => {
    if (!window.confirm('Are you sure you want to revoke this consent? The doctor will no longer be able to access your records.')) {
      return;
    }

    const pId = patientId || user?.id;

    try {
      await api.put(`/patients/${pId}/consents/${consentId}/revoke`);
      toast.success('Consent revoked successfully!');
      fetchConsents(pId);
    } catch (error) {
      toast.error('Failed to revoke consent');
    }
  };

  const handleRenew = async (consentId) => {
    const newEndDate = prompt('Enter new end date (YYYY-MM-DDTHH:MM):');
    if (!newEndDate) return;

    try {
      await api.put(`/consents/${consentId}/renew`, null, {
        params: { newEndDate }
      });
      toast.success('Consent renewed!');
      fetchConsents(patientId);
    } catch (error) {
      toast.error('Failed to renew consent');
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
      </div>
    );
  }

  const activeConsents = consents.filter(c => c.status === 'ACTIVE').length;
  const expiredConsents = consents.filter(c => c.status === 'EXPIRED').length;
  const revokedConsents = consents.filter(c => c.status === 'REVOKED').length;

  return (
    <div>
      {/* Header */}
      <div className="flex-between mb-20">
        <div>
          <h1 style={{ fontSize: '1.8em', fontWeight: 800, color: '#0F172A' }}>Consent Management</h1>
          <p style={{ color: '#475569', marginTop: 4 }}>
            Control which doctors can access your medical records | Patient ID: <strong style={{ color: '#0D9488' }}>{patientId}</strong>
          </p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowGrant(!showGrant)}>
          {showGrant ? '✕ Cancel' : '✅ Grant New Consent'}
        </button>
      </div>

      {/* Stats */}
      <div className="stats-row mb-20">
        <div className="stat-card">
          <div className="stat-value">{activeConsents}</div>
          <div className="stat-label">Active Consents</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{consents.length}</div>
          <div className="stat-label">Total Consents</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{expiredConsents}</div>
          <div className="stat-label">Expired</div>
        </div>
      </div>

      {/* Grant Consent Form */}
      {showGrant && (
        <div className="card mb-20">
          <h3 className="card-title">Grant Access to Doctor</h3>

          <div className="info-box">
            <span style={{ fontSize: '1.2em' }}>💡</span>
            <div>
              <strong>How to grant access:</strong> Select a doctor from the dropdown below.
              You can find Doctor IDs on the <strong>Find Doctors</strong> page.
              Ask your doctor for their Doctor ID if you don't see them in the list.
            </div>
          </div>

          <form onSubmit={handleGrant}>
            <div className="form-group">
              <label className="form-label">Select Doctor *</label>
              <select
                className="form-select"
                value={grantForm.doctorId}
                onChange={(e) => setGrantForm({ ...grantForm, doctorId: e.target.value })}
                required
              >
                <option value="">-- Choose a Doctor --</option>
                {doctors.map((doc) => (
                  <option key={doc.id} value={doc.id}>
                    ID: {doc.id} — Dr. {doc.firstName} {doc.lastName} — {doc.specialty} ({doc.hospitalAffiliation})
                  </option>
                ))}
              </select>
              {doctors.length === 0 && (
                <p style={{ color: '#94A3B8', fontSize: '0.85em', marginTop: 6 }}>
                  No doctors registered yet. Please check back later.
                </p>
              )}
            </div>

            <div className="grid grid-2">
              <div className="form-group">
                <label className="form-label">Start Date (optional)</label>
                <input
                  type="datetime-local"
                  className="form-input"
                  value={grantForm.startDate}
                  onChange={(e) => setGrantForm({ ...grantForm, startDate: e.target.value })}
                />
                <span className="form-hint">Defaults to now if left empty</span>
              </div>
              <div className="form-group">
                <label className="form-label">End Date (optional)</label>
                <input
                  type="datetime-local"
                  className="form-input"
                  value={grantForm.endDate}
                  onChange={(e) => setGrantForm({ ...grantForm, endDate: e.target.value })}
                />
                <span className="form-hint">Defaults to 1 year if left empty</span>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Purpose / Reason</label>
              <input
                className="form-input"
                value={grantForm.purpose}
                onChange={(e) => setGrantForm({ ...grantForm, purpose: e.target.value })}
                placeholder="e.g., Annual health checkup, Emergency consultation, Second opinion"
              />
            </div>

            <button type="submit" className="btn btn-primary w-full mt-20">
              🔒 Grant Access
            </button>
          </form>
        </div>
      )}

      {/* Consents List */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">My Consents ({consents.length})</h3>
          <button className="btn btn-outline btn-sm" onClick={() => fetchConsents(patientId)}>
            🔄 Refresh
          </button>
        </div>

        {consents.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 60, color: '#94A3B8' }}>
            <p style={{ fontSize: '1.1em', marginBottom: 8 }}>🔒 No Consents Granted Yet</p>
            <p style={{ marginBottom: 20 }}>
              You haven't granted access to any doctor yet.
              Click "Grant New Consent" to share your medical records securely.
            </p>
            <button className="btn btn-primary" onClick={() => setShowGrant(true)}>
              Grant New Consent
            </button>
          </div>
        ) : (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Doctor</th>
                  <th>Doctor ID</th>
                  <th>Status</th>
                  <th>Start Date</th>
                  <th>End Date</th>
                  <th>Purpose</th>
                  <th>Blockchain</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {consents.map((consent) => (
                  <tr key={consent.id}>
                    <td>
                      <strong>{consent.doctorName || 'Unknown Doctor'}</strong>
                    </td>
                    <td>
                      <span style={{ color: '#0D9488', fontWeight: 600 }}>#{consent.doctorId}</span>
                    </td>
                    <td>
                      <span className={`badge ${
                        consent.status === 'ACTIVE' ? 'badge-success' :
                        consent.status === 'EXPIRED' ? 'badge-warning' :
                        'badge-danger'
                      }`}>
                        {consent.status}
                      </span>
                    </td>
                    <td>
                      {consent.startDate ? new Date(consent.startDate).toLocaleDateString() : 'N/A'}
                    </td>
                    <td>
                      {consent.endDate ? new Date(consent.endDate).toLocaleDateString() : 'N/A'}
                    </td>
                    <td style={{ maxWidth: 150, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {consent.purpose || '—'}
                    </td>
                    <td>
                      {consent.blockchainTxId ? (
                        <span className="badge badge-success" title={consent.blockchainTxId}>
                          ✓ Secured
                        </span>
                      ) : (
                        <span className="badge badge-warning">⏳ Pending</span>
                      )}
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: 6 }}>
                        {consent.status === 'ACTIVE' && (
                          <>
                            <button
                              className="btn btn-sm btn-danger"
                              onClick={() => handleRevoke(consent.id)}
                            >
                              Revoke
                            </button>
                            <button
                              className="btn btn-sm btn-outline"
                              onClick={() => handleRenew(consent.id)}
                            >
                              Renew
                            </button>
                          </>
                        )}
                      </div>
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