export default function MessageBubble({ message }) {
  const isUser = message.role === 'user';

  return (
    <div className={`flex ${isUser ? 'justify-end' : 'justify-start'}`}>
      <div
        className={`max-w-[75%] px-4 py-2.5 rounded-2xl text-sm leading-relaxed whitespace-pre-wrap ${
          isUser
            ? 'bg-blue-600 text-white rounded-br-md'
            : 'bg-gray-800 text-gray-100 rounded-bl-md'
        }`}
      >
        {message.content}
      </div>
    </div>
  );
}
