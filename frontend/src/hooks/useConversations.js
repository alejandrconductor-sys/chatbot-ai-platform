import { useState, useEffect, useCallback } from 'react';
import { fetchConversations, createConversation } from '../services/conversationService';

export function useConversations() {
  const [conversations, setConversations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetchConversations();
      setConversations(res.data ?? []);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load conversations');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const create = useCallback(async (title) => {
    setError(null);
    try {
      const res = await createConversation(title);
      const created = res.data;
      setConversations((prev) => [created, ...prev]);
      return created;
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create conversation');
      return null;
    }
  }, []);

  return { conversations, loading, error, create, reload: load };
}
