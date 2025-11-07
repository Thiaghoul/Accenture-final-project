"use client"

import { useState, useEffect } from "react"
import { useParams, useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import type { Task, Project } from "@/lib/mock-data"
import { projectService } from "@/services/project.service"
import { taskService } from "@/services/task.service"
import { KanbanBoard } from "@/components/project/KanbanBoard"
import { AddTaskDialog } from "@/components/project/AddTaskDialog"
import { ManageMembersDialog } from "@/components/project/ManageMembersDialog"
import { toast } from "sonner"

export default function ProjectBoardPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [tasks, setTasks] = useState<Task[]>([])
  const [project, setProject] = useState<Project | null>(null)
  const [searchQuery, setSearchQuery] = useState("")
  const [isAddTaskOpen, setIsAddTaskOpen] = useState(false)
  const [isManageMembersOpen, setIsManageMembersOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const loadProjectData = async () => {
      if (!id) return

      try {
        const [projectData, tasksData] = await Promise.all([
          projectService.getProjectById(id),
          taskService.getTasksByProject(id),
        ])
        setProject(projectData)
        setTasks(tasksData)
      } catch (error) {
        console.error("[v0] Failed to load project:", error)
        toast.error("Erro ao carregar projeto")
      } finally {
        setIsLoading(false)
      }
    }

    loadProjectData()
  }, [id])

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <div className="flex flex-col items-center gap-4">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-primary border-t-transparent" />
          <p className="text-sm text-muted-foreground">Carregando projeto...</p>
        </div>
      </div>
    )
  }

  if (!project) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-neutral-900 mb-2">Projeto não encontrado</h2>
          <Button onClick={() => navigate("/dashboard")}>Voltar ao Dashboard</Button>
        </div>
      </div>
    )
  }

  const filteredTasks = tasks.filter(
    (task) =>
      task.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      task.description.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  const handleTaskMove = async (taskId: string, newStatus: Task["status"]) => {
    try {
      await taskService.moveTask(id!, taskId, newStatus)
      setTasks((prev) => prev.map((task) => (task.id === taskId ? { ...task, status: newStatus } : task)))
      toast.success("Tarefa movida com sucesso!")
    } catch (error) {
      console.error("[v0] Failed to move task:", error)
      toast.error("Erro ao mover tarefa")
    }
  }

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <header className="bg-white border-b border-neutral-200 sticky top-0 z-40">
        <div className="max-w-[1600px] mx-auto px-8 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Button variant="ghost" size="icon" onClick={() => navigate("/dashboard")} aria-label="Voltar">
                <span className="material-icons">arrow_back</span>
              </Button>
              <div
                className="w-10 h-10 rounded-lg flex items-center justify-center"
                style={{ backgroundColor: `${project.color}20` }}
              >
                <span className="material-icons text-xl" style={{ color: project.color }}>
                  folder
                </span>
              </div>
              <div>
                <h1 className="text-xl font-bold text-neutral-900">{project.name}</h1>
                <p className="text-sm text-neutral-600">{project.description}</p>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <div className="relative w-80">
                <span className="material-icons absolute left-3 top-1/2 -translate-y-1/2 text-neutral-400 text-xl">
                  search
                </span>
                <Input
                  type="search"
                  placeholder="Buscar tarefas..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
              <Button onClick={() => setIsAddTaskOpen(true)}>
                <span className="material-icons text-lg">add</span>
                Nova Tarefa
              </Button>
              <Button variant="outline" onClick={() => setIsManageMembersOpen(true)}>
                <span className="material-icons text-lg mr-1">group</span>
                Gerenciar Membros
              </Button>
              <Button variant="ghost" size="icon" aria-label="Filtros">
                <span className="material-icons">filter_list</span>
              </Button>
              <Button variant="ghost" size="icon" aria-label="Mais opções">
                <span className="material-icons">more_vert</span>
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Kanban Board */}
      <main className="max-w-[1600px] mx-auto px-8 py-8">
        <KanbanBoard tasks={filteredTasks} onTaskMove={handleTaskMove} />
      </main>

      <AddTaskDialog
        open={isAddTaskOpen}
        onOpenChange={setIsAddTaskOpen}
        onTaskAdd={(task) => setTasks((prev) => [...prev, task])}
      />

      <ManageMembersDialog
        projectId={id!}
        open={isManageMembersOpen}
        onOpenChange={setIsManageMembersOpen}
      />
    </div>
  )
}
