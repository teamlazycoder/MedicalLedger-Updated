import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserIcon, ArrowRightIcon } from '../components/Icons';

const Register = () => {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    username: '', email: '', password: '', role: 'PATIENT',
    firstName: '', lastName: '', dateOfBirth: '', gender: '',
    bloodType: '', contactNumber: '', address: '',
    specialty: '', licenseNumber: '', hospitalAffiliation: '',
    yearsOfExperience: '', qualifications: '',
  });
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => setFormData({...formData, [e.target.name]: e.target.value});

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    const data = {...formData};
    if (data.yearsOfExperience) data.yearsOfExperience = parseInt(data.yearsOfExperience);
    const success = await register(data);
    setLoading(false);
    if (success) navigate('/dashboard');
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card" style={{ maxWidth: '560px' }}>
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <UserIcon />
        </div>
        <h2>Create Account</h2>
        <p className="auth-subtitle">Join HealthChain and own your medical data</p>

        <div className="step-bars">
          <div className={`step-bar ${step >= 1 ? 'active' : ''}`}></div>
          <div className={`step-bar ${step >= 2 ? 'active' : ''}`}></div>
        </div>

        <form onSubmit={handleSubmit}>
          {step === 1 && (
            <>
              <div className="form-group">
                <label className="form-label">Username</label>
                <input name="username" className="form-input" value={formData.username} onChange={handleChange} required placeholder="johndoe" />
              </div>
              <div className="form-group">
                <label className="form-label">Email Address</label>
                <input type="email" name="email" className="form-input" value={formData.email} onChange={handleChange} required placeholder="john@example.com" />
              </div>
              <div className="form-group">
                <label className="form-label">Password</label>
                <input type="password" name="password" className="form-input" value={formData.password} onChange={handleChange} required placeholder="Min 8 characters" minLength="8" />
              </div>
              <div className="form-group">
                <label className="form-label">I am a</label>
                <select name="role" className="form-select" value={formData.role} onChange={handleChange}>
                  <option value="PATIENT">Patient</option>
                  <option value="DOCTOR">Doctor</option>
                </select>
              </div>
              <div className="grid grid-2">
                <div className="form-group">
                  <label className="form-label">First Name</label>
                  <input name="firstName" className="form-input" value={formData.firstName} onChange={handleChange} required placeholder="John" />
                </div>
                <div className="form-group">
                  <label className="form-label">Last Name</label>
                  <input name="lastName" className="form-input" value={formData.lastName} onChange={handleChange} required placeholder="Doe" />
                </div>
              </div>
              <button type="button" className="btn btn-primary w-full mt-20" onClick={() => setStep(2)}>
                Continue <ArrowRightIcon />
              </button>
            </>
          )}

          {step === 2 && (
            <>
              {formData.role === 'PATIENT' && (
                <>
                  <div className="grid grid-2">
                    <div className="form-group">
                      <label className="form-label">Date of Birth</label>
                      <input type="date" name="dateOfBirth" className="form-input" value={formData.dateOfBirth} onChange={handleChange} />
                    </div>
                    <div className="form-group">
                      <label className="form-label">Gender</label>
                      <select name="gender" className="form-select" value={formData.gender} onChange={handleChange}>
                        <option value="">Select</option>
                        <option value="MALE">Male</option>
                        <option value="FEMALE">Female</option>
                        <option value="OTHER">Other</option>
                      </select>
                    </div>
                    <div className="form-group">
                      <label className="form-label">Blood Type</label>
                      <input name="bloodType" className="form-input" value={formData.bloodType} onChange={handleChange} placeholder="O+" />
                    </div>
                    <div className="form-group">
                      <label className="form-label">Contact Number</label>
                      <input name="contactNumber" className="form-input" value={formData.contactNumber} onChange={handleChange} placeholder="+91 9876543210" />
                    </div>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Address</label>
                    <textarea name="address" className="form-textarea" value={formData.address} onChange={handleChange} placeholder="Your full address" rows="2" />
                  </div>
                </>
              )}

              {formData.role === 'DOCTOR' && (
                <>
                  <div className="grid grid-2">
                    <div className="form-group">
                      <label className="form-label">Specialty</label>
                      <input name="specialty" className="form-input" value={formData.specialty} onChange={handleChange} required placeholder="Cardiology" />
                    </div>
                    <div className="form-group">
                      <label className="form-label">License Number</label>
                      <input name="licenseNumber" className="form-input" value={formData.licenseNumber} onChange={handleChange} placeholder="MED-12345" />
                    </div>
                    <div className="form-group">
                      <label className="form-label">Hospital Affiliation</label>
                      <input name="hospitalAffiliation" className="form-input" value={formData.hospitalAffiliation} onChange={handleChange} required placeholder="City General Hospital" />
                    </div>
                    <div className="form-group">
                      <label className="form-label">Years of Experience</label>
                      <input type="number" name="yearsOfExperience" className="form-input" value={formData.yearsOfExperience} onChange={handleChange} placeholder="10" />
                    </div>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Qualifications</label>
                    <textarea name="qualifications" className="form-textarea" value={formData.qualifications} onChange={handleChange} placeholder="MBBS, MD, DM" rows="2" />
                  </div>
                </>
              )}

              <div className="flex-between mt-20">
                <button type="button" className="btn btn-white" onClick={() => setStep(1)}>← Back</button>
                <button type="submit" className="btn btn-primary" disabled={loading} style={{ flex: 1, marginLeft: '12px', justifyContent: 'center' }}>
                  {loading ? 'Creating Account...' : 'Create Account'}
                </button>
              </div>
            </>
          )}
        </form>

        <p style={{ textAlign: 'center', marginTop: '24px', color: '#64748B', fontSize: '0.9em' }}>
          Already have an account?{' '}
          <Link to="/login" style={{ color: '#0D9488', fontWeight: 600 }}>Sign in</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;