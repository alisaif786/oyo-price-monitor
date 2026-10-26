import { useEffect, useState } from 'react'

export default function TelegramInstructionsDialog({ isOpen, command, connectionUrl, onOpenTelegram, onDone, onCancel }) {
  const [copyState, setCopyState] = useState('Copy')

  useEffect(() => {
    if (!isOpen) return undefined

    function handleKeyDown(event) {
      if (event.key === 'Escape') onCancel()
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [isOpen, onCancel])

  useEffect(() => {
    setCopyState('Copy')
  }, [command, isOpen])

  async function handleCopy() {
    try {
      await navigator.clipboard.writeText(command)
      setCopyState('Copied')
    } catch {
      setCopyState('Select command to copy')
    }
  }

  if (!isOpen) return null

  return (
    <div
      className="dialog-backdrop telegram-dialog-backdrop"
      role="presentation"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) onCancel()
      }}
    >
      <section className="history-dialog telegram-dialog" role="dialog" aria-modal="true" aria-labelledby="telegram-dialog-title" aria-describedby="telegram-dialog-description">
        <div className="dialog-header">
          <div>
            <span className="eyebrow">TELEGRAM ALERTS</span>
            <h2 id="telegram-dialog-title">Connect Telegram</h2>
            <p className="telegram-dialog-subtitle" id="telegram-dialog-description">Receive instant Telegram notifications when prices drop.</p>
          </div>
        </div>

        <ol className="telegram-instructions">
          <li>Open Telegram.</li>
          <li>Send the generated command to the bot.</li>
        </ol>

        <div className="telegram-command-box">
          <code>{command || 'Generating command...'}</code>
          <button className="button button-outline button-small" type="button" onClick={handleCopy} disabled={!command}>
            {copyState}
          </button>
        </div>

        <div className="telegram-dialog-actions">
          <button className="button button-primary button-small" type="button" onClick={onOpenTelegram} disabled={!connectionUrl}>
            Open Telegram
          </button>
          <button className="button button-secondary" type="button" onClick={onDone}>Close</button>
        </div>
      </section>
    </div>
  )
}
