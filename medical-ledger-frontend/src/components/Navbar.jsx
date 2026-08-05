import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogoutIcon } from './Icons';

const Navbar = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">HealthChain</Link>
      <div className="navbar-links">
        {isAuthenticated ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/profile">Profile</Link>

            {user?.role === 'PATIENT' && (
              <>
                <Link to="/records">My Records</Link>
                <Link to="/consents">Consents</Link>
                <Link to="/doctors">Find Doctors</Link>
              </>
            )}

            {user?.role === 'DOCTOR' && (
              <Link to="/doctor">Doctor Panel</Link>
            )}

            {user?.role === 'ADMIN' && (
              <>
                <Link to="/admin">Admin</Link>
                <Link to="/audit">Audit Logs</Link>
              </>
            )}

            <Link to="/blockchain">Verify</Link>
            <button onClick={handleLogout} className="nav-logout">
              <LogoutIcon width="16" height="16" /> Sign Out
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