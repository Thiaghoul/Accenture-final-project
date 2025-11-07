"use client"

import { ProjectCard } from "./project-card"
import type { Project } from "@/lib/mock-data"

interface ProjectGridProps {
  projects: Project[]
}

export function ProjectGrid({ projects }: ProjectGridProps) {
  return (
    <div className="grid gap-6 grid-cols-3 xl:grid-cols-4">
      {projects.map((project) => (
        <ProjectCard key={project.id} project={project} />
      ))}
    </div>
  )
}
