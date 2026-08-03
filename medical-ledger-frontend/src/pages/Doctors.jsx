import { useState } from 'react';
import api from '../api/axios';

const Doctors = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!searchTerm.trim()) return;
    setLoading(true);
    try {
      const response = await api.get(`/doctors/search?q=${searchTerm}`);
      setDoctors(response.data.data || []);
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1 style={{ marginBottom: '30px' }}>👨‍⚕️ Find Doctors</h1>
      <div className="card mb-20">
        <div style={{ display: 'flex', gap: '10px' }}>
          <input
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search by name, specialty, or hospital"
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            style={{ flex: 1 }}
          />
          <button className="btn" onClick={handleSearch} disabled={loading}>
            {loading ? 'Searching...' : 'Search'}
          </button>
        </div>
      </div>

      {doctors.length > 0 && (
        <div className="grid grid-3">
          {doctors.map(doctor => (
            <div key={doctor.id} className="card">
              <h3>Dr. {doctor.firstName} {doctor.lastName}</h3>
              <p><strong>ID:</strong> {doctor.id}</p>
              <p><strong>Specialty:</strong> {doctor.specialty}</p>
              <p><strong>Hospital:</strong> {doctor.hospitalAffiliation}</p>
              <p><strong>Experience:</strong> {doctor.yearsOfExperience || 0} years</p>
              <span className={`badge ${doctor.isAvailable ? 'badge-success' : 'badge-danger'}`}>
                {doctor.isAvailable ? 'Available' : 'Unavailable'}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Doctors;
