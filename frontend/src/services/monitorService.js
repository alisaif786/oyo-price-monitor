import apiClient from '../api/client'

const MONITORS_URL = '/api/monitors'

export async function getMonitors() {
  const { data } = await apiClient.get(MONITORS_URL)
  return data
}

export async function createMonitor(monitor) {
  const { data } = await apiClient.post(MONITORS_URL, monitor)
  return data
}

export async function getMonitor(id) {
  const { data } = await apiClient.get(`${MONITORS_URL}/${id}`)
  return data
}

export async function checkMonitor(id) {
  const { data } = await apiClient.get(`${MONITORS_URL}/${id}/check`)
  return data
}

export async function getPriceHistory(id) {
  const { data } = await apiClient.get(`${MONITORS_URL}/${id}/history`)
  return data
}

export async function deleteMonitor(id) {
  const { data } = await apiClient.delete(`${MONITORS_URL}/${id}`)
  return data
}
