import { useState, useEffect, useRef } from 'react';
import MessageBubble from './MessageBubble';
import { motion } from "framer-motion";

export default function ChatArea({
  messages,
  loading,
  sending,
  error,
  selectedId,
  onSend,
  setSidebarOpen 
}) {
  const [input, setInput] = useState('');
  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const trimmed = input.trim();
    if (!trimmed || sending || !selectedId) return;
    setInput('');
    onSend(selectedId, trimmed);
  };

  if (!selectedId) {
    return (
      <main className="flex-1 flex items-center justify-center bg-gray-950 text-gray-500">
        <p className="text-lg">Select or create a conversation</p>
      </main>
    );
  }


  return (
    <main className="flex-1 flex flex-col bg-gray-950 h-full w-full relative">
      <button
        onClick={() => setSidebarOpen(true)}
        className="md:hidden fixed top-4 left-4 z-50 bg-gray-800 p-2 rounded shadow-lg"
      >
        ☰
      </button>
      <div className="flex-1 overflow-y-auto p-4 space-y-4 pb-28">
        {loading && (
          <p className="text-gray-500 text-sm text-center">Loading messages...</p>
        )}
        {!loading && messages.length === 0 && (
          <p className="text-gray-500 text-sm text-center">No messages yet</p>
        )}
        {messages.map((msg) => (
          msg.role === 'user' ? (
            <div key={msg.id} className="flex justify-end">
              <div className="bg-blue-600 px-4 py-2 rounded-2xl max-w-[75%]">
                {msg.content}
              </div>
            </div>
          ) : (
            <div key={msg.id} className="flex justify-start">
              <div className="bg-gray-800 px-4 py-2 rounded-2xl max-w-[75%]">
                {msg.content}
              </div>
            </div>
          )
        ))}
        <div ref={bottomRef} />
      </div>

      {error && (
        <div className="px-4 py-2 bg-red-900/50 text-red-300 text-sm text-center border-t border-red-800">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="p-4 border-t border-gray-800 flex gap-3">
        <input
          className="flex-1 px-4 py-2.5 rounded-xl bg-gray-800 border border-gray-700 text-gray-100 text-sm focus:outline-none focus:border-blue-500 placeholder-gray-500"
          placeholder="Type a message..."
          value={input}
          onChange={(e) => setInput(e.target.value)}
          disabled={sending}
        />
        <button
          type="submit"
          disabled={sending || !input.trim()}
          className="px-5 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed text-sm font-medium transition-colors"
        >
          Send
        </button>
      </form>
    </main>
  );
}
