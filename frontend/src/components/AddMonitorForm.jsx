import { useEffect, useState } from 'react'
import { getErrorMessage } from '../utils/getErrorMessage'

const INITIAL_FORM = {
  hotelUrl: '',
  checkIn: '',
  checkOut: '',
  guests: 2,
  rooms: 1,
  targetPrice: '',
}

export default function AddMonitorForm({ isOpen, onToggle, onSubmit }) {
  const [form, setForm] = useState(INITIAL_FORM)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!isOpen) return undefined

    function handleKeyDown(event) {
      if (event.key === 'Escape' && !isSubmitting) onToggle(false)
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [isOpen, isSubmitting, onToggle])

  function handleChange(event) {
    const { name, value } = event.target
    setForm((previous) => ({ ...previous, [name]: value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setIsSubmitting(true)

    try {
      await onSubmit({
        hotelUrl: form.hotelUrl,
        checkIn: form.checkIn,
        checkOut: form.checkOut,
        adults: Number(form.guests),
        rooms: Number(form.rooms),
        targetPrice: Number(form.targetPrice),
      })
      setForm(INITIAL_FORM)
      onToggle(false)
    } catch (submitError) {
      setError(getErrorMessage(submitError, 'Failed to create monitor.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  if (!isOpen) return null

  return (
    <div
      className="dialog-backdrop add-monitor-backdrop"
      role="presentation"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !isSubmitting) onToggle(false)
      }}
    >
      <section className="add-monitor-section add-monitor-dialog" id="add-monitor" role="dialog" aria-modal="true" aria-labelledby="add-monitor-title">
        <div className="section-heading add-monitor-heading">
          <div>
            <span className="eyebrow">PRICE TRACKING</span>
            <h2 id="add-monitor-title">Add Monitor</h2>
          </div>
          <button
            className="icon-button form-close-button"
            type="button"
            onClick={() => onToggle(false)}
            disabled={isSubmitting}
            aria-label="Close Add Monitor form"
          >
            <svg viewBox="0 0 16 16" aria-hidden="true"><path d="m3 3 10 10M13 3 3 13" /></svg>
          </button>
        </div>

        <form className="monitor-form" id="monitor-form" onSubmit={handleSubmit}>
          {error && <div className="inline-error" role="alert">{error}</div>}

          <div className="form-group form-group-wide">
            <label htmlFor="hotelUrl">OYO hotel URL</label>
            <input
              id="hotelUrl"
              type="url"
              name="hotelUrl"
              value={form.hotelUrl}
              onChange={handleChange}
              placeholder="https://www.oyorooms.com/..."
              autoComplete="url"
              required
            />
          </div>

          <div className="form-grid form-grid-two">
            <div className="form-group">
              <label htmlFor="checkIn">Check-in</label>
              <input id="checkIn" type="date" name="checkIn" value={form.checkIn} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label htmlFor="checkOut">Check-out</label>
              <input id="checkOut" type="date" name="checkOut" value={form.checkOut} onChange={handleChange} required />
            </div>
          </div>

          <div className="form-grid form-grid-three">
            <div className="form-group">
              <label htmlFor="guests">Guests</label>
              <input id="guests" type="number" name="guests" min="1" value={form.guests} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label htmlFor="rooms">Rooms</label>
              <input id="rooms" type="number" name="rooms" min="1" value={form.rooms} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label htmlFor="targetPrice">Target price (₹)</label>
              <input id="targetPrice" type="number" name="targetPrice" min="1" value={form.targetPrice} onChange={handleChange} placeholder="e.g. 2500" required />
            </div>
          </div>

          <div className="form-actions">
            <button className="button button-primary" type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Saving…' : 'Add monitor'}
            </button>
            <button className="button button-secondary" type="button" onClick={() => onToggle(false)} disabled={isSubmitting}>
              Cancel
            </button>
          </div>
        </form>
      </section>
    </div>
  )
}
