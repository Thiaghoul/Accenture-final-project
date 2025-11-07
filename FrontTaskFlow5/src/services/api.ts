import axios from "axios"
import { getUserId } from "@/lib/storage"

// Base API URL - configure this in your .env file
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:3333/api"

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
})

// Request interceptor - Add auth token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("auth_token")
    const userId = getUserId()

    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    if (userId && config.headers) {
      config.headers["X-User-Id"] = userId
    }

    console.log("[v0] API Request:", config.method?.toUpperCase(), config.url)
    return config
  },
  (error) => {
    console.error("[v0] Request Error:", error)
    return Promise.reject(error)
  },
)

// Response interceptor - Handle errors globally
api.interceptors.response.use(
  (response) => {
    console.log("[v0] API Response:", response.status, response.config.url)
    return response
  },
  (error) => {
    console.error("[v0] Response Error:", error.response?.status, error.config?.url)

    // Handle 401 Unauthorized - Token expired or invalid
    if (error.response?.status === 401) {
      localStorage.removeItem("auth_token")
      localStorage.removeItem("user")
      window.location.href = "/login"
    }

    // Handle 403 Forbidden
    if (error.response?.status === 403) {
      console.error("[v0] Access denied")
    }

    // Handle 500 Server Error
    if (error.response?.status === 500) {
      console.error("[v0] Server error")
    }

    return Promise.reject(error)
  },
)

export default api
