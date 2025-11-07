"use client"

import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import type { Task } from "@/lib/mock-data"
import { cn } from "@/lib/utils"
import { toast } from "sonner"

interface TaskDetailDialogProps {
  task: Task | null
  open: boolean
  onOpenChange: (open: boolean) => void
  onTaskUpdate: (task: Task) => void
}

const priorityConfig = {
  low: { label: "Baixa", color: "bg-neutral-100 text-neutral-700" },
  medium: { label: "Média", color: "bg-blue-100 text-blue-700" },
  high: { label: "Alta", color: "bg-orange-100 text-orange-700" },
  urgent: { label: "Urgente", color: "bg-red-100 text-red-700" },
}

const statusConfig = {
  todo: { label: "A Fazer", color: "#737373" },
  "in-progress": { label: "Em Progresso", color: "#4A90E2" },
  review: { label: "Em Revisão", color: "#F5A623" },
  done: { label: "Concluído", color: "#7ED321" },
}

export function TaskDetailDialog({ task, open, onOpenChange, onTaskUpdate }: TaskDetailDialogProps) {
  const [isEditing, setIsEditing] = useState(false)
  const [editedTask, setEditedTask] = useState<Task | null>(task)
  const [newComment, setNewComment] = useState("")

  useEffect(() => {
    if (task) {
      setEditedTask(task)
      setIsEditing(false)
    }
  }, [task])

  if (!task || !editedTask) return null

  const handleSave = () => {
    onTaskUpdate(editedTask)
    setIsEditing(false)
    toast.success("Tarefa atualizada com sucesso!")
  }

  const handleAddComment = () => {
    if (!newComment.trim()) return

    const comment = {
      id: Date.now().toString(),
      author: "Você",
      content: newComment,
      timestamp: new Date().toISOString(),
    }

    const updatedTask = {
      ...editedTask,
      comments: [...(editedTask.comments || []), comment],
    }
    setEditedTask(updatedTask)
    onTaskUpdate(updatedTask)
    setNewComment("")
    toast.success("Comentário adicionado!")
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <div className="flex items-start justify-between">
            <div className="flex-1">
              {isEditing ? (
                <Input
                  value={editedTask.title}
                  onChange={(e) => setEditedTask({ ...editedTask, title: e.target.value })}
                  className="text-xl font-semibold mb-2"
                />
              ) : (
                <DialogTitle className="text-2xl">{editedTask.title}</DialogTitle>
              )}
            </div>
            <div className="flex gap-2">
              {isEditing ? (
                <>
                  <Button size="sm" onClick={handleSave}>
                    <span className="material-icons text-lg mr-1">save</span>
                    Salvar
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => {
                      setEditedTask(task)
                      setIsEditing(false)
                    }}
                  >
                    Cancelar
                  </Button>
                </>
              ) : (
                <Button size="sm" variant="outline" onClick={() => setIsEditing(true)}>
                  <span className="material-icons text-lg mr-1">edit</span>
                  Editar
                </Button>
              )}
              <Button
                size="sm"
                variant="ghost"
                onClick={() => onOpenChange(false)}
                className="text-neutral-500 hover:text-neutral-700"
              >
                <span className="material-icons text-lg">close</span>
              </Button>
            </div>
          </div>
        </DialogHeader>

        <div className="grid grid-cols-3 gap-6 mt-6">
          {/* Main Content */}
          <div className="col-span-2 space-y-6">
            {/* Description */}
            <div>
              <Label className="text-base font-semibold mb-2 block">Descrição</Label>
              {isEditing ? (
                <textarea
                  value={editedTask.description}
                  onChange={(e) => setEditedTask({ ...editedTask, description: e.target.value })}
                  className="flex min-h-[120px] w-full rounded-md border border-neutral-300 bg-white px-3 py-2 text-sm ring-offset-white placeholder:text-neutral-400 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
                  placeholder="Adicione uma descrição detalhada..."
                />
              ) : (
                <p className="text-neutral-700 leading-relaxed">{editedTask.description || "Sem descrição"}</p>
              )}
            </div>

            {/* Comments */}
            <div>
              <Label className="text-base font-semibold mb-3 block">
                Comentários ({editedTask.comments?.length || 0})
              </Label>
              <div className="space-y-4">
                {editedTask.comments && editedTask.comments.length > 0 ? (
                  editedTask.comments.map((comment) => (
                    <div key={comment.id} className="bg-neutral-50 rounded-lg p-4">
                      <div className="flex items-center gap-2 mb-2">
                        <div className="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center text-sm font-medium">
                          {comment.author
                            .split(" ")
                            .map((n) => n[0])
                            .join("")
                            .slice(0, 2)}
                        </div>
                        <div>
                          <p className="font-medium text-sm text-neutral-900">{comment.author}</p>
                          <p className="text-xs text-neutral-500">
                            {new Date(comment.timestamp).toLocaleString("pt-BR")}
                          </p>
                        </div>
                      </div>
                      <p className="text-sm text-neutral-700 leading-relaxed">{comment.content}</p>
                    </div>
                  ))
                ) : (
                  <p className="text-sm text-neutral-500 text-center py-4">Nenhum comentário ainda</p>
                )}

                {/* Add Comment */}
                <div className="flex gap-2">
                  <Input
                    placeholder="Adicionar um comentário..."
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter" && !e.shiftKey) {
                        e.preventDefault()
                        handleAddComment()
                      }
                    }}
                  />
                  <Button onClick={handleAddComment} disabled={!newComment.trim()}>
                    <span className="material-icons text-lg">send</span>
                  </Button>
                </div>
              </div>
            </div>

            {/* Activity Log */}
            <div>
              <Label className="text-base font-semibold mb-3 block">Atividades Recentes</Label>
              <div className="space-y-3">
                {editedTask.activities && editedTask.activities.length > 0 ? (
                  editedTask.activities.map((activity) => (
                    <div key={activity.id} className="flex gap-3">
                      <div className="w-8 h-8 rounded-full bg-neutral-100 flex items-center justify-center flex-shrink-0">
                        <span className="material-icons text-neutral-600 text-sm">history</span>
                      </div>
                      <div className="flex-1">
                        <p className="text-sm text-neutral-700">{activity.description}</p>
                        <p className="text-xs text-neutral-500 mt-1">
                          {new Date(activity.timestamp).toLocaleString("pt-BR")}
                        </p>
                      </div>
                    </div>
                  ))
                ) : (
                  <p className="text-sm text-neutral-500 text-center py-4">Nenhuma atividade registrada</p>
                )}
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Status */}
            <div>
              <Label className="text-sm font-semibold mb-2 block">Status</Label>
              {isEditing ? (
                <select
                  value={editedTask.status}
                  onChange={(e) => setEditedTask({ ...editedTask, status: e.target.value as Task["status"] })}
                  className="flex h-10 w-full rounded-md border border-neutral-300 bg-white px-3 py-2 text-sm"
                >
                  {Object.entries(statusConfig).map(([key, config]) => (
                    <option key={key} value={key}>
                      {config.label}
                    </option>
                  ))}
                </select>
              ) : (
                <div className="flex items-center gap-2">
                  <div
                    className="w-3 h-3 rounded-full"
                    style={{ backgroundColor: statusConfig[editedTask.status].color }}
                  />
                  <span className="text-sm font-medium">{statusConfig[editedTask.status].label}</span>
                </div>
              )}
            </div>

            {/* Priority */}
            <div>
              <Label className="text-sm font-semibold mb-2 block">Prioridade</Label>
              {isEditing ? (
                <select
                  value={editedTask.priority}
                  onChange={(e) => setEditedTask({ ...editedTask, priority: e.target.value as Task["priority"] })}
                  className="flex h-10 w-full rounded-md border border-neutral-300 bg-white px-3 py-2 text-sm"
                >
                  <option value="low">Baixa</option>
                  <option value="medium">Média</option>
                  <option value="high">Alta</option>
                  <option value="urgent">Urgente</option>
                </select>
              ) : (
                <span
                  className={cn(
                    "inline-block text-xs px-3 py-1.5 rounded font-medium",
                    priorityConfig[editedTask.priority].color,
                  )}
                >
                  {priorityConfig[editedTask.priority].label}
                </span>
              )}
            </div>

            {/* Assignee */}
            <div>
              <Label className="text-sm font-semibold mb-2 block">Responsável</Label>
              {isEditing ? (
                <Input
                  value={editedTask.assignee || ""}
                  onChange={(e) => setEditedTask({ ...editedTask, assignee: e.target.value })}
                  placeholder="Nome do responsável"
                />
              ) : editedTask.assignee ? (
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center text-sm font-medium">
                    {editedTask.assignee
                      .split(" ")
                      .map((n) => n[0])
                      .join("")
                      .slice(0, 2)}
                  </div>
                  <span className="text-sm">{editedTask.assignee}</span>
                </div>
              ) : (
                <span className="text-sm text-neutral-500">Não atribuído</span>
              )}
            </div>

            {/* Due Date */}
            <div>
              <Label className="text-sm font-semibold mb-2 block">Data de Entrega</Label>
              {isEditing ? (
                <Input
                  type="date"
                  value={editedTask.dueDate || ""}
                  onChange={(e) => setEditedTask({ ...editedTask, dueDate: e.target.value })}
                />
              ) : editedTask.dueDate ? (
                <div className="flex items-center gap-2 text-sm">
                  <span className="material-icons text-neutral-600 text-lg">schedule</span>
                  <span>{new Date(editedTask.dueDate).toLocaleDateString("pt-BR")}</span>
                </div>
              ) : (
                <span className="text-sm text-neutral-500">Sem prazo definido</span>
              )}
            </div>

            {/* Tags */}
            {editedTask.tags && editedTask.tags.length > 0 && (
              <div>
                <Label className="text-sm font-semibold mb-2 block">Tags</Label>
                <div className="flex flex-wrap gap-2">
                  {editedTask.tags.map((tag) => (
                    <span key={tag} className="text-xs px-2 py-1 bg-neutral-100 text-neutral-700 rounded">
                      {tag}
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
