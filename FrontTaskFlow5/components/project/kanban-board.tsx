"use client"

import type React from "react"

import { useState } from "react"
import { StatusColumn } from "@/components/task-flow/status-column"
import { TaskCard, type Task } from "@/components/task-flow/task-card"
import { TaskDetailDialog } from "./task-detail-dialog"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"

interface KanbanBoardProps {
  initialTasks: Record<string, Task[]>
}

type ColumnId = "todo" | "inProgress" | "review" | "done"

const columns: { id: ColumnId; title: string }[] = [
  { id: "todo", title: "To Do" },
  { id: "inProgress", title: "In Progress" },
  { id: "review", title: "Review" },
  { id: "done", title: "Done" },
]

export function KanbanBoard({ initialTasks }: KanbanBoardProps) {
  const [tasks, setTasks] = useState(initialTasks)
  const [draggedTask, setDraggedTask] = useState<{ task: Task; fromColumn: ColumnId } | null>(null)
  const [dragOverColumn, setDragOverColumn] = useState<ColumnId | null>(null)
  const [selectedTask, setSelectedTask] = useState<Task | null>(null)
  const [isDetailOpen, setIsDetailOpen] = useState(false)

  const handleDragStart = (task: Task, fromColumn: ColumnId) => {
    setDraggedTask({ task, fromColumn })
  }

  const handleDragEnd = () => {
    setDraggedTask(null)
    setDragOverColumn(null)
  }

  const handleDragOver = (e: React.DragEvent, columnId: ColumnId) => {
    e.preventDefault()
    setDragOverColumn(columnId)
  }

  const handleDragLeave = () => {
    setDragOverColumn(null)
  }

  const handleDrop = (e: React.DragEvent, toColumn: ColumnId) => {
    e.preventDefault()

    if (!draggedTask) return

    const { task, fromColumn } = draggedTask

    if (fromColumn === toColumn) {
      setDraggedTask(null)
      setDragOverColumn(null)
      return
    }

    // Remove task from source column
    const newTasks = { ...tasks }
    newTasks[fromColumn] = newTasks[fromColumn].filter((t) => t.id !== task.id)

    // Add task to destination column
    newTasks[toColumn] = [...newTasks[toColumn], task]

    setTasks(newTasks)
    setDraggedTask(null)
    setDragOverColumn(null)
  }

  const handleTaskClick = (task: Task) => {
    setSelectedTask(task)
    setIsDetailOpen(true)
  }

  return (
    <>
      <div className="flex gap-6 overflow-x-auto pb-4">
        {columns.map((column) => (
          <div
            key={column.id}
            onDragOver={(e) => handleDragOver(e, column.id)}
            onDragLeave={handleDragLeave}
            onDrop={(e) => handleDrop(e, column.id)}
            className="flex-shrink-0"
          >
            <StatusColumn
              title={column.title}
              count={tasks[column.id]?.length || 0}
              className={dragOverColumn === column.id ? "ring-2 ring-primary ring-offset-2" : ""}
            >
              {tasks[column.id]?.map((task) => (
                <TaskCard
                  key={task.id}
                  task={task}
                  onClick={() => handleTaskClick(task)}
                  draggable
                  onDragStart={(e) => {
                    e.dataTransfer.effectAllowed = "move"
                    handleDragStart(task, column.id)
                  }}
                  onDragEnd={handleDragEnd}
                  className={draggedTask?.task.id === task.id ? "opacity-50" : ""}
                />
              ))}

              <Button
                variant="ghost"
                className="w-full justify-start gap-2 text-muted-foreground hover:text-foreground"
                size="sm"
              >
                <Plus className="h-4 w-4" />
                Add Task
              </Button>
            </StatusColumn>
          </div>
        ))}
      </div>

      {selectedTask && <TaskDetailDialog task={selectedTask} open={isDetailOpen} onOpenChange={setIsDetailOpen} />}
    </>
  )
}
