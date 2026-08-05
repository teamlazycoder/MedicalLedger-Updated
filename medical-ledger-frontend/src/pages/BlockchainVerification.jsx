import { useState } from 'react';
import api from '../api/axios';
import toast from '../components/Toast';

const BlockchainVerification = () => {
  const [txId, setTxId] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleVerify = async () => {
    if (!txId.trim()) {
      toast.error('Please enter a transaction ID');
      return;
    }
    setLoading(true);
    try {
      const response = await api.get(`/blockchain/transaction/${txId}`);
      setResult(response.data?.data);
      toast.success('Transaction verified!');
    } catch (error) {
      toast.error('Transaction not found');
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <h1 style={{ fontSize: '1.8em', fontWeight: 800, marginBottom: 8 }}>⛓️ Blockchain Verification</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: 24 }}>
        Verify the authenticity of medical records on the blockchain
      </p>

      <div className="info-box">
        <span className="info-icon">💡</span>
        <div>
          <strong>How Blockchain Verification Works:</strong>
          Every medical record upload and access is recorded on the Hyperledger Fabric blockchain
          with a unique transaction ID. You can verify any transaction using its ID to ensure
          the record hasn't been tampered with.
        </div>
      </div>

      <div className="card">
        <div className="form-group">
          <label className="form-label">Blockchain Transaction ID</label>
          <input
            className="form-input"
            value={txId}
            onChange={(e) => setTxId(e.target.value)}
            placeholder="Enter transaction ID (e.g., tx_abc123...)"
          />
        </div>
        <button className="btn btn-primary w-full" onClick={handleVerify} disabled={loading}>
          {loading ? '⏳ Verifying...' : '🔍 Verify on Blockchain'}
        </button>
      </div>

      {result && (
        <div className="card">
          <h3 className="card-title">✅ Verification Result</h3>
          <div style={{ background: '#F8FAFC', padding: 20, borderRadius: 8 }}>
            <p><strong>Transaction ID:</strong> {result.txId || txId}</p>
            <p><strong>Function:</strong> {result.function || result.chaincodeFunction || 'N/A'}</p>
            <p><strong>Status:</strong> <span className="badge badge-success">{result.status || 'CONFIRMED'}</span></p>
            <p><strong>Timestamp:</strong> {result.timestamp || 'N/A'}</p>
            {result.payload && (
              <div style={{ marginTop: 12 }}>
                <strong>Payload:</strong>
                <pre style={{ background: '#1E293B', color: '#E2E8F0', padding: 12, borderRadius: 8, marginTop: 8, overflow: 'auto', fontSize: '0.8em' }}>
                  {JSON.stringify(result.payload, null, 2)}
                </pre>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default BlockchainVerification;