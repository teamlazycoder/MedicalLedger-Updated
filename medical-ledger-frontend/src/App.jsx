import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import ProtectedRoute from './components/ProtectedRoute';
import LoadingSpinner from './components/LoadingSpinner';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import PatientDashboard from './pages/PatientDashboard';
import DoctorDashboard from './pages/DoctorDashboard';
import AdminDashboard from './pages/AdminDashboard';
import PatientRecords from './pages/PatientRecords';
import PatientConsents from './pages/PatientConsents';
import Doctors from './pages/Doctors';
import AuditLogs from './pages/AuditLogs';
import BlockchainVerification from './pages/BlockchainVerification';
import Profile from './pages/Profile';
import NotFound from './pages/NotFound';

function App() {
  const { loading, isAuthenticated, user } = useAuth();

  if (loading) return <LoadingSpinner />;

  return (
    <div className="app">
      <Navbar />
      <main className="main-content">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={!isAuthenticated ? <Login /> : <Navigate to="/dashboard" />} />
          <Route path="/register" element={!isAuthenticated ? <Register /> : <Navigate to="/dashboard" />} />

          {/* Protected Routes */}
          <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
          <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />

          {/* Patient Routes */}
          <Route path="/records" element={<ProtectedRoute><PatientRecords /></ProtectedRoute>} />
          <Route path="/consents" element={<ProtectedRoute><PatientConsents /></ProtectedRoute>} />
          <Route path="/doctors" element={<ProtectedRoute><Doctors /></ProtectedRoute>} />

          {/* Admin Routes */}
          <Route path="/admin" element={<ProtectedRoute role="ADMIN"><AdminDashboard /></ProtectedRoute>} />
          <Route path="/audit" element={<ProtectedRoute><AuditLogs /></ProtectedRoute>} />

          {/* Blockchain */}
          <Route path="/blockchain" element={<ProtectedRoute><BlockchainVerification /></ProtectedRoute>} />

          {/* 404 */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;
