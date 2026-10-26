function formatPrice(value) {
  if (value == null) return '—'
  return `₹${Number(value).toLocaleString('en-IN')}`
}

function formatDate(value) {
  if (!value) return 'Date unavailable'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString()
}

export default function PriceHistoryDialog({ monitor, history, loading, error, onClose }) {
  if (!monitor) return null

  return (
    <div className="dialog-backdrop" role="presentation" onMouseDown={(event) => {
      if (event.target === event.currentTarget) onClose()
    }}>
      <section className="history-dialog" role="dialog" aria-modal="true" aria-labelledby="history-title">
        <div className="dialog-header">
          <div>
            <span className="eyebrow">PRICE ACTIVITY</span>
            <h2 id="history-title">Monitor #{monitor.id} history</h2>
          </div>
          <button className="icon-button" type="button" onClick={onClose} aria-label="Close price history">×</button>
        </div>

        {loading ? (
          <div className="dialog-state">Loading price history…</div>
        ) : error ? (
          <div className="inline-error" role="alert">{error}</div>
        ) : history.length === 0 ? (
          <div className="dialog-state">No price checks have been recorded yet.</div>
        ) : (
          <div className="history-list">
            {history.map((entry, index) => (
              <div className="history-entry" key={entry.id ?? `${entry.checkedAt}-${index}`}>
                <span className="history-marker" aria-hidden="true" />
                <div><strong>{formatPrice(entry.price)}</strong><span>{formatDate(entry.checkedAt)}</span></div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  )
}
