 
import { useAuth } from '../context/AuthContext';

const Profile = () => {
  const { user } = useAuth();

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>
      <h1 style={{ marginBottom: '30px' }}>👤 My Profile</h1>
      <div className="card">
        <div className="grid grid-2">
          <div>
            <p><strong>Username:</strong> {user?.username}</p>
            <p><strong>Email:</strong> {user?.email}</p>
            <p><strong>Role:</strong> {user?.role}</p>
          </div>
          <div>
            <p><strong>Status:</strong> <span className={`badge ${user?.isActive ? 'badge-success' : 'badge-danger'}`}>{user?.isActive ? 'Active' : 'Inactive'}</span></p>
            <p><strong>Member since:</strong> {user?.createdAt ? new Date(user?.createdAt).toLocaleDateString() : 'N/A'}</p>
            <p><strong>Last login:</strong> {user?.lastLogin ? new Date(user?.lastLogin).toLocaleString() : 'N/A'}</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;