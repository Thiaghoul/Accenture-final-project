"use client"

import { createContext, useContext, useState, useEffect, type ReactNode } from "react"
import { authService } from "@/services/auth.service"
import type { User } from "../lib/mock-data"
import type { LoginRequest, RegisterRequest } from "@/services/auth.service"

interface AuthContextData {
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
  updateUser: (user: User) => void
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData)

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  // Check for existing session on mount
  useEffect(() => {
    const initAuth = async () => {
      try {
        if (authService.isAuthenticated()) {
          // Try to get current user from API
          const currentUser = await authService.getCurrentUser()
          setUser(currentUser)
        } else {
          // Check localStorage for user data
          const storedUser = authService.getStoredUser()
          if (storedUser) {
            setUser(storedUser)
          }
        }
      } catch (error) {
        console.error("[v0] Failed to load user:", error)
        // Clear invalid session
        localStorage.removeItem("auth_token")
        localStorage.removeItem("user")
      } finally {
        setIsLoading(false)
      }
    }

    initAuth()
  }, [])

  const login = async (data: LoginRequest) => {
    setIsLoading(true)
    try {
      const response = await authService.login(data)
      setUser(response.user)
    } finally {
      setIsLoading(false)
    }
  }

  const register = async (data: RegisterRequest) => {
    setIsLoading(true)
    try {
      const response = await authService.register(data)
      setUser(response.user)
    } finally {
      setIsLoading(false)
    }
  }

  const logout = async () => {
    setIsLoading(true)
    try {
      await authService.logout()
      setUser(null)
    } finally {
      setIsLoading(false)
    }
  }

  const updateUser = (updatedUser: User) => {
    setUser(updatedUser)
    localStorage.setItem("user", JSON.stringify(updatedUser))
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        logout,
        updateUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider")
  }

  return context
}
