import api from "./api"
import type { User } from "@/lib/mock-data"

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  firstName: string
  lastName: string
  email: string
  password: string
}

export interface AuthResponse {
  user: User
  token: string
}

export interface ForgotPasswordRequest {
  email: string
}

class AuthService {
  /**
   * Login user
   */
  async login(data: LoginRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>("/auth/login", data)

    // Save token and user to localStorage
    if (response.data.token) {
      localStorage.setItem("auth_token", response.data.token)
      localStorage.setItem("user", JSON.stringify(response.data.user))
    }

    return response.data
  }

  /**
   * Register new user
   */
  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>("/auth/register", data)

    // Save token and user to localStorage
    if (response.data.token) {
      localStorage.setItem("auth_token", response.data.token)
      localStorage.setItem("user", JSON.stringify(response.data.user))
    }

    return response.data
  }

  /**
   * Logout user
   */
  async logout(): Promise<void> {
    try {
      await api.post("/auth/logout")
    } catch (error) {
      console.error("[v0] Logout error:", error)
    } finally {
      // Always clear local storage
      localStorage.removeItem("auth_token")
      localStorage.removeItem("user")
    }
  }

  /**
   * Forgot password - Send reset email
   */
  async forgotPassword(data: ForgotPasswordRequest): Promise<{ message: string }> {
    const response = await api.post<{ message: string }>("/auth/forgot-password", data)
    return response.data
  }

  /**
   * Get current user from token
   */
  async getCurrentUser(): Promise<User> {
    const response = await api.get<User>("/auth/me")

    // Update user in localStorage
    localStorage.setItem("user", JSON.stringify(response.data))

    return response.data
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return !!localStorage.getItem("auth_token")
  }

  /**
   * Get stored user
   */
  getStoredUser(): User | null {
    const userStr = localStorage.getItem("user")
    return userStr ? JSON.parse(userStr) : null
  }
}

export const authService = new AuthService()
