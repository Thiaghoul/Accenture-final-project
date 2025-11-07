import api from "./api"
import type { Project } from "@/lib/mock-data"

export enum MemberRoles {
  VIEWER = "VIEWER",
  EDITOR = "EDITOR",
  MANAGER = "MANAGER",
}

export interface UserResponse {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
}

export interface CreateProjectRequest {
  name: string
  description: string
  color: string
}

export interface UpdateProjectRequest {
  name?: string
  description?: string
  color?: string
}

class ProjectService {
  /**
   * Get all projects for current user
   */
  async getProjects(): Promise<Project[]> {
    const response = await api.get<Project[]>("/projects")
    return response.data
  }

  /**
   * Get project by ID
   */
  async getProjectById(id: string): Promise<Project> {
    const response = await api.get<Project>(`/projects/${id}`)
    return response.data
  }

  /**
   * Create new project
   */
  async createProject(data: CreateProjectRequest): Promise<Project> {
    const response = await api.post<Project>("/projects", data)
    return response.data
  }

  /**
   * Update project
   */
  async updateProject(id: string, data: UpdateProjectRequest): Promise<Project> {
    const response = await api.patch<Project>(`/projects/${id}`, data)
    return response.data
  }

  /**
   * Delete project
   */
  async deleteProject(id: string): Promise<void> {
    await api.delete(`/projects/${id}`)
  }

  /**
   * Get project members
   */
  async getMembers(projectId: string): Promise<UserResponse[]> {
    const response = await api.get<UserResponse[]>(`/projects/${projectId}/members`)
    return response.data
  }

  /**
   * Add member to project
   */
  async addMember(projectId: string, userId: string, role: MemberRoles): Promise<void> {
    await api.post(`/projects/${projectId}/members`, { userId, role })
  }

  /**
   * Remove member from project
   */
  async removeMember(projectId: string, userId: string): Promise<void> {
    await api.delete(`/projects/${projectId}/members/${userId}`)
  }
}

export const projectService = new ProjectService()
