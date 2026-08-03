import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { HeartIcon, LogoutIcon } from './Icons';

const Navbar = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">
        <HeartIcon />
        HealthChain
      </Link>
      <div className="navbar-links">
        {isAuthenticated ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/profile">Profile</Link>
            {(user?.role === 'PATIENT' || user?.role === 'DOCTOR') && (
              <>
                <Link to="/records">Records</Link>
                <Link to="/consents">Consents</Link>
              </>
            )}
            {user?.role === 'PATIENT' && <Link to="/doctors">Doctors</Link>}
            {user?.role === 'ADMIN' && (
              <>
                <Link to="/admin">Admin</Link>
                <Link to="/audit">Audit</Link>
              </>
            )}
            <Link to="/blockchain">Verify</Link>
            <button onClick={handleLogout} className="nav-logout" style={{ border: 'none', background: 'none', cursor: 'pointer', padding: '10px 16px', borderRadius: '8px', fontSize: '0.875em', fontWeight: 500 }}>
              <LogoutIcon />
            </button>
          </>
        ) : (
          <>
            <Link to="/login">Sign In</Link>
            <Link to="/register" className="nav-link-primary">Get Started</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;