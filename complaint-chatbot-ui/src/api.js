import axios from "axios";

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const chatAPI = {
    //Start a new chat session
    startChat: async (userId) => {
        const response = await api.post('/chat/start', { userId });
        return response.data;
    },

    //Send a message in an existing chat session
    sendMessage: async (sessionId, message, userId) => {
        const response = await api.post('/chat/message', { 
            sessionId, 
            message, 
            userId,
        })
        return response.data;
    },

    //Get Conversation for a session
    getHistory: async (sessionId) => {
        const response = await api.get(`/chat/history/${sessionId}`);
        return response.data;
    },
};

export default api;