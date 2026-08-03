import { useAuth } from '../context/AuthContext';
import PatientDashboard from './PatientDashboard';
import DoctorDashboard from './DoctorDashboard';
import AdminDashboard from './AdminDashboard';

const Dashboard = () => {
  const { user } = useAuth();

  if (user?.role === 'PATIENT') return <PatientDashboard />;
  if (user?.role === 'DOCTOR') return <DoctorDashboard />;
  if (user?.role === 'ADMIN') return <AdminDashboard />;

  return (
    <div className="card">
      <h2>Welcome to your Dashboard</h2>
      <p>Role: {user?.role || 'Unknown'}</p>
    </div>
  );
};

export default Dashboard;
