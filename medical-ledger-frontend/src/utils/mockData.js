import api from '../api/axios';

/**
 * MOCK DATA INJECTOR - FIXED WITH AUTHENTICATION
 */

let authToken = null;

// Helper to make authenticated requests
const authRequest = async (method, url, data = null, isFormData = false) => {
  const headers = {
    'Content-Type': isFormData ? 'multipart/form-data' : 'application/json'
  };

  if (authToken) {
    headers['Authorization'] = `Bearer ${authToken}`;
  }

  const config = { headers };

  switch (method) {
    case 'POST': return api.post(url, data, config);
    case 'GET': return api.get(url, config);
    case 'PUT': return api.put(url, data, config);
    default: return api.get(url, config);
  }
};

// Login as admin to get full access token
const loginAsAdmin = async () => {
  try {
    const response = await api.post('/auth/login', {
      email: 'admin@healthchain.com',
      password: 'Admin@123'
    });
    if (response.data?.success) {
      authToken = response.data.data.token;
      localStorage.setItem('token', authToken);
      console.log('🔑 Logged in as Admin - Full access granted');
      return true;
    }
  } catch (error) {
    // Admin might not exist yet, register first
    try {
      const regResponse = await api.post('/auth/register', {
        username: 'adminuser',
        email: 'admin@healthchain.com',
        password: 'Admin@123',
        role: 'ADMIN',
        firstName: 'Admin',
        lastName: 'System'
      });
      if (regResponse.data?.success) {
        authToken = regResponse.data.data.token;
        localStorage.setItem('token', authToken);
        console.log('🔑 Admin registered & logged in');
        return true;
      }
    } catch (regError) {
      console.log('Admin already exists, trying login again...');
      const loginRes = await api.post('/auth/login', {
        email: 'admin@healthchain.com',
        password: 'Admin@123'
      });
      if (loginRes.data?.success) {
        authToken = loginRes.data.data.token;
        localStorage.setItem('token', authToken);
        return true;
      }
    }
  }
  return false;
};

export const injectMockData = async () => {
  console.log('🚀 Starting Mock Data Injection...\n');

  // Step 0: Authenticate as Admin
  const loggedIn = await loginAsAdmin();
  if (!loggedIn) {
    console.error('❌ Failed to authenticate. Cannot inject mock data.');
    return;
  }

  try {
    // Register Users
    console.log('📝 Registering Users...');
    const users = [
      { username: 'johnpatient', email: 'john.patient@email.com', password: 'Patient@123', role: 'PATIENT', firstName: 'John', lastName: 'Smith', dateOfBirth: '1990-05-15', gender: 'MALE', bloodType: 'O+', contactNumber: '+1-555-0101' },
      { username: 'sarahpatient', email: 'sarah.patient@email.com', password: 'Patient@123', role: 'PATIENT', firstName: 'Sarah', lastName: 'Johnson', dateOfBirth: '1985-08-22', gender: 'FEMALE', bloodType: 'A+', contactNumber: '+1-555-0102' },
      { username: 'drcardio', email: 'dr.cardio@hospital.com', password: 'Doctor@123', role: 'DOCTOR', firstName: 'Robert', lastName: 'Williams', specialty: 'Cardiology', licenseNumber: 'MED-001', hospitalAffiliation: 'Heart Care Center', yearsOfExperience: 15 },
      { username: 'drneuro', email: 'dr.neuro@hospital.com', password: 'Doctor@123', role: 'DOCTOR', firstName: 'Emily', lastName: 'Davis', specialty: 'Neurology', licenseNumber: 'MED-002', hospitalAffiliation: 'Brain Institute', yearsOfExperience: 12 },
      { username: 'drpedia', email: 'dr.pedia@hospital.com', password: 'Doctor@123', role: 'DOCTOR', firstName: 'Michael', lastName: 'Brown', specialty: 'Pediatrics', licenseNumber: 'MED-003', hospitalAffiliation: 'Children Hospital', yearsOfExperience: 8 },
    ];

    for (const user of users) {
      try {
        await api.post('/auth/register', user);
        console.log(`  ✅ ${user.email} (${user.role})`);
      } catch (e) {
        console.log(`  🔄 ${user.email} - already exists`);
      }
      await new Promise(r => setTimeout(r, 300)); // Small delay
    }

    // Login as patient to create records
    console.log('\n📄 Creating Medical Records...');

    // Login as john patient
    const johnLogin = await api.post('/auth/login', { email: 'john.patient@email.com', password: 'Patient@123' });
    const johnToken = johnLogin.data?.data?.token;
    const johnId = johnLogin.data?.data?.user?.id;

    if (johnToken && johnId) {
      localStorage.setItem('token', johnToken);

      const records = [
        { type: 'LAB_RESULT', desc: 'Complete Blood Count - Normal', diag: 'Normal', treat: 'None', file: 'cbc_report.pdf' },
        { type: 'IMAGING', desc: 'Chest X-Ray PA View', diag: 'Clear lungs', treat: 'Annual follow-up', file: 'chest_xray.pdf' },
        { type: 'PRESCRIPTION', desc: 'Allergy medication', diag: 'Allergic Rhinitis', treat: 'Cetirizine 10mg', file: 'prescription.pdf' },
      ];

      for (const rec of records) {
        try {
          const fileContent = `Medical Record\nType: ${rec.type}\nDescription: ${rec.desc}\nDiagnosis: ${rec.diag}\nTreatment: ${rec.treat}`;
          const file = new File([fileContent], rec.file, { type: 'text/plain' });

          const formData = new FormData();
          formData.append('file', file);
          formData.append('patientId', johnId.toString());
          formData.append('recordType', rec.type);
          formData.append('description', rec.desc);
          formData.append('diagnosis', rec.diag);
          formData.append('treatment', rec.treat);

          const res = await api.post('/records/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
          });
          console.log(`  ✅ ${rec.file} uploaded`);
        } catch (e) {
          console.log(`  ❌ ${rec.file} - ${e.response?.data?.message || e.message}`);
        }
        await new Promise(r => setTimeout(r, 300));
      }
    }

    // Login as sarah patient
    const sarahLogin = await api.post('/auth/login', { email: 'sarah.patient@email.com', password: 'Patient@123' });
    const sarahToken = sarahLogin.data?.data?.token;
    const sarahId = sarahLogin.data?.data?.user?.id;

    if (sarahToken && sarahId) {
      localStorage.setItem('token', sarahToken);

      const records = [
        { type: 'LAB_RESULT', desc: 'Lipid Profile', diag: 'Elevated LDL', treat: 'Diet & exercise', file: 'lipid_profile.pdf' },
        { type: 'IMAGING', desc: 'MRI Brain', diag: 'Normal', treat: 'Follow-up in 6 months', file: 'mri_brain.pdf' },
        { type: 'SURGERY_REPORT', desc: 'Cholecystectomy', diag: 'Cholecystitis', treat: 'Gallbladder removed', file: 'surgery.pdf' },
      ];

      for (const rec of records) {
        try {
          const fileContent = `Medical Record\nType: ${rec.type}\nDescription: ${rec.desc}\nDiagnosis: ${rec.diag}\nTreatment: ${rec.treat}`;
          const file = new File([fileContent], rec.file, { type: 'text/plain' });

          const formData = new FormData();
          formData.append('file', file);
          formData.append('patientId', sarahId.toString());
          formData.append('recordType', rec.type);
          formData.append('description', rec.desc);
          formData.append('diagnosis', rec.diag);
          formData.append('treatment', rec.treat);

          const res = await api.post('/records/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
          });
          console.log(`  ✅ ${rec.file} uploaded`);
        } catch (e) {
          console.log(`  ❌ ${rec.file} - ${e.response?.data?.message || e.message}`);
        }
        await new Promise(r => setTimeout(r, 300));
      }
    }

    // Get doctor IDs
    console.log('\n🔒 Setting Up Consents...');
    const drCardioLogin = await api.post('/auth/login', { email: 'dr.cardio@hospital.com', password: 'Doctor@123' });
    const drCardioId = drCardioLogin.data?.data?.user?.id;

    const drNeuroLogin = await api.post('/auth/login', { email: 'dr.neuro@hospital.com', password: 'Doctor@123' });
    const drNeuroId = drNeuroLogin.data?.data?.user?.id;

    const drPediaLogin = await api.post('/auth/login', { email: 'dr.pedia@hospital.com', password: 'Doctor@123' });
    const drPediaId = drPediaLogin.data?.data?.user?.id;

    // Grant consents (login as each patient)
    // John grants to Cardio and Neuro
    localStorage.setItem('token', johnToken);
    const consents = [
      { patientId: johnId, doctorId: drCardioId, purpose: 'Annual cardiac evaluation' },
      { patientId: johnId, doctorId: drNeuroId, purpose: 'Neurological consultation' },
    ];

    for (const c of consents) {
      try {
        const now = new Date();
        const future = new Date(now.getFullYear() + 1, now.getMonth(), now.getDate());
        await api.post(`/patients/${c.patientId}/consents/grant`, {
          doctorId: c.doctorId,
          recordType: 'ALL',
          startDate: now.toISOString(),
          endDate: future.toISOString(),
          purpose: c.purpose
        });
        console.log(`  ✅ Consent granted: Patient ${c.patientId} → Doctor ${c.doctorId}`);
      } catch (e) {
        console.log(`  ❌ Consent failed: ${e.response?.data?.message || e.message}`);
      }
      await new Promise(r => setTimeout(r, 300));
    }

    // Sarah grants to Cardio and Pedia
    localStorage.setItem('token', sarahToken);
    const sarahConsents = [
      { patientId: sarahId, doctorId: drCardioId, purpose: 'Cardiac risk assessment' },
      { patientId: sarahId, doctorId: drPediaId, purpose: 'Post-surgery follow-up' },
    ];

    for (const c of sarahConsents) {
      try {
        const now = new Date();
        const future = new Date(now.getFullYear() + 1, now.getMonth(), now.getDate());
        await api.post(`/patients/${c.patientId}/consents/grant`, {
          doctorId: c.doctorId,
          recordType: 'ALL',
          startDate: now.toISOString(),
          endDate: future.toISOString(),
          purpose: c.purpose
        });
        console.log(`  ✅ Consent granted: Patient ${c.patientId} → Doctor ${c.doctorId}`);
      } catch (e) {
        console.log(`  ❌ Consent failed: ${e.response?.data?.message || e.message}`);
      }
      await new Promise(r => setTimeout(r, 300));
    }

    // Restore admin token
    localStorage.setItem('token', authToken);

    console.log('\n✅ MOCK DATA INJECTION COMPLETE!\n');
    console.log('📋 TEST CREDENTIALS:');
    console.log('👤 Patients: john.patient@email.com / Patient@123');
    console.log('            sarah.patient@email.com / Patient@123');
    console.log('👨‍⚕️ Doctors: dr.cardio@hospital.com / Doctor@123');
    console.log('             dr.neuro@hospital.com / Doctor@123');
    console.log('             dr.pedia@hospital.com / Doctor@123');
    console.log('⚙️ Admin:   admin@healthchain.com / Admin@123\n');

  } catch (error) {
    console.error('❌ Error:', error.message);
  }
};

export default injectMockData;