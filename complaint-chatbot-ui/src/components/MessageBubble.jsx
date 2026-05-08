function formatTime(date) {
  return new Date(date).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

export default function MessageBubble({ message, showAvatar, isNewGroup }) {
  const isBot = message.sender === 'BOT';

  return (
    <div className={`msg-row ${isBot ? 'bot' : 'user'}${isNewGroup ? ' group-start' : ''}`}>
      {isBot && (
        <div className={`msg-avatar ${showAvatar ? 'visible' : 'hidden'}`}>B</div>
      )}
      <div className="msg-bubble-group">
        <div className="msg-bubble">{message.content}</div>
        <div className="msg-time">{formatTime(message.timestamp)}</div>
      </div>
    </div>
  );
}
