export default function ChatHeader({ connected }) {
  return (
    <div className="chat-header">
      <div className="header-bot-info">
        <div className="bot-avatar-sm">💬</div>
        <div>
          <span className="header-title">Support Assistant</span>
          <span className="header-subtitle">
            <span className={`status-dot ${connected ? 'online' : 'offline'}`} />
            {connected ? 'Online' : 'Start a chat to connect'}
          </span>
        </div>
      </div>
    </div>
  );
}
