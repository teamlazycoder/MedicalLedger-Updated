 
import { useState } from 'react';
import api from '../api/axios';
import toast from 'react-hot-toast';

const BlockchainVerification = () => {
  const [txId, setTxId] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const verifyTransaction = async () => {
    if (!txId.trim()) {
      toast.error('Enter a transaction ID');
      return;
    }
    setLoading(true);
    try {
      const response = await api.get(`/blockchain/transaction/${txId}`);
      setResult(response.data.data);
      toast.success('Transaction found!');
    } catch (error) {
      toast.error('Transaction not found');
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1 style={{ marginBottom: '30px' }}>⛓️ Blockchain Verification</h1>
      <div className="card mb-20">
        <h3>Verify Transaction</h3>
        <div className="form-group">
          <label>Transaction ID</label>
          <input value={txId} onChange={(e) => setTxId(e.target.value)} placeholder="Enter blockchain transaction ID" />
        </div>
        <button className="btn" onClick={verifyTransaction} disabled={loading}>
          {loading ? 'Verifying...' : 'Verify'}
        </button>
      </div>
      {result && (
        <div className="card">
          <h3>Result</h3>
          <pre style={{ background: '#f8f9fa', padding: '20px', borderRadius: '8px', overflow: 'auto' }}>
            {JSON.stringify(result, null, 2)}
          </pre>
        </div>
      )}
    </div>
  );
};

export default BlockchainVerification;