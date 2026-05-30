import { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import ChatArea from '../components/ChatArea';
import { useConversations } from '../hooks/useConversations';
import { useChat } from '../hooks/useChat';

export default function ChatPage() {
  const { conversations, loading: convsLoading, create } = useConversations();
  const { messages, loading: msgsLoading, sending, error, loadMessages, send } = useChat();

  const [selectedId, setSelectedId] = useState(null);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  useEffect(() => {
    if (selectedId) loadMessages(selectedId);
  }, [selectedId, loadMessages]);

  const handleSelect = (id) => {
    setSelectedId(id);
    setSidebarOpen(false); // 👈 cerrar en móvil
  };

  return (
    <div className="flex h-screen w-screen bg-gray-950 text-gray-100 overflow-hidden">

      <Sidebar
        conversations={conversations}
        selectedId={selectedId}
        onSelect={handleSelect}
        onCreate={create}
        loading={convsLoading}
        sidebarOpen={sidebarOpen}
        setSidebarOpen={setSidebarOpen}
      />

      <ChatArea
        messages={messages}
        loading={msgsLoading}
        sending={sending}
        error={error}
        selectedId={selectedId}
        onSend={send}
        setSidebarOpen={setSidebarOpen}
      />
    </div>
  );
}