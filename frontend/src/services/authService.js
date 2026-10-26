import apiClient from '../api/client'

export async function login(email, password) {
  const { data } = await apiClient.post('/auth/login', { email, password })
  const token = data?.token ?? data?.accessToken ?? data?.jwt

  if (!token) {
    throw new Error('The server did not return an authentication token.')
  }

  return token
}

export async function register(email, password) {
  const { data } = await apiClient.post('/auth/register', { email, password })
  return data
}
