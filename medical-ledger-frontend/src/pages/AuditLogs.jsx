import { useState, useEffect } from 'react';
import api from '../api/axios';

const AuditLogs = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchLogs();
  }, []);

  const fetchLogs = async () => {
    try {
      const response = await api.get('/audit/global');
      setLogs(response.data.data || []);
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1 style={{ marginBottom: '30px' }}>📊 Audit Logs</h1>
      <div className="card">
        <h3>Global Audit Logs ({logs.length})</h3>
        {loading ? (
          <div className="text-center" style={{ padding: '40px' }}><div className="spinner"></div></div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Action</th>
                <th>Actor</th>
                <th>IP Address</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {logs.map(log => (
                <tr key={log.id}>
                  <td>{log.id}</td>
                  <td><span className="badge badge-info">{log.action}</span></td>
                  <td>{log.actor?.username || 'N/A'}</td>
                  <td>{log.ipAddress || 'N/A'}</td>
                  <td>{new Date(log.createdAt).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default AuditLogs;
