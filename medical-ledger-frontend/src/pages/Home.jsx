import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ShieldIcon, LockIcon, ActivityIcon, HeartIcon, ArrowRightIcon } from '../components/Icons';

const Home = () => {
  const { isAuthenticated } = useAuth();

  return (
    <div>
      {/* HERO */}
      <section className="hero">
        <div className="hero-badge">
          <span className="live-dot"></span>
          HIPAA Compliant • Blockchain Secured
        </div>
        <h1>
          Your Medical Records,{" "}
          <span className="gradient-text">Your Rules</span>
        </h1>
        <p>
          A decentralized healthcare platform that puts you in complete control.
          Grant time-limited access to doctors, track every interaction, and rest
          easy knowing your data is protected by military-grade encryption.
        </p>
        <div className="hero-buttons">
          {isAuthenticated ? (
            <Link to="/dashboard" className="btn btn-primary">
              Go to Dashboard <ArrowRightIcon />
            </Link>
          ) : (
            <>
              <Link to="/register" className="btn btn-primary">
                Start Free Trial <ArrowRightIcon />
              </Link>
              <Link to="/login" className="btn btn-white">Sign In</Link>
            </>
          )}
        </div>
      </section>

      {/* STATS */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon"><LockIcon /></div>
          <div className="stat-value">AES-256</div>
          <div className="stat-label">Military-grade encryption protecting every single record</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon"><ShieldIcon /></div>
          <div className="stat-value">Blockchain</div>
          <div className="stat-label">Immutable audit trail on Hyperledger Fabric network</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon"><ActivityIcon /></div>
          <div className="stat-value">Real-Time</div>
          <div className="stat-label">Instant access logging and notification system</div>
        </div>
      </div>

      {/* FEATURES */}
      <h2 className="section-title">Why HealthChain?</h2>
      <p className="section-subtitle">Built for patients, trusted by doctors, secured by blockchain</p>
      <div className="features-grid">
        <div className="feature-card">
          <div className="feature-icon"><LockIcon /></div>
          <h3>Complete Data Privacy</h3>
          <p>End-to-end AES-256-GCM encryption ensures only you can access your medical records. Not even administrators can view your data without consent.</p>
        </div>
        <div className="feature-card">
          <div className="feature-icon"><ShieldIcon /></div>
          <h3>Granular Access Control</h3>
          <p>Grant access to specific records for specific doctors with time-bound permissions. Revoke instantly with a single click, anytime, anywhere.</p>
        </div>
        <div className="feature-card">
          <div className="feature-icon"><ActivityIcon /></div>
          <h3>Immutable Audit Trail</h3>
          <p>Every access to your records is permanently logged on the blockchain. Complete transparency of who viewed what and when. Tamper-proof by design.</p>
        </div>
        <div className="feature-card">
          <div className="feature-icon"><HeartIcon /></div>
          <h3>Emergency Access Protocol</h3>
          <p>Break-glass emergency access for life-threatening situations. Full justification logging ensures accountability even in critical moments.</p>
        </div>
        <div className="feature-card">
          <div className="feature-icon">🏥</div>
          <h3>Universal Interoperability</h3>
          <p>FHIR and HL7 compliant architecture enables seamless record sharing across hospitals, clinics, and healthcare networks worldwide.</p>
        </div>
        <div className="feature-card">
          <div className="feature-icon">✅</div>
          <h3>Regulatory Compliance</h3>
          <p>Built from the ground up for HIPAA, GDPR, and global healthcare regulations. Automated compliance reporting saves weeks of manual work.</p>
        </div>
      </div>

      {/* MEDICAL IMAGE + TEXT */}
      <div className="image-section">
        <img
          src="https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800&q=80"
          alt="Modern healthcare technology"
          loading="lazy"
        />
        <div>
          <h3>Modern Healthcare Meets Blockchain Technology</h3>
          <p>
            HealthChain bridges the gap between traditional healthcare systems and
            cutting-edge blockchain technology. Our platform ensures your medical
            records are not just secure, but also instantly accessible when you
            need them most.
          </p>
          <p>
            With decentralized storage on IPFS and immutable audit trails on
            Hyperledger Fabric, your health data is protected by the same
            technology that secures billion-dollar financial systems.
          </p>
          {!isAuthenticated && (
            <Link to="/register" className="btn btn-primary mt-20">
              Get Started Now <ArrowRightIcon />
            </Link>
          )}
        </div>
      </div>

      {/* SECOND IMAGE SECTION */}
      <div className="image-section" style={{ direction: 'rtl' }}>
        <img
          src="https://images.unsplash.com/photo-1551076805-e1869033e561?w=800&q=80"
          alt="Doctor consulting with patient"
          loading="lazy"
        />
        <div style={{ direction: 'ltr' }}>
          <h3>Empowering the Doctor-Patient Relationship</h3>
          <p>
            Give your doctors complete context with comprehensive medical history
            at their fingertips. Reduce misdiagnosis, eliminate redundant tests,
            and improve treatment outcomes with complete, accurate patient data.
          </p>
          <p>
            Our granular consent system ensures doctors only see what they need to
            see, when they need to see it. Your privacy, your rules.
          </p>
        </div>
      </div>

      {/* CTA */}
      <div className="cta-section">
        <h2>Ready to Take Control of Your Health Data?</h2>
        <p>Join thousands of patients who trust HealthChain with their medical records.</p>
        {!isAuthenticated && (
          <Link to="/register" className="btn btn-primary">
            Create Free Account <ArrowRightIcon />
          </Link>
        )}
      </div>
    </div>
  );
};

export default Home;