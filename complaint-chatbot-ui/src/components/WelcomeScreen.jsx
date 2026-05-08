const QUICK_REPLIES = [
  { emoji: '📦', label: 'Delivery Issue' },
  { emoji: '💳', label: 'Billing Problem' },
  { emoji: '🔧', label: 'Technical Support' },
  { emoji: '🛍️', label: 'Product Complaint' },
  { emoji: '📋', label: 'General Inquiry' },
];

export default function WelcomeScreen({ onStart, isLoading }) {
  return (
    <div className="welcome-screen">
      <div className="welcome-avatar">💬</div>
      <h2>How can we help?</h2>
      <p>Choose a topic below or start a free-form conversation.</p>

      <div className="quick-reply-grid">
        {QUICK_REPLIES.map(({ emoji, label }) => (
          <button
            key={label}
            className="quick-reply-chip"
            onClick={() => onStart(label)}
            disabled={isLoading}
          >
            <span className="chip-emoji">{emoji}</span>
            {label}
          </button>
        ))}
      </div>

      <button className="start-btn" onClick={() => onStart(null)} disabled={isLoading}>
        {isLoading ? 'Connecting...' : 'Start a conversation'}
      </button>
    </div>
  );
}
