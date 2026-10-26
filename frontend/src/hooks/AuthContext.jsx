import { createContext, useContext, useMemo, useState } from 'react'
import {
  clearAuthToken,
  getAuthToken,
  saveAuthToken,
} from '../utils/authStorage'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => getAuthToken())

  const value = useMemo(() => ({
    token,
    isAuthenticated: Boolean(token),
    signIn(nextToken) {
      saveAuthToken(nextToken)
      setToken(nextToken)
    },
    signOut() {
      clearAuthToken()
      setToken(null)
    },
  }), [token])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth must be used inside an AuthProvider.')
  }

  return context
}
