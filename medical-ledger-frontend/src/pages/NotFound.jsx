import { Link } from 'react-router-dom';

const NotFound = () => {
  return (
    <div className="text-center" style={{ padding: '80px 20px' }}>
      <h1 style={{ fontSize: '6em', color: '#667eea' }}>404</h1>
      <h2>Page Not Found</h2>
      <p style={{ color: '#666', marginBottom: '30px' }}>The page you're looking for doesn't exist.</p>
      <Link to="/" className="btn">Go Home</Link>
    </div>
  );
};

export default NotFound;
