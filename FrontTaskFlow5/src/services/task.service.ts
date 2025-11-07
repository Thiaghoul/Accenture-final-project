import api from "./api"
import type { Task } from "@/lib/mock-data"

export interface CreateTaskRequest {
  title: string
  description?: string
  status: Task["status"]
  priority: Task["priority"]
  assigneeId?: string
  dueDate?: string
  tags?: string[]
}

export interface UpdateTaskRequest {
  title?: string
  description?: string
  status?: Task["status"]
  priority?: Task["priority"]
  assigneeId?: string
  dueDate?: string
  tags?: string[]
}

export interface AddCommentRequest {
  content: string
}

class TaskService {
  /**
   * Get all tasks for a project
   */
  async getTasksByProject(projectId: string): Promise<Task[]> {
    const response = await api.get<Task[]>(`/projects/${projectId}/tasks`)
    return response.data
  }

  /**
   * Get task by ID
   */
  async getTaskById(projectId: string, taskId: string): Promise<Task> {
    const response = await api.get<Task>(`/projects/${projectId}/tasks/${taskId}`)
    return response.data
  }

  /**
   * Create new task
   */
  async createTask(projectId: string, data: CreateTaskRequest): Promise<Task> {
    const response = await api.post<Task>(`/projects/${projectId}/tasks`, data)
    return response.data
  }

  /**
   * Update task
   */
  async updateTask(projectId: string, taskId: string, data: UpdateTaskRequest): Promise<Task> {
    const response = await api.patch<Task>(`/projects/${projectId}/tasks/${taskId}`, data)
    return response.data
  }

  /**
   * Delete task
   */
  async deleteTask(projectId: string, taskId: string): Promise<void> {
    await api.delete(`/projects/${projectId}/tasks/${taskId}`)
  }

  /**
   * Move task to different status
   */
  async moveTask(projectId: string, taskId: string, newStatus: Task["status"]): Promise<Task> {
    const response = await api.patch<Task>(`/projects/${projectId}/tasks/${taskId}/move`, {
      status: newStatus,
    })
    return response.data
  }

  /**
   * Assign task to current user
   */
  async assignMeToTask(taskId: string): Promise<Task> {
    const response = await api.post<Task>(`/cards/${taskId}/assign-me`);
    return response.data;
  }

  /**
   * Add comment to task
   */
  async addComment(projectId: string, taskId: string, data: AddCommentRequest): Promise<Task> {
    const response = await api.post<Task>(`/projects/${projectId}/tasks/${taskId}/comments`, data)
    return response.data
  }
}

export const taskService = new TaskService()
