const API_URL =
  import.meta.env.VITE_API_URL ||
  (import.meta.env.DEV
    ? "http://localhost:7070"
    : "https://chatbot-ai-platform.onrender.com");

export default API_URL;