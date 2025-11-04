import { useState } from 'react';
import './App.css';

function App() {
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [sessionId, setSessionId] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

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
            messages.map((msg, index) => (
              <div key={index} className={`message ${msg.sender.toLowerCase()}`}>
                <div className="message-content">
                  <strong>{msg.sender}:</strong> {msg.content}
                </div>
                <div className="message-time">{msg.timestamp}</div>
              </div>
            ))
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
            <button className="start-button">
              Start Chat
            </button>
          ) : (
            <>
              <input
                type="text"
                placeholder="Type your message..."
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                disabled={isLoading}
              />
              <button disabled={isLoading || !inputMessage.trim()}>
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