import { ProjectBoardHeader } from "@/components/project/project-board-header"
import { KanbanBoard } from "@/components/project/kanban-board"
import { mockProjects, mockTasks } from "@/lib/mock-data"
import { notFound } from "next/navigation"

interface ProjectPageProps {
  params: Promise<{
    id: string
  }>
}

export default async function ProjectPage({ params }: ProjectPageProps) {
  const { id } = await params
  const project = mockProjects.find((p) => p.id === id)

  if (!project) {
    notFound()
  }

  return (
    <div className="min-h-screen bg-background">
      <ProjectBoardHeader project={project} />
      <main className="container mx-auto px-8 py-6">
        <div className="mb-6">
          <h1 className="text-3xl font-bold mb-2">{project.name}</h1>
          <p className="text-muted-foreground">{project.description}</p>
        </div>
        <KanbanBoard initialTasks={mockTasks} />
      </main>
    </div>
  )
}
