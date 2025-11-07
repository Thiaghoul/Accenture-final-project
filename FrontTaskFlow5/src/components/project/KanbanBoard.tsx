"use client"

import React from "react"

import { useState } from "react"
import type { Task } from "@/lib/mock-data"
import { TaskCard } from "./TaskCard"
import { TaskDetailDialog } from "./TaskDetailDialog"

interface KanbanBoardProps {
  tasks: Task[]
  onTaskMove: (taskId: string, newStatus: Task["status"]) => void
}

const columns: { id: Task["status"]; title: string; color: string }[] = [
  { id: "todo", title: "A Fazer", color: "#737373" },
  { id: "in-progress", title: "Em Progresso", color: "#4A90E2" },
  { id: "review", title: "Em Revisão", color: "#F5A623" },
  { id: "done", title: "Concluído", color: "#7ED321" },
]

export function KanbanBoard({ tasks, onTaskMove }: KanbanBoardProps) {
  const [draggedTask, setDraggedTask] = useState<string | null>(null)
  const [selectedTask, setSelectedTask] = useState<Task | null>(null)
  const [isDetailOpen, setIsDetailOpen] = useState(false)
  const [localTasks, setLocalTasks] = useState<Task[]>(tasks)

  React.useEffect(() => {
    setLocalTasks(tasks)
  }, [tasks])

  const handleDragStart = (taskId: string) => {
    setDraggedTask(taskId)
  }

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
  }

  const handleDrop = (status: Task["status"]) => {
    if (draggedTask) {
      onTaskMove(draggedTask, status)
      setDraggedTask(null)
    }
  }

  const handleTaskClick = (task: Task) => {
    const currentTask = localTasks.find((t) => t.id === task.id) || task
    setSelectedTask(currentTask)
    setIsDetailOpen(true)
  }

  const handleTaskUpdate = (updatedTask: Task) => {
    const updatedTasks = localTasks.map((task) => (task.id === updatedTask.id ? updatedTask : task))
    setLocalTasks(updatedTasks)
    setSelectedTask(updatedTask)
    onTaskMove(updatedTask.id, updatedTask.status)
  }

  return (
    <>
      <div className="grid grid-cols-4 gap-6">
        {columns.map((column) => {
          const columnTasks = localTasks.filter((task) => task.status === column.id)

          return (
            <div
              key={column.id}
              className="flex flex-col"
              onDragOver={handleDragOver}
              onDrop={() => handleDrop(column.id)}
            >
              <div className="flex items-center justify-between mb-4 px-2">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 rounded-full" style={{ backgroundColor: column.color }} />
                  <h3 className="font-semibold text-neutral-900">{column.title}</h3>
                  <span className="text-sm text-neutral-500 bg-neutral-100 px-2 py-0.5 rounded-full">
                    {columnTasks.length}
                  </span>
                </div>
                <button className="text-neutral-400 hover:text-neutral-600" aria-label="Mais opções">
                  <span className="material-icons text-lg">more_horiz</span>
                </button>
              </div>

              <div className="flex-1 bg-neutral-100 rounded-lg p-3 min-h-[600px] space-y-3">
                {columnTasks.length === 0 ? (
                  <div className="flex flex-col items-center justify-center h-32 text-neutral-400">
                    <span className="material-icons text-3xl mb-2">inbox</span>
                    <p className="text-sm">Nenhuma tarefa</p>
                  </div>
                ) : (
                  columnTasks.map((task) => (
                    <TaskCard
                      key={task.id}
                      task={task}
                      onDragStart={() => handleDragStart(task.id)}
                      onClick={() => handleTaskClick(task)}
                    />
                  ))
                )}
              </div>
            </div>
          )
        })}
      </div>

      <TaskDetailDialog
        task={selectedTask}
        open={isDetailOpen}
        onOpenChange={setIsDetailOpen}
        onTaskUpdate={handleTaskUpdate}
      />
    </>
  )
}
