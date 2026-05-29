import axios from 'axios';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:7070',
  timeout: 90000,
  headers: { 'Content-Type': 'application/json' },
});

export default client;