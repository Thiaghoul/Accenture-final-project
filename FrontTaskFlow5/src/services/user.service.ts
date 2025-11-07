import api from "./api"
import type { User } from "@/lib/mock-data"

class UserService {
  /**
   * Get all users
   */
  async getUsers(): Promise<User[]> {
    const response = await api.get<User[]>("/users")
    return response.data
  }
}

export const userService = new UserService()
