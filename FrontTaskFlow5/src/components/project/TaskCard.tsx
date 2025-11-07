"use client"

import type { Task } from "@/lib/mock-data"
import { cn } from "@/lib/utils"

interface TaskCardProps {
  task: Task
  onDragStart: () => void
  onClick?: () => void
}

const priorityConfig = {
  low: { label: "Baixa", color: "bg-neutral-100 text-neutral-700" },
  medium: { label: "Média", color: "bg-blue-100 text-blue-700" },
  high: { label: "Alta", color: "bg-orange-100 text-orange-700" },
  urgent: { label: "Urgente", color: "bg-red-100 text-red-700" },
}

export function TaskCard({ task, onDragStart, onClick }: TaskCardProps) {
  const priority = priorityConfig[task.priority]

  return (
    <div
      draggable
      onDragStart={onDragStart}
      onClick={onClick}
      className="bg-white rounded-lg p-4 shadow-sm border border-neutral-200 hover:shadow-md transition-shadow cursor-pointer"
    >
      <div className="flex items-start justify-between mb-3">
        <h4 className="font-medium text-neutral-900 text-sm leading-snug flex-1 text-balance">{task.title}</h4>
        <button
          className="text-neutral-400 hover:text-neutral-600 -mt-1 -mr-1"
          onClick={(e) => {
            e.stopPropagation()
          }}
          aria-label="Mais opções"
        >
          <span className="material-icons text-lg">more_vert</span>
        </button>
      </div>

      {task.description && <p className="text-xs text-neutral-600 mb-3 line-clamp-2">{task.description}</p>}

      {task.tags && task.tags.length > 0 && (
        <div className="flex flex-wrap gap-1.5 mb-3">
          {task.tags.map((tag) => (
            <span key={tag} className="text-xs px-2 py-0.5 bg-neutral-100 text-neutral-700 rounded">
              {tag}
            </span>
          ))}
        </div>
      )}

      <div className="flex items-center justify-between pt-3 border-t border-neutral-100">
        <span className={cn("text-xs px-2 py-1 rounded font-medium", priority.color)}>{priority.label}</span>

        <div className="flex items-center gap-2">
          {task.dueDate && (
            <div className="flex items-center gap-1 text-xs text-neutral-600">
              <span className="material-icons text-sm">schedule</span>
              <span>{new Date(task.dueDate).toLocaleDateString("pt-BR", { day: "2-digit", month: "short" })}</span>
            </div>
          )}
          {task.assignee && (
            <div
              className="w-6 h-6 rounded-full bg-primary text-white flex items-center justify-center text-xs font-medium"
              title={task.assignee}
            >
              {task.assignee
                .split(" ")
                .map((n) => n[0])
                .join("")
                .slice(0, 2)}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
