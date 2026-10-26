import { useCallback, useEffect, useState } from 'react'
import { createTelegramConnectionToken } from '../services/telegramService'

const TELEGRAM_BOT_URL = 'https://t.me/Oyopricecheckerbot?start='
const CONNECTION_ERROR = 'Unable to generate Telegram connection link.'

export default function useTelegramConnection() {
  // The connection status is local until the backend exposes a status endpoint.
  const [isConnected, setIsConnected] = useState(false)
  const [isConnecting, setIsConnecting] = useState(false)
  const [isInstructionsOpen, setIsInstructionsOpen] = useState(false)
  const [connectionCommand, setConnectionCommand] = useState('')
  const [connectionUrl, setConnectionUrl] = useState('')
  const [toast, setToast] = useState(null)

  useEffect(() => {
    if (!toast) return undefined

    const timeoutId = window.setTimeout(() => setToast(null), 4000)
    return () => window.clearTimeout(timeoutId)
  }, [toast])

  const connectTelegram = useCallback(async () => {
    // Reserve the tab during the click so browsers do not block the later redirect.
    let telegramTab = null
    try {
      telegramTab = window.open('about:blank', '_blank')
    } catch {
      // The request still runs; a direct open is attempted after the token arrives.
    }

    setIsConnecting(true)
    setToast(null)

    try {
      const token = await createTelegramConnectionToken()
      const telegramUrl = `${TELEGRAM_BOT_URL}${encodeURIComponent(token)}`
      setConnectionCommand(`/connect ${token}`)
      setConnectionUrl(telegramUrl)

      if (telegramTab && !telegramTab.closed) {
        telegramTab.opener = null
        telegramTab.location.replace(telegramUrl)
      } else {
        window.open(telegramUrl, '_blank', 'noopener,noreferrer')
      }

      setIsInstructionsOpen(true)
    } catch {
      if (telegramTab && !telegramTab.closed) telegramTab.close()
      setToast({ message: CONNECTION_ERROR, id: Date.now() })
    } finally {
      setIsConnecting(false)
    }
  }, [])

  const finishConnection = useCallback(() => {
    setIsConnected(true)
    setIsInstructionsOpen(false)
  }, [])

  const cancelInstructions = useCallback(() => {
    setIsInstructionsOpen(false)
  }, [])

  const openTelegram = useCallback(() => {
    if (connectionUrl) window.open(connectionUrl, '_blank', 'noopener,noreferrer')
  }, [connectionUrl])

  return {
    isConnected,
    isConnecting,
    isInstructionsOpen,
    connectionCommand,
    connectionUrl,
    toastMessage: toast?.message ?? '',
    connectTelegram,
    openTelegram,
    finishConnection,
    cancelInstructions,
  }
}
