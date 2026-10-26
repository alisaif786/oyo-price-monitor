export default function TelegramConnectionCard({
  isConnected,
  isConnecting,
  toastMessage,
  onConnect,
}) {
  const description = isConnected
    ? 'Your Telegram account is connected. You’ll receive price alerts instantly.'
    : 'Connect your Telegram bot to receive instant notifications.'

  return (
    <section className="dashboard-summary-card telegram-summary-card" aria-labelledby="telegram-alerts-title">
      <div className="summary-card-heading">
        <div className="summary-card-title-group">
          <span className="summary-card-icon telegram-card-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="m4.2 11.4 14.9-6.1c.7-.3 1.4.4 1.1 1.1l-5.5 13.1c-.3.7-1.3.7-1.6 0l-2.2-4.4-4.4-2.2c-.6-.3-.5-1.2.1-1.5l8.7-4.1-7.5 5.6" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </span>
          <h2 id="telegram-alerts-title">Telegram Alerts</h2>
        </div>
        <div className={`telegram-compact-status ${isConnected ? 'is-connected' : 'is-disconnected'}`} role="status" aria-live="polite">
          <span className="telegram-compact-dot" aria-hidden="true" />
          {isConnected ? 'Connected' : 'Not Connected'}
        </div>
      </div>

      <p className="summary-card-description">{description}</p>

      {!isConnected && (
        <button className="button button-outline telegram-summary-button" type="button" onClick={onConnect} disabled={isConnecting}>
          {isConnecting && <span className="telegram-spinner" aria-hidden="true" />}
          {isConnecting ? 'Generating Link...' : 'Connect Telegram'}
        </button>
      )}

      {toastMessage && <div className="telegram-toast" role="alert">{toastMessage}</div>}
    </section>
  )
}
