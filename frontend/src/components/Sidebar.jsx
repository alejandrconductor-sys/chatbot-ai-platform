import { useState } from 'react';

export default function Sidebar({
  conversations,
  selectedId,
  onSelect,
  onCreate,
  loading,
  sidebarOpen,
  setSidebarOpen
}) {
const [title, setTitle] = useState('');

  const handleCreate = async (e) => {
    e.preventDefault();
    const trimmed = title.trim();
    if (!trimmed) return;
    const created = await onCreate(trimmed);
    if (created) {
      setTitle('');
      onSelect(created.id);
    }
  };

  return (
    <aside className={`
      fixed top-0 left-0 z-40 h-full w-72 bg-gray-900 border-r border-gray-800
      transform transition-transform duration-300
      ${sidebarOpen ? "translate-x-0" : "-translate-x-full"}
      md:translate-x-0
    `}>
    <button
      onClick={() => setSidebarOpen(false)}
      className="md:hidden absolute top-4 right-4 text-gray-400"
    >
      ✕
    </button>
     <h1 className="text-lg font-semibold p-4 border-b border-gray-800">
        Chatbot AI
     </h1>
      <form onSubmit={handleCreate} className="p-4 border-b border-gray-700 flex gap-2">
        <input
          className="flex-1 px-3 py-2 rounded bg-gray-800 border border-gray-600 text-sm focus:outline-none focus:border-blue-500"
          placeholder="New conversation..."
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <button
          type="submit"
          className="px-3 py-2 rounded bg-blue-600 hover:bg-blue-700 text-sm font-medium"
        >
          +
        </button>
      </form>

      <nav className="flex-1 overflow-y-auto p-2 space-y-1">
        {loading && conversations.length === 0 && (
          <p className="text-gray-500 text-sm text-center py-4">Loading...</p>
        )}
        {!loading && conversations.length === 0 && (
          <p className="text-gray-500 text-sm text-center py-4">No conversations</p>
        )}
        {conversations.map((conv) => (
          <button
            key={conv.id}
            onClick={() => onSelect(conv.id)}
            className={`w-full text-left px-3 py-2 rounded text-sm truncate transition-colors ${
              selectedId === conv.id
                ? 'bg-blue-600 text-white'
                : 'hover:bg-gray-800 text-gray-300'
            }`}
          >
            {conv.title}
          </button>
        ))}
      </nav>
      <div className="p-3 text-xs text-gray-500 border-t border-gray-800 text-center space-y-1">
        <div className="text-gray-300 font-semibold tracking-wide">
          Chatbot AI
        </div>
        <div className="text-gray-500">
          Java • React • MariaDB
        </div>
        <div className="text-gray-600">
          Built by Rafael Marquez
        </div>
      </div>
    </aside>
  );
}
