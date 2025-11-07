import { DashboardHeader } from "@/components/dashboard/dashboard-header"
import { ProjectGrid } from "@/components/dashboard/project-grid"
import { useEffect, useState } from "react"
import api from "@/services/api"
import { toast } from "sonner"
import type { Project } from "@/lib/mock-data"

export default function DashboardPage() {
  const [projects, setProjects] = useState<Project[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const response = await api.get<Project[]>("/api/projects")
        setProjects(response.data)
      } catch (error: any) {
        console.error("[v0] Failed to load projects:", error)
        toast.error(error.response?.data?.message || "Erro ao carregar projetos.")
      } finally {
        setIsLoading(false)
      }
    }

    fetchProjects()
  }, [])

  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="container mx-auto px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold mb-2">My Projects</h1>
          <p className="text-muted-foreground">Manage and track all your projects in one place</p>
        </div>
        {isLoading ? (
          <p>Loading projects...</p>
        ) : (
          <ProjectGrid projects={projects} />
        )}
      </main>
    </div>
  )
}
