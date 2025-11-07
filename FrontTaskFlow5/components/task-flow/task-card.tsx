"use client"

import type React from "react"

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { PriorityBadge, type Priority } from "./priority-badge"
import { Calendar } from "lucide-react"
import { cn } from "@/lib/utils"

export interface Task {
  id: string
  title: string
  assignee?: {
    name: string
    avatar?: string
  }
  priority: Priority
  dueDate?: string
  completionPercentage?: number
}

interface TaskCardProps {
  task: Task
  onClick?: () => void
  className?: string
  draggable?: boolean
  onDragStart?: (e: React.DragEvent) => void
  onDragEnd?: (e: React.DragEvent) => void
}

export function TaskCard({ task, onClick, className, draggable = false, onDragStart, onDragEnd }: TaskCardProps) {
  const initials = task.assignee?.name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()

  return (
    <Card
      className={cn(
        "cursor-pointer transition-all hover:shadow-md",
        draggable && "cursor-grab active:cursor-grabbing",
        className,
      )}
      onClick={onClick}
      draggable={draggable}
      onDragStart={onDragStart}
      onDragEnd={onDragEnd}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") {
          e.preventDefault()
          onClick?.()
        }
      }}
    >
      <CardHeader className="p-4 pb-3">
        <div className="flex items-start justify-between gap-2">
          <h4 className="text-sm font-medium leading-snug text-balance">{task.title}</h4>
          <PriorityBadge priority={task.priority} />
        </div>
      </CardHeader>
      <CardContent className="p-4 pt-0">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            {task.assignee && (
              <Avatar className="h-6 w-6">
                <AvatarImage src={task.assignee.avatar || "/placeholder.svg"} alt={task.assignee.name} />
                <AvatarFallback className="text-xs">{initials}</AvatarFallback>
              </Avatar>
            )}
            {task.assignee && <span className="text-xs text-muted-foreground">{task.assignee.name}</span>}
          </div>
          {task.dueDate && (
            <div className="flex items-center gap-1 text-xs text-muted-foreground">
              <Calendar className="h-3 w-3" aria-hidden="true" />
              <span>{task.dueDate}</span>
            </div>
          )}
        </div>
        {task.completionPercentage !== undefined && (
          <div className="mt-3">
            <div className="flex items-center justify-between text-xs text-muted-foreground mb-1">
              <span>Progress</span>
              <span>{task.completionPercentage}%</span>
            </div>
            <div className="h-1.5 w-full rounded-full bg-muted overflow-hidden">
              <div
                className="h-full bg-primary transition-all"
                style={{ width: `${task.completionPercentage}%` }}
                role="progressbar"
                aria-valuenow={task.completionPercentage}
                aria-valuemin={0}
                aria-valuemax={100}
              />
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
