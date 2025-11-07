"use client"

import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import type { Project } from "@/lib/mock-data"
import { projectService } from "@/services/project.service"
import { useAuth } from "../context/AuthContext"
import { ProjectCard } from "@/components/dashboard/ProjectCard"
import { CreateProjectDialog } from "@/components/dashboard/CreateProjectDialog"
import { toast } from "sonner"

export default function DashboardPage() {
  const navigate = useNavigate()
  const { logout, user } = useAuth()
  const [searchQuery, setSearchQuery] = useState("")
  const [projects, setProjects] = useState<Project[]>([])
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const loadProjects = async () => {
      try {
        const data = await projectService.getProjects()
        setProjects(data)
      } catch (error) {
        console.error("[v0] Failed to load projects:", error)
        toast.error("Erro ao carregar projetos")
      } finally {
        setIsLoading(false)
      }
    }

    loadProjects()
  }, [])

  const filteredProjects = projects.filter(
    (project) =>
      project.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      project.description.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  const handleLogout = async () => {
    try {
      await logout()
      navigate("/login")
    } catch (error) {
      console.error("[v0] Logout error:", error)
      navigate("/login")
    }
  }

  const handleProjectCreated = (newProject: Project) => {
    setProjects((prev) => [...prev, newProject])
  }

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <div className="flex flex-col items-center gap-4">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-primary border-t-transparent" />
          <p className="text-sm text-muted-foreground">Carregando projetos...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <header className="bg-white border-b border-neutral-200 sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-8 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <h1 className="text-2xl font-bold text-neutral-900">Task Flow</h1>
              <div className="relative w-96">
                <span className="material-icons absolute left-3 top-1/2 -translate-y-1/2 text-neutral-400 text-xl">
                  search
                </span>
                <Input
                  type="search"
                  placeholder="Buscar projetos..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>
            <div className="flex items-center gap-4">
              {user && <span className="text-sm text-neutral-600">Olá, {user.name}</span>}
              <Button variant="ghost" size="icon" aria-label="Notificações">
                <span className="material-icons">notifications</span>
              </Button>
              <Button variant="ghost" size="icon" aria-label="Configurações">
                <span className="material-icons">settings</span>
              </Button>
              <Button variant="outline" onClick={handleLogout}>
                <span className="material-icons text-lg">logout</span>
                Sair
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-8 py-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h2 className="text-3xl font-bold text-neutral-900 mb-2">Meus Projetos</h2>
            <p className="text-neutral-600">Gerencie e acompanhe todos os seus projetos</p>
          </div>
          <Button onClick={() => setIsCreateDialogOpen(true)}>
            <span className="material-icons text-lg">add</span>
            Novo Projeto
          </Button>
        </div>

        {filteredProjects.length === 0 ? (
          <div className="text-center py-16">
            <span className="material-icons text-6xl text-neutral-300 mb-4">folder_open</span>
            <h3 className="text-xl font-semibold text-neutral-900 mb-2">Nenhum projeto encontrado</h3>
            <p className="text-neutral-600 mb-6">
              {searchQuery ? "Tente ajustar sua busca" : "Comece criando seu primeiro projeto"}
            </p>
            {!searchQuery && (
              <Button onClick={() => setIsCreateDialogOpen(true)}>
                <span className="material-icons text-lg">add</span>
                Criar Projeto
              </Button>
            )}
          </div>
        ) : (
          <div className="grid grid-cols-3 gap-6">
            {filteredProjects.map((project) => (
              <ProjectCard key={project.id} project={project} />
            ))}
          </div>
        )}
      </main>

      <CreateProjectDialog
        open={isCreateDialogOpen}
        onOpenChange={setIsCreateDialogOpen}
        onProjectCreated={handleProjectCreated}
      />
    </div>
  )
}
