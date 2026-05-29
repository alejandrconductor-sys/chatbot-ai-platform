import client from '../api/client';

export async function fetchConversations() {
  const res = await client.get('/api/conversations');
  return res.data;
}

export async function createConversation(title) {
  const res = await client.post('/api/conversations', { title });
  return res.data;
}

export async function fetchMessages(conversationId) {
  const res = await client.get(`/api/conversations/${conversationId}/messages`);
  return res.data;
}
