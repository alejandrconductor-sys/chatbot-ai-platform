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
      fixed md:static flex-shrink-0 top-0 left-0 z-40 h-full w-72 bg-gray-900 border-r border-gray-800
      flex flex-col
      transform transition-transform duration-300 ease-in-out
      ${sidebarOpen ? "translate-x-0" : "-translate-x-full"}
      md:translate-x-0
    `}>

      {/* HEADER + CLOSE BUTTON */}
      <div className="p-4 border-b border-gray-800 relative">
        <button
          onClick={() => setSidebarOpen(false)}
          className="md:hidden absolute top-3 right-3 text-gray-400"
        >
          ✕
        </button>

        <h1 className="text-lg font-semibold">
          Chatbot AI
        </h1>
      </div>

      {/* CONTENT SCROLLABLE */}
      <div className="flex-1 overflow-y-auto p-2 space-y-2">

        <form onSubmit={handleCreate} className="p-2 flex gap-2">
          <input
            className="flex-1 px-3 py-2 rounded bg-gray-800 border border-gray-600 text-sm"
            placeholder="Haz tu propia conversación..."
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
          <button
            type="submit"
            className="px-3 py-2 rounded bg-blue-600 text-sm"
          >
            +
          </button>
        </form>

        <nav className="space-y-1">
          {loading && conversations.length === 0 && (
            <p className="text-gray-500 text-sm text-center py-4">
              Loading...
            </p>
          )}

          {!loading && conversations.length === 0 && (
            <p className="text-gray-500 text-sm text-center py-4">
              No conversations
            </p>
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

      </div>

      {/* MEMORY NOTICE */}
      <div className="mx-3 mb-3 p-4 rounded-xl border border-yellow-400 bg-yellow-500/20 text-yellow-100 text-sm font-semibold leading-relaxed shadow-xl">
        💡 Prueba la memoria de la IA: entra en chats anteriores y comprueba cómo recuerda el contexto de cada conversación.
      </div>

      {/* FOOTER FIJO */}
      <div className="p-3 text-xs text-gray-500 border-t border-gray-800 text-center space-y-1">
        <div className="text-gray-300 font-semibold">
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
