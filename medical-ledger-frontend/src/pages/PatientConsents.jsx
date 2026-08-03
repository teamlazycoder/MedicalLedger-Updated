import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

const PatientConsents = () => {
  const { user } = useAuth();
  const [consents, setConsents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showGrant, setShowGrant] = useState(false);
  const [grantData, setGrantData] = useState({
    doctorId: '',
    startDate: '',
    endDate: '',
    purpose: ''
  });

  useEffect(() => {
    fetchConsents();
  }, []);

  const fetchConsents = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/patients/${user.id}/consents`);
      setConsents(response.data.data || []);
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleGrant = async (e) => {
    e.preventDefault();
    try {
      await api.post(`/patients/${user.id}/consents/grant`, {
        doctorId: parseInt(grantData.doctorId),
        startDate: grantData.startDate,
        endDate: grantData.endDate,
        purpose: grantData.purpose,
        recordType: 'ALL'
      });
      toast.success('Consent granted!');
      setShowGrant(false);
      fetchConsents();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed');
    }
  };

  const handleRevoke = async (consentId) => {
    if (!window.confirm('Revoke this consent?')) return;
    try {
      await api.put(`/patients/${user.id}/consents/${consentId}/revoke`);
      toast.success('Consent revoked!');
      fetchConsents();
    } catch (error) {
      toast.error('Failed to revoke');
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
        <h1>Consent Management</h1>
        <button className="btn" onClick={() => setShowGrant(!showGrant)}>
          {showGrant ? 'Cancel' : 'Grant Consent'}
        </button>
      </div>

      {showGrant && (
        <div className="card mb-20">
          <h3>Grant Consent to Doctor</h3>
          <form onSubmit={handleGrant}>
            <div className="grid grid-2">
              <div className="form-group">
                <label>Doctor ID</label>
                <input type="number" value={grantData.doctorId} onChange={(e) => setGrantData({...grantData, doctorId: e.target.value})} required />
              </div>
              <div className="form-group">
                <label>Purpose</label>
                <input value={grantData.purpose} onChange={(e) => setGrantData({...grantData, purpose: e.target.value})} placeholder="Annual checkup" />
              </div>
              <div className="form-group">
                <label>Start Date</label>
                <input type="datetime-local" value={grantData.startDate} onChange={(e) => setGrantData({...grantData, startDate: e.target.value})} required />
              </div>
              <div className="form-group">
                <label>End Date</label>
                <input type="datetime-local" value={grantData.endDate} onChange={(e) => setGrantData({...grantData, endDate: e.target.value})} required />
              </div>
            </div>
            <button type="submit" className="btn mt-20">Grant Consent</button>
          </form>
        </div>
      )}

      <div className="card">
        <h3>My Consents ({consents.length})</h3>
        {loading ? (
          <div className="text-center" style={{ padding: '40px' }}><div className="spinner"></div></div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Doctor</th>
                <th>Status</th>
                <th>Start</th>
                <th>End</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {consents.map(consent => (
                <tr key={consent.id}>
                  <td>#{consent.id}</td>
                  <td>{consent.doctorName || 'N/A'}</td>
                  <td><span className={`badge badge-${consent.status === 'ACTIVE' ? 'success' : 'danger'}`}>{consent.status}</span></td>
                  <td>{new Date(consent.startDate).toLocaleDateString()}</td>
                  <td>{new Date(consent.endDate).toLocaleDateString()}</td>
                  <td>
                    {consent.status === 'ACTIVE' && (
                      <button className="btn btn-sm btn-danger" onClick={() => handleRevoke(consent.id)}>Revoke</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default PatientConsents;
