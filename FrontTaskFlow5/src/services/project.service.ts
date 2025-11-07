import api from "./api"
import type { Project } from "@/lib/mock-data"

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
   * Add member to project
   */
  async addMember(projectId: string, userId: string): Promise<Project> {
    const response = await api.post<Project>(`/projects/${projectId}/members`, { userId })
    return response.data
  }

  /**
   * Remove member from project
   */
  async removeMember(projectId: string, userId: string): Promise<Project> {
    const response = await api.delete<Project>(`/projects/${projectId}/members/${userId}`)
    return response.data
  }
}

export const projectService = new ProjectService()
