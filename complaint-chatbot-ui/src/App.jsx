import { useState } from 'react';
import { chatAPI } from './api';
import ChatHeader from './components/ChatHeader';
import MessageList from './components/MessageList';
import ChatInput from './components/ChatInput';
import WelcomeScreen from './components/WelcomeScreen';
import './App.css';

function App() {
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [sessionId, setSessionId] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [startError, setStartError] = useState(null);
  const [userId] = useState(() => {
    const stored = localStorage.getItem('chatbot_user_id');
    if (stored) return stored;
    const id = 'user_' + Math.random().toString(36).substring(2, 9);
    localStorage.setItem('chatbot_user_id', id);
    return id;
  });

  const handleStartChat = async (quickReplyText = null) => {
    setIsLoading(true);
    setStartError(null);
    try {
      const response = await chatAPI.startChat(userId);
      const newSessionId = response.sessionId;
      const welcomeMsg = { sender: 'BOT', content: response.message, timestamp: new Date() };

      if (quickReplyText) {
        const userMsg = { sender: 'USER', content: quickReplyText, timestamp: new Date() };
        setMessages([welcomeMsg, userMsg]);
        setSessionId(newSessionId);

        const botResponse = await chatAPI.sendMessage(newSessionId, quickReplyText, userId);
        setMessages(prev => [...prev, {
          sender: 'BOT',
          content: botResponse.message,
          timestamp: botResponse.timestamp ? new Date(botResponse.timestamp) : new Date(),
        }]);
      } else {
        setMessages([welcomeMsg]);
        setSessionId(newSessionId);
      }
    } catch (error) {
      console.error('Error starting chat:', error);
      const msg = error?.response?.data?.message
        || (error?.message === 'Network Error' ? 'Cannot reach the server. Check your connection or API URL.' : null)
        || 'Failed to start chat. Please try again.';
      setStartError(msg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSendMessage = async () => {
    if (!inputMessage.trim() || isLoading) return;
    const text = inputMessage.trim();
    setInputMessage('');

    setMessages(prev => [...prev, { sender: 'USER', content: text, timestamp: new Date() }]);
    setIsLoading(true);

    try {
      const response = await chatAPI.sendMessage(sessionId, text, userId);
      setMessages(prev => [...prev, {
        sender: 'BOT',
        content: response.message,
        timestamp: response.timestamp ? new Date(response.timestamp) : new Date(),
      }]);
    } catch {
      setMessages(prev => [...prev, {
        sender: 'BOT',
        content: 'Sorry, I encountered an error. Please try again.',
        timestamp: new Date(),
      }]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="app">
      <div className="chat-window">
        <ChatHeader connected={!!sessionId} />
        {!sessionId ? (
          <>
            <WelcomeScreen
              onStart={handleStartChat}
              isLoading={isLoading}
            />
            {startError && (
              <p style={{ color: 'red', textAlign: 'center', padding: '0 1rem 1rem', fontSize: '0.875rem' }}>
                {startError}
              </p>
            )}
          </>
        ) : (
          <>
            <MessageList messages={messages} isLoading={isLoading} />
            <ChatInput
              value={inputMessage}
              onChange={setInputMessage}
              onSend={handleSendMessage}
              disabled={isLoading}
            />
          </>
        )}
      </div>
    </div>
  );
}

export default App;
