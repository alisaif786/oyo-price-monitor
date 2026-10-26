function formatPrice(value) {
  if (value == null) return 'Not checked'
  return `\u20B9${Number(value).toLocaleString('en-IN')}`
}

function getHotelName(monitor) {
  if (monitor.hotelName || monitor.name) return monitor.hotelName || monitor.name

  try {
    const url = new URL(monitor.hotelUrl)
    const slug = url.pathname.split('/').filter(Boolean).pop()
    if (slug) {
      return decodeURIComponent(slug)
        .replace(/[-_]+/g, ' ')
        .replace(/\b\w/g, (letter) => letter.toUpperCase())
    }
    return url.hostname.replace(/^www\./, '')
  } catch {
    return `Monitor #${monitor.id}`
  }
}

function formatLastChecked(value) {
  if (!value) return 'Not checked'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' })
}

function getMonitorStatus(monitor) {
  if (monitor.alertSent) return { label: 'Alert Sent', className: 'status-alert' }
  if (monitor.active === false) return { label: 'Paused', className: 'status-paused' }
  return { label: 'Monitoring', className: 'status-monitoring' }
}

export default function MonitorCard({ monitor, checking, deleting, lastChecked, onCheck, onHistory, onDelete }) {
  const status = getMonitorStatus(monitor)

  return (
    <article className="monitor-card">
      <div className="monitor-card-header">
        <div className="monitor-title-wrap">
          <h3 className="hotel-name">{getHotelName(monitor)}</h3>
          <a href={monitor.hotelUrl} target="_blank" rel="noreferrer" className="hotel-link" title={monitor.hotelUrl}>
            {monitor.hotelUrl}
          </a>
        </div>
        <span className={`status-pill ${status.className}`}>
          <span className="status-dot" aria-hidden="true" />
          {status.label}
        </span>
      </div>

      <div className="monitor-summary">
        <div className="monitor-price-current"><span>Current price</span><strong>{formatPrice(monitor.currentPrice)}</strong></div>
        <div><span>Target price</span><strong>{formatPrice(monitor.targetPrice)}</strong></div>
      </div>

      <div className="monitor-details">
        <div><span>Check-in</span><strong>{monitor.checkIn || '—'}</strong></div>
        <div><span>Check-out</span><strong>{monitor.checkOut || '—'}</strong></div>
        <div><span>Last Checked</span><strong>{formatLastChecked(lastChecked || monitor.lastCheckedAt || monitor.checkedAt)}</strong></div>
        <div><span>Guests</span><strong>{monitor.guests ?? monitor.adults ?? '—'}</strong></div>
        <div><span>Rooms</span><strong>{monitor.rooms ?? '—'}</strong></div>
      </div>

      <div className="monitor-actions">
        <button className="button button-secondary button-small" type="button" disabled title="Editing requires a monitor update endpoint.">
          Edit
        </button>
        <button className="button button-secondary button-small" type="button" onClick={() => onCheck(monitor.id)} disabled={checking}>
          {checking ? 'Checking...' : 'Check now'}
        </button>
        <button className="button button-secondary button-small" type="button" onClick={() => onHistory(monitor)}>
          Price history
        </button>
        <button className="button button-danger button-small" type="button" onClick={() => onDelete(monitor.id)} disabled={deleting}>
          {deleting ? 'Deleting...' : 'Delete'}
        </button>
      </div>
    </article>
  )
}
