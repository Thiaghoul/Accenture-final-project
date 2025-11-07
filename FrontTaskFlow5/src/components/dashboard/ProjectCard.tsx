"use client"

import { useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import type { Project } from "@/lib/mock-data"

interface ProjectCardProps {
  project: Project
}

export function ProjectCard({ project }: ProjectCardProps) {
  const navigate = useNavigate()

  return (
    <div
      className="bg-white rounded-lg border border-neutral-200 p-6 hover:shadow-lg transition-shadow cursor-pointer"
      onClick={() => navigate(`/project/${project.id}`)}
    >
      <div className="flex items-start justify-between mb-4">
        <div
          className="w-12 h-12 rounded-lg flex items-center justify-center"
          style={{ backgroundColor: `${project.color}20` }}
        >
          <span className="material-icons text-2xl" style={{ color: project.color }}>
            folder
          </span>
        </div>
        <Button
          variant="ghost"
          size="icon"
          onClick={(e) => {
            e.stopPropagation()
          }}
          aria-label="Mais opções"
        >
          <span className="material-icons">more_vert</span>
        </Button>
      </div>

      <h3 className="text-lg font-semibold text-neutral-900 mb-2 text-balance">{project.name}</h3>
      <p className="text-sm text-neutral-600 mb-4 line-clamp-2">{project.description}</p>

      <div className="space-y-3">
        <div>
          <div className="flex items-center justify-between text-sm mb-2">
            <span className="text-neutral-600">Progresso</span>
            <span className="font-medium text-neutral-900">{project.progress}%</span>
          </div>
          <div className="w-full bg-neutral-200 rounded-full h-2">
            <div
              className="h-2 rounded-full transition-all"
              style={{
                width: `${project.progress}%`,
                backgroundColor: project.color,
              }}
            />
          </div>
        </div>

        <div className="flex items-center justify-between text-sm">
          <div className="flex items-center gap-1 text-neutral-600">
            <span className="material-icons text-lg">check_circle</span>
            <span>{project.taskCount} tarefas</span>
          </div>
          <div className="flex items-center gap-1 text-neutral-600">
            <span className="material-icons text-lg">group</span>
            <span>{project.members} membros</span>
          </div>
        </div>

        <div className="text-xs text-neutral-500 pt-2 border-t border-neutral-100">
          Atualizado em {new Date(project.lastUpdated).toLocaleDateString("pt-BR")}
        </div>
      </div>
    </div>
  )
}
