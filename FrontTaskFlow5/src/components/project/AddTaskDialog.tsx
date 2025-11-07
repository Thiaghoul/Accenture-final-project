"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useParams } from "react-router-dom"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import type { Task, User } from "@/lib/mock-data"
import { toast } from "sonner"
import { taskService } from "@/services/task.service"
import { userService } from "@/services/user.service"

interface AddTaskDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onTaskAdd: (task: Task) => void
}

export function AddTaskDialog({ open, onOpenChange, onTaskAdd }: AddTaskDialogProps) {
  const { id: projectId } = useParams()
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [priority, setPriority] = useState<Task["priority"]>("medium")
  const [assigneeId, setAssigneeId] = useState<string | undefined>(undefined)
  const [dueDate, setDueDate] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [users, setUsers] = useState<User[]>([])

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const userList = await userService.getUsers()
        setUsers(userList)
      } catch (error) {
        console.error("Failed to fetch users", error)
        toast.error("Não foi possível carregar a lista de usuários.")
      }
    }
    if (open) {
      fetchUsers()
    }
  }, [open])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!title.trim()) {
      toast.error("Título da tarefa é obrigatório")
      return
    }

    if (!projectId) {
      toast.error("ID do projeto não encontrado")
      return
    }

    setIsLoading(true)

    try {
      const newTask = await taskService.createTask(projectId, {
        title: title.trim(),
        description: description.trim(),
        status: "todo",
        priority,
        assigneeId: assigneeId,
        dueDate: dueDate || undefined,
        tags: [],
      })

      onTaskAdd(newTask)
      toast.success("Tarefa criada com sucesso!")
      onOpenChange(false)

      // Reset form
      setTitle("")
      setDescription("")
      setPriority("medium")
      setAssigneeId(undefined)
      setDueDate("")
    } catch (error: any) {
      console.error("[v0] Create task error:", error)
      toast.error(error.response?.data?.message || "Erro ao criar tarefa. Tente novamente.")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Criar Nova Tarefa</DialogTitle>
          <DialogDescription>Adicione uma nova tarefa ao projeto</DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-2">
            <Label htmlFor="task-title">Título da Tarefa</Label>
            <Input
              id="task-title"
              placeholder="Ex: Implementar sistema de login"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              autoFocus
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="task-description">Descrição</Label>
            <textarea
              id="task-description"
              placeholder="Descreva os detalhes da tarefa..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="flex min-h-[100px] w-full rounded-md border border-neutral-300 bg-white px-3 py-2 text-sm ring-offset-white placeholder:text-neutral-400 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="task-priority">Prioridade</Label>
              <select
                id="task-priority"
                value={priority}
                onChange={(e) => setPriority(e.target.value as Task["priority"])}
                className="flex h-10 w-full rounded-md border border-neutral-300 bg-white px-3 py-2 text-sm ring-offset-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
              >
                <option value="low">Baixa</option>
                <option value="medium">Média</option>
                <option value="high">Alta</option>
                <option value="urgent">Urgente</option>
              </select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="task-due-date">Data de Entrega</Label>
              <Input id="task-due-date" type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="task-assignee">Responsável</Label>
            <select
              id="task-assignee"
              value={assigneeId || ""}
              onChange={(e) => setAssigneeId(e.target.value || undefined)}
              className="flex h-10 w-full rounded-md border border-neutral-300 bg-white px-3 py-2 text-sm ring-offset-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
            >
              <option value="">Não atribuído</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>
                  {user.name}
                </option>
              ))}
            </select>
          </div>

          <div className="flex gap-3 pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)} className="flex-1">
              Cancelar
            </Button>
            <Button type="submit" disabled={isLoading} className="flex-1">
              {isLoading ? "Criando..." : "Criar Tarefa"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
}
