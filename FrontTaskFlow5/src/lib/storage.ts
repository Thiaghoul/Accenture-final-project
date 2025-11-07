import type { Project, Task } from "./mock-data"

const PROJECTS_KEY = "taskflow_projects"
const TASKS_KEY = "taskflow_tasks"
const USER_KEY = "user"

export const getUserId = (): string | null => {
  const user = localStorage.getItem(USER_KEY)
  if (user) {
    try {
      const userData = JSON.parse(user)
      return userData.id || null
    } catch (error) {
      console.error("Failed to parse user data from storage", error)
      return null
    }
  }
  return null
}

export const storage = {
  // Projects
  getProjects(): Project[] {
    const stored = localStorage.getItem(PROJECTS_KEY)
    return stored ? JSON.parse(stored) : []
  },

  saveProjects(projects: Project[]): void {
    localStorage.setItem(PROJECTS_KEY, JSON.stringify(projects))
  },

  addProject(project: Project): void {
    const projects = this.getProjects()
    projects.push(project)
    this.saveProjects(projects)
  },

  getProject(id: string): Project | undefined {
    const projects = this.getProjects()
    return projects.find((p) => p.id === id)
  },

  // Tasks
  getTasks(): Task[] {
    const stored = localStorage.getItem(TASKS_KEY)
    return stored ? JSON.parse(stored) : []
  },

  saveTasks(tasks: Task[]): void {
    localStorage.setItem(TASKS_KEY, JSON.stringify(tasks))
  },

  addTask(task: Task): void {
    const tasks = this.getTasks()
    tasks.push(task)
    this.saveTasks(tasks)
  },

  updateTask(taskId: string, updates: Partial<Task>): void {
    const tasks = this.getTasks()
    const index = tasks.findIndex((t) => t.id === taskId)
    if (index !== -1) {
      tasks[index] = { ...tasks[index], ...updates }
      this.saveTasks(tasks)
    }
  },

  // Initialize with mock data if empty
  initializeIfEmpty(mockProjects: Project[], mockTasks: Task[]): void {
    if (this.getProjects().length === 0) {
      this.saveProjects(mockProjects)
    }
    if (this.getTasks().length === 0) {
      this.saveTasks(mockTasks)
    }
  },
}
