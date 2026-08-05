import { useState, useEffect } from 'react';
import api from '../api/axios';
import { SearchIcon } from '../components/Icons';
import toast from 'react-hot-toast';

export default function Doctors() {
  const [doctors, setDoctors] = useState([]);
  const [filteredDoctors, setFilteredDoctors] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => { fetchAllDoctors(); }, []);

  const fetchAllDoctors = async () => {
    setLoading(true);
    try {
      const r = await api.get('/doctors/search?q=');
      const all = r.data?.data || [];
      setDoctors(all); setFilteredDoctors(all);
    } catch(err) {
      if(err.response?.status === 403) {
        try {
          const r = await fetch('http://localhost:8081/api/v1/doctors/search?q=');
          const data = await r.json();
          const all = data?.data || [];
          setDoctors(all); setFilteredDoctors(all);
        } catch(e) { toast.error('Failed to load doctors'); }
      }
    } finally { setLoading(false); }
  };

  const handleFilter = (value) => {
    setSearchTerm(value);
    if(!value.trim()) { setFilteredDoctors(doctors); return; }
    const s = value.toLowerCase();
    setFilteredDoctors(doctors.filter(d =>
      (d.firstName?.toLowerCase().includes(s)) || (d.lastName?.toLowerCase().includes(s)) ||
      (d.specialty?.toLowerCase().includes(s)) || (d.hospitalAffiliation?.toLowerCase().includes(s))
    ));
  };

  if(loading) return <div className="loading-container"><div className="spinner"></div></div>;

  return (
    <div>
      <div className="flex-between mb-20">
        <div>
          <h1 style={{ fontSize: '1.8em', fontWeight: 800, color: '#0F172A' }}>Find Doctors</h1>
          <p style={{ color: '#475569', marginTop: 4 }}>Browse registered doctors — use Doctor ID to grant access</p>
        </div>
        <button className="btn btn-outline btn-sm" onClick={fetchAllDoctors}>🔄 Refresh</button>
      </div>

      <div className="card mb-20">
        <div className="info-box">
          <strong>How to grant access:</strong> Find a doctor below, note their <strong>Doctor ID</strong>, go to <strong>Consents</strong> page and select that doctor.
        </div>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center', flexWrap: 'wrap' }}>
          <div style={{ flex: 1, minWidth: 200, position: 'relative' }}>
            <span style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: '#94A3B8', zIndex: 1 }}>
              <SearchIcon size={16} />
            </span>
            <input className="form-input" value={searchTerm} onChange={e => handleFilter(e.target.value)}
              placeholder="Search by name, specialty, or hospital..." style={{ paddingLeft: 40 }} />
          </div>
          {searchTerm && <button className="btn btn-outline btn-sm" onClick={() => { setSearchTerm(''); setFilteredDoctors(doctors); }}>✕ Clear</button>}
        </div>
      </div>

      {filteredDoctors.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: 60 }}>
          <p style={{ fontSize: '1.2em', color: '#94A3B8' }}>No doctors found</p>
        </div>
      ) : (
        <div className="grid grid-3">
          {filteredDoctors.map(d => (
            <div key={d.id} className="card" style={{ textAlign: 'center' }}>
              <div style={{ width: 60, height: 60, background: 'linear-gradient(135deg, #0D9488, #0891B2)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 12px', color: 'white', fontSize: '1.3em', fontWeight: 700 }}>
                {d.firstName?.[0]}{d.lastName?.[0]}
              </div>
              <div style={{ background: '#F0FDFA', color: '#0D9488', padding: '6px 16px', borderRadius: 20, fontSize: '0.9em', fontWeight: 700, marginBottom: 10, display: 'inline-block', border: '2px solid #0D9488' }}>
                Doctor ID: {d.id}
              </div>
              <h3 style={{ fontSize: '1.1em', fontWeight: 700, color: '#0F172A', marginBottom: 4 }}>Dr. {d.firstName} {d.lastName}</h3>
              <span className="badge badge-info" style={{ marginBottom: 8 }}>{d.specialty}</span>
              <p style={{ color: '#475569', fontSize: '0.85em', marginTop: 8 }}>🏥 {d.hospitalAffiliation}</p>
              <p style={{ color: '#94A3B8', fontSize: '0.8em', marginTop: 4 }}>{d.yearsOfExperience || 0} years exp.</p>
              <div style={{ marginTop: 10, display: 'flex', gap: 8, justifyContent: 'center', flexWrap: 'wrap' }}>
                <span className={`badge ${d.isAvailable ? 'badge-success' : 'badge-danger'}`}>{d.isAvailable ? '✓ Available' : '✕ Unavailable'}</span>
                <span className={`badge ${d.isVerified ? 'badge-success' : 'badge-warning'}`}>{d.isVerified ? '✓ Verified' : 'Pending'}</span>
              </div>
              <button className="btn btn-primary btn-sm w-full mt-20" onClick={() => window.location.href = '/consents'}>Grant Access</button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}