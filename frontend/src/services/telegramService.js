import apiClient from '../api/client'

export async function createTelegramConnectionToken() {
  const { data } = await apiClient.post('/api/telegram/connect')

  if (!data?.token) {
    throw new Error('The server did not return a Telegram connection token.')
  }

  return data.token
}
