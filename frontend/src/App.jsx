import { useEffect, useState } from 'react'
import './App.css'
import {
  getMonitors,
  createMonitor,
  checkMonitor,
  deleteMonitor,
} from './api'

function App() {
  const [monitors, setMonitors] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [error, setError] = useState('')

  const [form, setForm] = useState({
    hotelUrl: '',
    checkIn: '',
    checkOut: '',
    guests: 2,
    rooms: 1,
    targetPrice: '',
  })

  async function loadMonitors() {
    try {
      const data = await getMonitors()
      setMonitors(data)
    } catch (err) {
      console.error(err)
      setError('Failed to load monitors.')
    }
  }

  useEffect(() => {
    loadMonitors()
  }, [])

  function handleChange(event) {
    const { name, value } = event.target

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }))
  }

  async function handleCreateMonitor(event) {
    event.preventDefault()

    try {
      setError('')

      await createMonitor({
        hotelUrl: form.hotelUrl,
        checkIn: form.checkIn,
        checkOut: form.checkOut,
        adults: Number(form.guests),
        rooms: Number(form.rooms),
        targetPrice: Number(form.targetPrice),
      })

      setForm({
        hotelUrl: '',
        checkIn: '',
        checkOut: '',
        guests: 2,
        rooms: 1,
        targetPrice: '',
      })

      setShowForm(false)

      await loadMonitors()
    } catch (err) {
      console.error(err)
      setError('Failed to create monitor.')
    }
  }

  async function handleCheck(id) {
    try {
      setError('')

      await checkMonitor(id)

      await loadMonitors()
    } catch (err) {
      console.error(err)
      setError('Failed to check price.')
    }
  }

  async function handleDelete(id) {
    const confirmed = window.confirm(
        `Are you sure you want to delete Monitor #${id}?`
    )

    if (!confirmed) {
      return
    }

    try {
      setError('')

      await deleteMonitor(id)

      await loadMonitors()
    } catch (err) {
      console.error(err)
      setError('Failed to delete monitor.')
    }
  }

  return (
      <div className="app">
        <header className="header">
          <div>
            <h1>OYO Price Monitor</h1>
            <p>Track hotel prices and get alerts when prices drop.</p>
          </div>

          <button
              className="add-button"
              onClick={() => {
                setShowForm((previous) => !previous)
                setError('')
              }}
          >
            + Add Monitor
          </button>
        </header>

        {error && (
            <div className="error">
              {error}
            </div>
        )}

        {showForm && (
            <section className="form-card">
              <h2>Add Hotel Monitor</h2>

              <form onSubmit={handleCreateMonitor}>
                <div className="form-group">
                  <label>OYO Hotel URL</label>

                  <input
                      type="url"
                      name="hotelUrl"
                      value={form.hotelUrl}
                      onChange={handleChange}
                      placeholder="https://www.oyorooms.com/..."
                      required
                  />
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>Check-in</label>

                    <input
                        type="date"
                        name="checkIn"
                        value={form.checkIn}
                        onChange={handleChange}
                        required
                    />
                  </div>

                  <div className="form-group">
                    <label>Check-out</label>

                    <input
                        type="date"
                        name="checkOut"
                        value={form.checkOut}
                        onChange={handleChange}
                        required
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>Guests</label>

                    <input
                        type="number"
                        name="guests"
                        min="1"
                        value={form.guests}
                        onChange={handleChange}
                        required
                    />
                  </div>

                  <div className="form-group">
                    <label>Rooms</label>

                    <input
                        type="number"
                        name="rooms"
                        min="1"
                        value={form.rooms}
                        onChange={handleChange}
                        required
                    />
                  </div>

                  <div className="form-group">
                    <label>Target Price</label>

                    <input
                        type="number"
                        name="targetPrice"
                        min="1"
                        value={form.targetPrice}
                        onChange={handleChange}
                        placeholder="₹"
                        required
                    />
                  </div>
                </div>

                <div className="form-actions">
                  <button type="submit" className="save-button">
                    Add Monitor
                  </button>

                  <button
                      type="button"
                      className="cancel-button"
                      onClick={() => setShowForm(false)}
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </section>
        )}

        <main className="content">
          <h2>Your Monitors</h2>

          {monitors.length === 0 ? (
              <div className="empty-state">
                <h3>No monitors yet</h3>
                <p>
                  Add an OYO hotel URL to start monitoring its price.
                </p>
              </div>
          ) : (
              <div className="monitor-list">
                {monitors.map((monitor) => (
                    <div className="monitor-card" key={monitor.id}>
                      <div className="monitor-header">
                        <div>
                          <h3>
                            Monitor #{monitor.id}
                          </h3>

                          <a
                              href={monitor.hotelUrl}
                              target="_blank"
                              rel="noreferrer"
                          >
                            {monitor.hotelUrl}
                          </a>
                        </div>

                        <span className="status">
                    {monitor.alertSent
                        ? 'Alert Sent'
                        : 'Monitoring'}
                  </span>
                      </div>

                      <div className="monitor-details">
                        <div>
                          <span>Check-in</span>
                          <strong>{monitor.checkIn}</strong>
                        </div>

                        <div>
                          <span>Check-out</span>
                          <strong>{monitor.checkOut}</strong>
                        </div>

                        <div>
                          <span>Guests</span>
                          <strong>
                            {monitor.guests ?? monitor.adults}
                          </strong>
                        </div>

                        <div>
                          <span>Rooms</span>
                          <strong>{monitor.rooms}</strong>
                        </div>

                        <div>
                          <span>Target Price</span>
                          <strong>
                            ₹{monitor.targetPrice}
                          </strong>
                        </div>

                        <div>
                          <span>Current Price</span>
                          <strong>
                            {monitor.currentPrice != null
                                ? `₹${monitor.currentPrice}`
                                : 'Not checked'}
                          </strong>
                        </div>
                      </div>

                      <div className="monitor-actions">
                        <button
                            className="check-button"
                            onClick={() => handleCheck(monitor.id)}
                        >
                          Check Now
                        </button>

                        <button
                            className="history-button"
                            onClick={() =>
                                window.open(
                                    `http://localhost:8080/api/monitors/${monitor.id}/history`,
                                    '_blank'
                                )
                            }
                        >
                          Price History
                        </button>

                        <button
                            className="delete-button"
                            onClick={() => handleDelete(monitor.id)}
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                ))}
              </div>
          )}
        </main>
      </div>
  )
}

export default App