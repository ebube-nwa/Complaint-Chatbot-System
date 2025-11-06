import { useState, useEffect, useRef } from 'react';
import { chatAPI } from './api';
import './App.css';

function App() {
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [sessionId, setSessionId] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const [userId] = useState('user_' + Math.random().toString(36).substring(2, 9));
  const messagesEndRef = useRef(null);

  //Auto scroll to bottom on new message
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);




  //Start a new chat session
  const handleStartChat = async () => {
    setIsLoading(true);
    try {
      const response = await chatAPI.startChat(userId);
      setSessionId(response.sessionId);
      
      //Add welcome message from UI
      setMessages([
        { 
          sender: 'BOT', 
          content: response.message,
          timestamp: new Date().toLocaleTimeString()
        },
      ]);
    } catch (error) {
      console.error('Error starting chat:', error);
      alert('Failed to start chat. Make sure the backend is running on http://localhost:8080');
    } finally {
      setIsLoading(false);
    }
  };


  //Send message to bot
  const handleSendMessage = async () => {
    if (!inputMessage.trim()) return;

    const userMessage = {
      sender: 'USER',
      content: inputMessage,
      timestamp: new Date().toLocaleTimeString(),
    };

    //Update UI with user message
    setMessages((prev) => [...prev, userMessage]);
    setInputMessage('');
    setIsLoading(true);

    try {
      //Send message to backend and get bot response
      const response = await chatAPI.sendMessage(sessionId, inputMessage, userId);

      const botMessage = {
        sender: 'BOT',
        content: response.message,
        timestamp: new Date(response.timestamp).toLocaleTimeString(),
      };

      //Update UI with bot message
      setMessages((prev) => [...prev, botMessage]);
    } catch (error) {
      console.error('Error sending message:', error);

      //Add error message to UI
      const errorMessage = {
        sender: 'BOT',
        content: 'Sorry, I encountered an erroe. Please try again later.',
        timestamp: new Date().toLocaleTimeString(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  //Handle Enter key press
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };
  




  return (
    <div className="app">
      <div className="chat-container">
        <div className="chat-header">
          <h2>Customer Support Chat</h2>
          <span className="status">
            {sessionId ? '🟢 Connected' : '🔴 Not Connected'}
          </span>
        </div>

        <div className="chat-messages">
          {messages.length === 0 ? (
            <div className="welcome-message">
              <h3>👋 Welcome!</h3>
              <p>Click "Start Chat" to begin a conversation with our support bot.</p>
            </div>
          ) : (
            <>
              {messages.map((msg, index) => (
                <div key={index} className={`message ${msg.sender.toLowerCase()}`}>
                  <div className="message-content">
                    <strong>{msg.sender}:</strong> {msg.content}
                  </div>
                  <div className="message-time">{msg.timestamp}</div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </>
          )}
          {isLoading && (
            <div className="message bot">
              <div className="message-content typing">
                Bot is typing...
              </div>
            </div>
          )}
        </div>

        <div className="chat-input">
          {!sessionId ? (
            <button className="start-button" onClick={handleStartChat}>
              Start Chat
            </button>
          ) : (
            <>
              <input
                type="text"
                placeholder="Type your message..."
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                onKeyPress={handleKeyPress}
                disabled={isLoading}
              />
              <button 
                onClick={handleSendMessage}
                disabled={isLoading || !inputMessage.trim()}
              >
                Send
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;