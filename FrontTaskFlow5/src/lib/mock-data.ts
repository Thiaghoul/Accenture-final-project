export interface Task {
  id: string
  title: string
  description: string
  status: "todo" | "in-progress" | "review" | "done"
  priority: "low" | "medium" | "high" | "urgent"
  assignee?: string
  dueDate?: string
  tags?: string[]
  comments?: Comment[]
  activities?: Activity[]
}

export interface Comment {
  id: string
  author: string
  content: string
  timestamp: string
}

export interface Activity {
  id: string
  type: string
  description: string
  timestamp: string
}

export interface Project {
  id: string
  name: string
  description: string
  color: string
  taskCount: number
  progress: number
  members: number
  lastUpdated: string
  totalTasks: number
  completedTasks: number
  updatedAt: string
}

export interface User {
  id: string
  name: string
  email: string
  avatar?: string
}

export const mockProjects: Project[] = [
  {
    id: "1",
    name: "Website Redesign",
    description: "Complete overhaul of company website",
    color: "#4A90E2",
    taskCount: 24,
    progress: 65,
    members: 5,
    lastUpdated: "2024-01-15",
    totalTasks: 24,
    completedTasks: 16,
    updatedAt: new Date().toISOString().split("T")[0],
  },
  {
    id: "2",
    name: "Mobile App Development",
    description: "iOS and Android app for customers",
    color: "#50E3C2",
    taskCount: 18,
    progress: 42,
    members: 4,
    lastUpdated: "2024-01-14",
    totalTasks: 18,
    completedTasks: 8,
    updatedAt: new Date().toISOString().split("T")[0],
  },
  {
    id: "3",
    name: "Marketing Campaign",
    description: "Q1 2024 marketing initiatives",
    color: "#F5A623",
    taskCount: 12,
    progress: 88,
    members: 3,
    lastUpdated: "2024-01-16",
    totalTasks: 12,
    completedTasks: 11,
    updatedAt: new Date().toISOString().split("T")[0],
  },
]

export const mockTasks: Task[] = [
  {
    id: "1",
    title: "Design new homepage layout",
    description: "Create wireframes and mockups for the new homepage design",
    status: "in-progress",
    priority: "high",
    assignee: "John Doe",
    dueDate: "2024-01-20",
    tags: ["design", "ui"],
    comments: [
      {
        id: "c1",
        author: "Jane Smith",
        content: "Looking great! Can we add more whitespace?",
        timestamp: "2024-01-15T10:30:00Z",
      },
    ],
    activities: [
      {
        id: "a1",
        type: "status_change",
        description: "Status changed from Todo to In Progress",
        timestamp: "2024-01-15T09:00:00Z",
      },
    ],
  },
  {
    id: "2",
    title: "Implement authentication system",
    description: "Set up user authentication with JWT tokens",
    status: "todo",
    priority: "urgent",
    assignee: "Alice Johnson",
    dueDate: "2024-01-18",
    tags: ["backend", "security"],
  },
  {
    id: "3",
    title: "Write API documentation",
    description: "Document all REST API endpoints",
    status: "review",
    priority: "medium",
    assignee: "Bob Wilson",
    dueDate: "2024-01-22",
    tags: ["documentation"],
  },
  {
    id: "4",
    title: "Fix mobile responsive issues",
    description: "Address layout problems on mobile devices",
    status: "done",
    priority: "high",
    assignee: "Carol Davis",
    dueDate: "2024-01-12",
    tags: ["frontend", "bug"],
  },
]
