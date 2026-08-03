import { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

const DoctorDashboard = () => {
  const { user } = useAuth();
  const [consents, setConsents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchConsents();
  }, []);

  const fetchConsents = async () => {
    try {
      const response = await api.get(`/doctors/${user?.id}/consents`);
      setConsents(response.data.data || []);
    } catch (error) {
      console.error('Error fetching consents:', error);
    } finally {
      setLoading(false);
    }
  };

  const activeConsents = consents.filter(c => c.status === 'ACTIVE').length;

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <h1 style={{ marginBottom: '30px', color: '#333' }}>Doctor Dashboard</h1>

      <div className="grid grid-3 mb-20">
        <div className="card text-center">
          <h3 style={{ fontSize: '2em' }}></h3>
          <p style={{ fontSize: '2em', color: '#28a745', fontWeight: 'bold' }}>{activeConsents}</p>
          <p style={{ color: '#666' }}>Active Consents</p>
        </div>
        <div className="card text-center">
          <h3 style={{ fontSize: '2em' }}>👥</h3>
          <p style={{ fontSize: '2em', color: '#667eea', fontWeight: 'bold' }}>{consents.length}</p>
          <p style={{ color: '#666' }}>Total Patients</p>
        </div>
        <div className="card text-center">
          <h3 style={{ fontSize: '2em' }}>👤</h3>
          <p style={{ fontSize: '1.2em', color: '#333', fontWeight: 'bold' }}>Dr. {user?.username}</p>
          <p style={{ color: '#666' }}>{user?.email}</p>
        </div>
      </div>

      {consents.length > 0 && (
        <div className="card">
          <h3 style={{ marginBottom: '20px' }}>Patient Consents</h3>
          <table className="table">
            <thead>
              <tr>
                <th>Patient</th>
                <th>Status</th>
                <th>Record Type</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Purpose</th>
              </tr>
            </thead>
            <tbody>
              {consents.map(consent => (
                <tr key={consent.id}>
                  <td>{consent.patientName}</td>
                  <td>
                    <span className={`badge badge-${consent.status === 'ACTIVE' ? 'success' : consent.status === 'EXPIRED' ? 'warning' : 'danger'}`}>
                      {consent.status}
                    </span>
                  </td>
                  <td>{consent.recordType}</td>
                  <td>{new Date(consent.startDate).toLocaleDateString()}</td>
                  <td>{new Date(consent.endDate).toLocaleDateString()}</td>
                  <td>{consent.purpose || 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default DoctorDashboard;
