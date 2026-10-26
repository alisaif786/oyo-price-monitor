export function getErrorMessage(error, fallback) {
  const responseData = error?.response?.data

  if (typeof responseData === 'string' && responseData.trim()) {
    return responseData
  }

  return responseData?.message || responseData?.error || error?.message || fallback
}
