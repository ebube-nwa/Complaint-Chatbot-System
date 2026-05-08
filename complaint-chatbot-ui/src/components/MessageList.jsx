import { useEffect, useRef } from 'react';
import MessageBubble from './MessageBubble';
import TypingIndicator from './TypingIndicator';

export default function MessageList({ messages, isLoading }) {
  const endRef = useRef(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isLoading]);

  return (
    <div className="message-list">
      {messages.map((msg, i) => {
        const isBot = msg.sender === 'BOT';
        const nextMsg = messages[i + 1];
        const prevMsg = messages[i - 1];
        const showAvatar = isBot && (!nextMsg || nextMsg.sender !== 'BOT');
        const isNewGroup = i > 0 && prevMsg.sender !== msg.sender;
        return (
          <MessageBubble
            key={i}
            message={msg}
            showAvatar={showAvatar}
            isNewGroup={isNewGroup}
          />
        );
      })}
      {isLoading && <TypingIndicator />}
      <div ref={endRef} />
    </div>
  );
}
