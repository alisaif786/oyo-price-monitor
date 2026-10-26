import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import AddMonitorForm from '../components/AddMonitorForm'
import MonitorCard from '../components/MonitorCard'
import PriceHistoryDialog from '../components/PriceHistoryDialog'
import TelegramConnectionCard from '../components/TelegramConnectionCard'
import TelegramInstructionsDialog from '../components/TelegramInstructionsDialog'
import { useAuth } from '../hooks/AuthContext'
import useTelegramConnection from '../hooks/useTelegramConnection'
import {
  checkMonitor,
  createMonitor,
  deleteMonitor,
  getMonitors,
  getPriceHistory,
} from '../services/monitorService'
import { getErrorMessage } from '../utils/getErrorMessage'

function getUserLabel(token) {
  try {
    const payloadPart = token?.split('.')[1]
    if (!payloadPart) return 'User'

    const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/')
    const paddedBase64 = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=')
    const payload = JSON.parse(window.atob(paddedBase64))
    const identity = payload.name || payload.preferred_username || payload.email || payload.sub

    if (typeof identity !== 'string' || !identity.trim()) return 'User'
    return identity.includes('@') ? identity.split('@')[0] : identity
  } catch {
    return 'User'
  }
}

export default function DashboardPage() {
  const [monitors, setMonitors] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [checkingId, setCheckingId] = useState(null)
  const [deletingId, setDeletingId] = useState(null)
  const [lastCheckedById, setLastCheckedById] = useState({})
  const [historyMonitor, setHistoryMonitor] = useState(null)
  const [history, setHistory] = useState([])
  const [isHistoryLoading, setIsHistoryLoading] = useState(false)
  const [historyError, setHistoryError] = useState('')
  const { token, signOut } = useAuth()
  const telegram = useTelegramConnection()
  const userLabel = getUserLabel(token)
  const activeMonitoringCount = monitors.filter((monitor) => monitor.active !== false && !monitor.alertSent).length

  const loadMonitors = useCallback(async () => {
    setError('')
    try {
      const data = await getMonitors()
      setMonitors(Array.isArray(data) ? data : [])
    } catch (loadError) {
      setError(getErrorMessage(loadError, 'Failed to load monitors.'))
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    loadMonitors()
  }, [loadMonitors])

  async function handleCreateMonitor(monitor) {
    await createMonitor(monitor)
    await loadMonitors()
  }

  async function handleCheck(id) {
    setError('')
    setCheckingId(id)
    try {
      await checkMonitor(id)
      setLastCheckedById((previous) => ({ ...previous, [id]: new Date().toISOString() }))
      await loadMonitors()
    } catch (checkError) {
      setError(getErrorMessage(checkError, 'Failed to check price.'))
    } finally {
      setCheckingId(null)
    }
  }

  async function handleDelete(id) {
    if (!window.confirm(`Are you sure you want to delete Monitor #${id}?`)) return

    setError('')
    setDeletingId(id)
    try {
      await deleteMonitor(id)
      await loadMonitors()
    } catch (deleteError) {
      setError(getErrorMessage(deleteError, 'Failed to delete monitor.'))
    } finally {
      setDeletingId(null)
    }
  }

  async function handleHistory(monitor) {
    setHistoryMonitor(monitor)
    setHistory([])
    setHistoryError('')
    setIsHistoryLoading(true)

    try {
      const data = await getPriceHistory(monitor.id)
      setHistory(Array.isArray(data) ? data : [])
    } catch (historyLoadError) {
      setHistoryError(getErrorMessage(historyLoadError, 'Failed to load price history.'))
    } finally {
      setIsHistoryLoading(false)
    }
  }

  function handleLogout() {
    signOut()
  }

  return (
    <div className="dashboard-page">
      <header className="dashboard-header">
        <div className="dashboard-header-inner">
          <Link className="dashboard-brand" to="/dashboard" aria-label="OYO Price Monitor dashboard">
            <span className="dashboard-brand-mark" aria-hidden="true">
              <svg viewBox="0 0 40 40" fill="none">
                <circle cx="20" cy="20" r="12" stroke="currentColor" strokeWidth="2.4" />
                <path d="M20 12.8c-3.7 0-6.2 2.7-6.2 6 0 4.1 6.2 9.1 6.2 9.1s6.2-5 6.2-9.1c0-3.3-2.5-6-6.2-6Z" stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round" />
                <path d="m17.4 20.1 2-2 1.6 1.5 2.1-2.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
            </span>
            <span className="dashboard-brand-copy">
              <strong>OYO Price Monitor</strong>
              <small>Smart Hotel Price Tracking</small>
            </span>
          </Link>
          <nav className="dashboard-navigation" aria-label="Main navigation">
            <Link className="dashboard-nav-link" to="/dashboard" aria-current="page">Dashboard</Link>
            <button className="dashboard-nav-logout" type="button" onClick={handleLogout}>
              <svg viewBox="0 0 20 20" fill="none" aria-hidden="true">
                <path d="M8 3.5H4.75A1.75 1.75 0 0 0 3 5.25v9.5c0 .97.78 1.75 1.75 1.75H8M12.5 6l4 4-4 4M7 10h9.25" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
              Logout
            </button>
          </nav>
        </div>
      </header>

      <main className="dashboard-content">
        <section className="welcome-section">
          <div>
            <h1>Welcome back, <br /> {userLabel} <span aria-hidden="true">👋</span></h1>
            <p>Track hotel prices automatically and receive instant Telegram alerts whenever prices drop.</p>
          </div>
        </section>

        <section className="dashboard-summary-grid" aria-label="Monitoring overview">
          <TelegramConnectionCard
            isConnected={telegram.isConnected}
            isConnecting={telegram.isConnecting}
            toastMessage={telegram.toastMessage}
            onConnect={telegram.connectTelegram}
          />

          <section className="dashboard-summary-card monitoring-overview-card" aria-labelledby="monitoring-overview-title">
            <div className="summary-card-heading">
              <span className="summary-card-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M4 18.5h16M5.5 15.5l4.2-4.2 3.1 2.6 5.7-6.4M15 7.5h3.5V11" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              </span>
              <h2 id="monitoring-overview-title">Monitoring Overview</h2>
            </div>
            <div className="monitoring-overview-count">
              <strong aria-live="polite">{isLoading ? '—' : activeMonitoringCount}</strong>
              <span>Hotels Being Monitored</span>
            </div>
            <p>Active hotel price trackers.</p>
          </section>
        </section>

        <button className="button button-primary add-monitor-cta" type="button" onClick={() => setIsFormOpen((open) => !open)}>
          <span aria-hidden="true">+</span> Add Monitor
        </button>

        <AddMonitorForm isOpen={isFormOpen} onToggle={setIsFormOpen} onSubmit={handleCreateMonitor} />

        {error && <div className="page-error" role="alert">{error}</div>}

        <section className="monitors-section">
          <div className="section-heading monitors-heading">
            <div>
              <h2>Your Monitors <span className="count-badge">{monitors.length}</span></h2>
            </div>
          </div>

          {isLoading ? (
            <div className="monitor-skeleton-grid" aria-label="Loading monitors" aria-busy="true">
              {[0, 1].map((item) => <div className="monitor-skeleton" key={item} />)}
            </div>
          ) : monitors.length === 0 ? (
            <div className="empty-state">
              <svg className="empty-hotel-icon" viewBox="0 0 40 40" fill="none" aria-hidden="true">
                <path d="M8 33V12.5L20 6l12 6.5V33M5 33h30M15 16h2m6 0h2m-10 6h2m6 0h2m-3 11V27h-4v6" />
              </svg>
              <h3>No monitors yet</h3>
              <p>Start tracking your first hotel in under a minute.</p>
              <button className="button button-primary button-small" type="button" onClick={() => setIsFormOpen(true)}>+ Add Monitor</button>
            </div>
          ) : (
            <div className="monitor-list">
              {monitors.map((monitor) => (
                <MonitorCard
                  key={monitor.id}
                  monitor={monitor}
                  checking={checkingId === monitor.id}
                  deleting={deletingId === monitor.id}
                  lastChecked={lastCheckedById[monitor.id]}
                  onCheck={handleCheck}
                  onHistory={handleHistory}
                  onDelete={handleDelete}
                />
              ))}
            </div>
          )}
        </section>
      </main>

      <PriceHistoryDialog
        monitor={historyMonitor}
        history={history}
        loading={isHistoryLoading}
        error={historyError}
        onClose={() => setHistoryMonitor(null)}
      />
      <TelegramInstructionsDialog
        isOpen={telegram.isInstructionsOpen}
        command={telegram.connectionCommand}
        connectionUrl={telegram.connectionUrl}
        onOpenTelegram={telegram.openTelegram}
        onDone={telegram.finishConnection}
        onCancel={telegram.cancelInstructions}
      />
    </div>
  )
}
