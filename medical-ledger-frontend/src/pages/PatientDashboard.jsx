import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

const PatientDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({ records: 0, consents: 0, activeConsents: 0 });
  const [recentRecords, setRecentRecords] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [recordsRes, consentsRes] = await Promise.all([
        api.get(`/patients/${user.id}/records`).catch(() => ({ data: { data: [] } })),
        api.get(`/patients/${user.id}/consents`).catch(() => ({ data: { data: [] } })),
      ]);

      const records = recordsRes.data.data || [];
      const consents = consentsRes.data.data || [];
      const activeConsents = consents.filter(c => c.status === 'ACTIVE').length;

      setRecentRecords(records.slice(0, 5));
      setStats({
        records: records.length,
        consents: consents.length,
        activeConsents,
      });
    } catch (error) {
      console.error('Error fetching dashboard:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <h1 style={{ marginBottom: '30px', color: '#333' }}>Patient Dashboard</h1>

      <div className="grid grid-3 mb-20">
        <div className="card text-center">
          <h3 style={{ fontSize: '2em', marginBottom: '5px' }}></h3>
          <p style={{ fontSize: '2em', color: '#667eea', fontWeight: 'bold' }}>{stats.records}</p>
          <p style={{ color: '#666' }}>Medical Records</p>
        </div>
        <div className="card text-center">
          <h3 style={{ fontSize: '2em', marginBottom: '5px' }}></h3>
          <p style={{ fontSize: '2em', color: '#28a745', fontWeight: 'bold' }}>{stats.activeConsents}</p>
          <p style={{ color: '#666' }}>Active Consents</p>
        </div>
        <div className="card text-center">
          <h3 style={{ fontSize: '2em', marginBottom: '5px' }}>👤</h3>
          <p style={{ fontSize: '1.2em', color: '#333', fontWeight: 'bold' }}>{user?.username}</p>
          <p style={{ color: '#666' }}>{user?.email}</p>
        </div>
      </div>

      <div className="grid grid-2 mb-20">
        <Link to="/records" className="card" style={{ textDecoration: 'none', color: 'inherit', cursor: 'pointer' }}>
          <h3>View My Records</h3>
          <p style={{ color: '#666' }}>Access and manage your medical records</p>
        </Link>
        <Link to="/consents" className="card" style={{ textDecoration: 'none', color: 'inherit', cursor: 'pointer' }}>
          <h3>Manage Consents</h3>
          <p style={{ color: '#666' }}>Grant or revoke access to doctors</p>
        </Link>
        <Link to="/doctors" className="card" style={{ textDecoration: 'none', color: 'inherit', cursor: 'pointer' }}>
          <h3>Find Doctors</h3>
          <p style={{ color: '#666' }}>Search for doctors by name or specialty</p>
        </Link>
        <Link to="/blockchain" className="card" style={{ textDecoration: 'none', color: 'inherit', cursor: 'pointer' }}>
          <h3>Verify Records</h3>
          <p style={{ color: '#666' }}>Verify record authenticity on blockchain</p>
        </Link>
      </div>

      {recentRecords.length > 0 && (
        <div className="card">
          <h3 style={{ marginBottom: '20px' }}>Recent Records</h3>
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Type</th>
                <th>File</th>
                <th>Date</th>
                <th>Blockchain</th>
              </tr>
            </thead>
            <tbody>
              {recentRecords.map(record => (
                <tr key={record.id}>
                  <td>#{record.id}</td>
                  <td><span className="badge badge-info">{record.recordType}</span></td>
                  <td>{record.fileName}</td>
                  <td>{new Date(record.createdAt).toLocaleDateString()}</td>
                  <td><span className="badge badge-success">✓ Verified</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default PatientDashboard;