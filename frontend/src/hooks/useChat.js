import { useState, useCallback, useRef } from 'react';
import { fetchMessages } from '../services/conversationService';
import { sendMessage } from '../services/chatService';

export function useChat() {
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState(null);
  const activeConvRef = useRef(null);

  const loadMessages = useCallback(async (conversationId) => {
    if (!conversationId) {
      setMessages([]);
      return;
    }
    activeConvRef.current = conversationId;
    setLoading(true);
    setError(null);
    try {
      const res = await fetchMessages(conversationId);
      if (activeConvRef.current === conversationId) {
        setMessages(res.data ?? []);
      }
    } catch (err) {
      if (activeConvRef.current === conversationId) {
        setError(err.response?.data?.message || 'Failed to load messages');
      }
    } finally {
      if (activeConvRef.current === conversationId) {
        setLoading(false);
      }
    }
  }, []);

  const send = useCallback(async (conversationId, content) => {
    if (!conversationId || !content) return null;
    setSending(true);
    setError(null);

    const userMsg = {
      id: 'temp-' + Date.now(),
      conversationId,
      role: 'user',
      content,
      createdAt: new Date().toISOString(),
    };
    setMessages((prev) => [...prev, userMsg]);

    try {
      const res = await sendMessage(conversationId, content);
      const assistantMsg = res.data;
      setMessages((prev) => [...prev, assistantMsg]);
      return assistantMsg;
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to send message');
      return null;
    } finally {
      setSending(false);
    }
  }, []);

  return { messages, loading, sending, error, loadMessages, send };
}
