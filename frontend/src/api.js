// Keep the original API module exports available while routing all requests
// through the shared Axios client and its JWT interceptor.
export {
  getMonitors,
  createMonitor,
  checkMonitor,
  getPriceHistory,
  deleteMonitor,
} from './services/monitorService'
