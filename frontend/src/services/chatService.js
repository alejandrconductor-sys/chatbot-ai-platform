import client from '../api/client';

export async function sendMessage(conversationId, content) {
  const res = await client.post('/api/chat', { conversationId, content });
  return res.data;
}
