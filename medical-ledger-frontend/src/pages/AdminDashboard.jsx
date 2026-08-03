import { useState, useEffect } from 'react';
import api from '../api/axios';

const AdminDashboard = () => {
  const [stats, setStats] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const response = await api.get('/admin/stats');
      setStats(response.data.data || {});
    } catch (error) {
      console.error('Error fetching stats:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading"><div className="spinner"></div></div>;

  return (
    <div>
      <h1 style={{ marginBottom: '30px', color: '#333' }}>Admin Dashboard</h1>

      <div className="grid grid-4 mb-20">
        <div className="card text-center">
          <h3>👥 Total Users</h3>
          <p style={{ fontSize: '2.5em', color: '#667eea', fontWeight: 'bold' }}>{stats.totalUsers || 0}</p>
        </div>
        <div className="card text-center">
          <h3>Active</h3>
          <p style={{ fontSize: '2.5em', color: '#28a745', fontWeight: 'bold' }}>{stats.activeUsers || 0}</p>
        </div>
        <div className="card text-center">
          <h3>Patients</h3>
          <p style={{ fontSize: '2.5em', color: '#667eea', fontWeight: 'bold' }}>{stats.patients || 0}</p>
        </div>
        <div className="card text-center">
          <h3>Doctors</h3>
          <p style={{ fontSize: '2.5em', color: '#667eea', fontWeight: 'bold' }}>{stats.doctors || 0}</p>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
