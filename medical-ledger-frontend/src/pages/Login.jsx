import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LockIcon, ArrowRightIcon } from '../components/Icons';

const Login = () => {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    const success = await login(formData.email, formData.password);
    setLoading(false);
    if (success) navigate('/dashboard');
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <LockIcon />
        </div>
        <h2>Welcome Back</h2>
        <p className="auth-subtitle">Sign in to access your health dashboard</p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              className="form-input"
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              placeholder="doctor@hospital.com"
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-input"
              value={formData.password}
              onChange={(e) => setFormData({...formData, password: e.target.value})}
              placeholder="••••••••"
              required
            />
          </div>
          <button type="submit" className="btn btn-primary w-full mt-20" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'} {!loading && <ArrowRightIcon />}
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: '24px', color: '#64748B', fontSize: '0.9em' }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ color: '#0D9488', fontWeight: 600 }}>
            Create one
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Login;