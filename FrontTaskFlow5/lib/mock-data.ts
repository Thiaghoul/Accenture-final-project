import type { Task } from "@/components/task-flow/task-card"

export interface Project {
  id: string
  name: string
  description: string
  createdAt: string
  updatedAt: string
  columns: ColumnResponse[]
}

export interface ColumnResponse {
  id: string
  name: string
  order: number
  boardId: string
  cards: CardResponse[]
}

export interface CardResponse {
  id: string
  title: string
  description: string
  priority: string
  dueDate?: string
  completionPercentage: number
  assigneeId?: string
  columnId: string
  createdAt: string
  updatedAt: string
}

export const mockTasks: Record<string, Task[]> = {
  todo: [
    {
      id: "1",
      title: "Design new homepage layout",
      assignee: { name: "Sarah Johnson", avatar: "/placeholder.svg?height=32&width=32" },
      priority: "high",
      dueDate: "Nov 15",
      completionPercentage: 0,
    },
    {
      id: "2",
      title: "Research competitor websites",
      assignee: { name: "Mike Chen", avatar: "/placeholder.svg?height=32&width=32" },
      priority: "medium",
      dueDate: "Nov 12",
      completionPercentage: 0,
    },
  ],
  inProgress: [
    {
      id: "3",
      title: "Implement responsive navigation",
      assignee: { name: "Alex Rivera", avatar: "/placeholder.svg?height=32&width=32" },
      priority: "high",
      dueDate: "Nov 10",
      completionPercentage: 65,
    },
    {
      id: "4",
      title: "Create design system documentation",
      assignee: { name: "Emma Davis", avatar: "/placeholder.svg?height=32&width=32" },
      priority: "medium",
      dueDate: "Nov 14",
      completionPercentage: 40,
    },
  ],
  review: [
    {
      id: "5",
      title: "Update brand guidelines",
      assignee: { name: "Sarah Johnson", avatar: "/placeholder.svg?height=32&width=32" },
      priority: "low",
      dueDate: "Nov 8",
      completionPercentage: 90,
    },
  ],
  done: [
    {
      id: "6",
      title: "Set up project repository",
      assignee: { name: "Mike Chen", avatar: "/placeholder.svg?height=32&width=32" },
      priority: "urgent",
      dueDate: "Nov 1",
      completionPercentage: 100,
    },
    {
      id: "7",
      title: "Initial stakeholder meeting",
      assignee: { name: "Alex Rivera", avatar: "/placeholder.svg?height=32&width=32" },
      priority: "high",
      dueDate: "Oct 28",
      completionPercentage: 100,
    },
  ],
}

export interface ActivityLog {
  id: string
  action: string
  user: string
  timestamp: string
  details?: string
}

export const mockActivityLog: ActivityLog[] = [
  {
    id: "1",
    action: "Task created",
    user: "Sarah Johnson",
    timestamp: "2 hours ago",
    details: "Created 'Design new homepage layout'",
  },
  {
    id: "2",
    action: "Status changed",
    user: "Alex Rivera",
    timestamp: "4 hours ago",
    details: "Moved 'Implement responsive navigation' to In Progress",
  },
  {
    id: "3",
    action: "Comment added",
    user: "Mike Chen",
    timestamp: "5 hours ago",
    details: "Added comment on 'Research competitor websites'",
  },
  {
    id: "4",
    action: "Task completed",
    user: "Alex Rivera",
    timestamp: "1 day ago",
    details: "Completed 'Initial stakeholder meeting'",
  },
]
