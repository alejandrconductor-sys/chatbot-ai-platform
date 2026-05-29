import { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import ChatArea from '../components/ChatArea';
import { useConversations } from '../hooks/useConversations';
import { useChat } from '../hooks/useChat';

export default function ChatPage() {
  const { conversations, loading: convsLoading, create, reload: reloadConvs } = useConversations();
  const { messages, loading: msgsLoading, sending, error, loadMessages, send } = useChat();
  const [selectedId, setSelectedId] = useState(null);

  useEffect(() => {
    if (selectedId) {
      loadMessages(selectedId);
    }
  }, [selectedId, loadMessages]);

  const handleSelect = (id) => {
    setSelectedId(id);
  };

  return (
    <div className="flex h-dvh w-dvw bg-gray-950 text-gray-100">
      <Sidebar
        conversations={conversations}
        selectedId={selectedId}
        onSelect={handleSelect}
        onCreate={create}
        loading={convsLoading}
      />
      <ChatArea
        messages={messages}
        loading={msgsLoading}
        sending={sending}
        error={error}
        selectedId={selectedId}
        onSend={send}
      />
    </div>
  );
}
