"use client"

import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Slider } from "@/components/ui/slider"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Separator } from "@/components/ui/separator"
import { ScrollArea } from "@/components/ui/scroll-area"
import type { Task } from "@/components/task-flow/task-card"
import { type Priority, PriorityBadge } from "@/components/task-flow/priority-badge"
import { mockActivityLog } from "@/lib/mock-data"
import { Save, Trash2, CalendarIcon, User, MessageSquare, Clock, Loader2 } from "lucide-react"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"

interface TaskDetailDialogProps {
  task: Task
  open: boolean
  onOpenChange: (open: boolean) => void
  onDelete?: () => void
  onSave?: (task: Task) => void
}

export function TaskDetailDialog({ task, open, onOpenChange, onDelete, onSave }: TaskDetailDialogProps) {
  const [isEditing, setIsEditing] = useState(false)
  const [isSaving, setIsSaving] = useState(false)
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)

  const [title, setTitle] = useState(task.title)
  const [description, setDescription] = useState(
    "Implement a fully responsive navigation component that works seamlessly across all device sizes. Should include mobile hamburger menu, tablet optimization, and desktop full navigation.",
  )
  const [priority, setPriority] = useState<Priority>(task.priority)
  const [assignee, setAssignee] = useState(task.assignee?.name || "")
  const [dueDate, setDueDate] = useState("2025-11-10")
  const [completion, setCompletion] = useState([task.completionPercentage || 0])
  const [newComment, setNewComment] = useState("")
  const [comments, setComments] = useState([
    {
      id: "1",
      author: "Sarah Johnson",
      avatar: "/placeholder.svg?height=32&width=32",
      content: "I've started working on the mobile version. Should have a prototype ready by tomorrow.",
      timestamp: "2 hours ago",
    },
    {
      id: "2",
      author: "Mike Chen",
      avatar: "/placeholder.svg?height=32&width=32",
      content: "Great! Make sure to test on both iOS and Android devices.",
      timestamp: "1 hour ago",
    },
  ])

  const handleSave = () => {
    setIsSaving(true)
    setTimeout(() => {
      setIsSaving(false)
      setIsEditing(false)
      onSave?.({
        ...task,
        title,
        priority,
        completionPercentage: completion[0],
      })
    }, 500)
  }

  const handleDelete = () => {
    onDelete?.()
    setShowDeleteDialog(false)
    onOpenChange(false)
  }

  const handleAddComment = () => {
    if (!newComment.trim()) return

    setComments([
      {
        id: Date.now().toString(),
        author: "John Doe",
        avatar: "/placeholder.svg?height=32&width=32",
        content: newComment,
        timestamp: "Just now",
      },
      ...comments,
    ])
    setNewComment("")
  }

  return (
    <>
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="max-w-4xl max-h-[90vh] p-0">
          <DialogHeader className="px-6 pt-6 pb-4">
            <div className="flex items-start justify-between gap-4">
              <div className="flex-1 space-y-2">
                {isEditing ? (
                  <Input
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="text-2xl font-bold h-auto py-2"
                  />
                ) : (
                  <DialogTitle className="text-2xl">{title}</DialogTitle>
                )}
                <div className="flex items-center gap-2">
                  <PriorityBadge priority={priority} />
                  <span className="text-sm text-muted-foreground">Task ID: {task.id}</span>
                </div>
              </div>

              <div className="flex items-center gap-2">
                {isEditing ? (
                  <>
                    <Button variant="outline" size="sm" onClick={() => setIsEditing(false)} disabled={isSaving}>
                      Cancel
                    </Button>
                    <Button size="sm" onClick={handleSave} disabled={isSaving}>
                      {isSaving ? (
                        <>
                          <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                          Saving...
                        </>
                      ) : (
                        <>
                          <Save className="mr-2 h-4 w-4" />
                          Save
                        </>
                      )}
                    </Button>
                  </>
                ) : (
                  <>
                    <Button variant="outline" size="sm" onClick={() => setIsEditing(true)}>
                      Edit
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setShowDeleteDialog(true)}
                      className="text-destructive hover:text-destructive"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </>
                )}
              </div>
            </div>
          </DialogHeader>

          <ScrollArea className="flex-1 px-6">
            <div className="grid md:grid-cols-3 gap-6 pb-6">
              {/* Main Content */}
              <div className="md:col-span-2 space-y-6">
                {/* Description */}
                <div className="space-y-2">
                  <Label className="text-base font-semibold">Description</Label>
                  {isEditing ? (
                    <Textarea
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      rows={4}
                      className="resize-none"
                    />
                  ) : (
                    <p className="text-sm text-muted-foreground leading-relaxed">{description}</p>
                  )}
                </div>

                <Separator />

                {/* Comments */}
                <div className="space-y-4">
                  <div className="flex items-center gap-2">
                    <MessageSquare className="h-5 w-5" />
                    <Label className="text-base font-semibold">Comments</Label>
                    <span className="text-sm text-muted-foreground">({comments.length})</span>
                  </div>

                  <div className="flex gap-3">
                    <Avatar className="h-8 w-8">
                      <AvatarImage src="/placeholder.svg?height=32&width=32" />
                      <AvatarFallback>JD</AvatarFallback>
                    </Avatar>
                    <div className="flex-1 space-y-2">
                      <Textarea
                        placeholder="Add a comment..."
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                        rows={2}
                        className="resize-none"
                      />
                      <Button size="sm" onClick={handleAddComment} disabled={!newComment.trim()}>
                        Add Comment
                      </Button>
                    </div>
                  </div>

                  <div className="space-y-4">
                    {comments.map((comment) => (
                      <div key={comment.id} className="flex gap-3">
                        <Avatar className="h-8 w-8">
                          <AvatarImage src={comment.avatar || "/placeholder.svg"} />
                          <AvatarFallback>
                            {comment.author
                              .split(" ")
                              .map((n) => n[0])
                              .join("")}
                          </AvatarFallback>
                        </Avatar>
                        <div className="flex-1 space-y-1">
                          <div className="flex items-center gap-2">
                            <span className="text-sm font-medium">{comment.author}</span>
                            <span className="text-xs text-muted-foreground">{comment.timestamp}</span>
                          </div>
                          <p className="text-sm text-muted-foreground">{comment.content}</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>

                <Separator />

                {/* Activity Log */}
                <div className="space-y-4">
                  <div className="flex items-center gap-2">
                    <Clock className="h-5 w-5" />
                    <Label className="text-base font-semibold">Activity Log</Label>
                  </div>

                  <div className="space-y-3">
                    {mockActivityLog.map((activity) => (
                      <div key={activity.id} className="flex gap-3 text-sm">
                        <div className="flex-shrink-0 w-2 h-2 rounded-full bg-primary mt-2" />
                        <div className="flex-1 space-y-1">
                          <div className="flex items-center gap-2">
                            <span className="font-medium">{activity.user}</span>
                            <span className="text-muted-foreground">{activity.action}</span>
                          </div>
                          {activity.details && <p className="text-muted-foreground text-xs">{activity.details}</p>}
                          <span className="text-xs text-muted-foreground">{activity.timestamp}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* Sidebar */}
              <div className="space-y-6">
                {/* Assignee */}
                <div className="space-y-2">
                  <Label className="flex items-center gap-2">
                    <User className="h-4 w-4" />
                    Assignee
                  </Label>
                  {isEditing ? (
                    <Select value={assignee} onValueChange={setAssignee}>
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Sarah Johnson">Sarah Johnson</SelectItem>
                        <SelectItem value="Mike Chen">Mike Chen</SelectItem>
                        <SelectItem value="Alex Rivera">Alex Rivera</SelectItem>
                        <SelectItem value="Emma Davis">Emma Davis</SelectItem>
                      </SelectContent>
                    </Select>
                  ) : (
                    <div className="flex items-center gap-2">
                      <Avatar className="h-8 w-8">
                        <AvatarImage src={task.assignee?.avatar || "/placeholder.svg"} />
                        <AvatarFallback>
                          {task.assignee?.name
                            .split(" ")
                            .map((n) => n[0])
                            .join("")}
                        </AvatarFallback>
                      </Avatar>
                      <span className="text-sm">{task.assignee?.name}</span>
                    </div>
                  )}
                </div>

                {/* Priority */}
                <div className="space-y-2">
                  <Label>Priority</Label>
                  {isEditing ? (
                    <Select value={priority} onValueChange={(value) => setPriority(value as Priority)}>
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="low">Low</SelectItem>
                        <SelectItem value="medium">Medium</SelectItem>
                        <SelectItem value="high">High</SelectItem>
                        <SelectItem value="urgent">Urgent</SelectItem>
                      </SelectContent>
                    </Select>
                  ) : (
                    <PriorityBadge priority={priority} />
                  )}
                </div>

                {/* Due Date */}
                <div className="space-y-2">
                  <Label className="flex items-center gap-2">
                    <CalendarIcon className="h-4 w-4" />
                    Due Date
                  </Label>
                  {isEditing ? (
                    <Input type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
                  ) : (
                    <p className="text-sm">{task.dueDate}</p>
                  )}
                </div>

                {/* Completion */}
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <Label>Completion</Label>
                    <span className="text-sm font-medium">{completion[0]}%</span>
                  </div>
                  {isEditing ? (
                    <Slider value={completion} onValueChange={setCompletion} max={100} step={5} className="w-full" />
                  ) : (
                    <div className="h-2 w-full rounded-full bg-muted overflow-hidden">
                      <div className="h-full bg-primary transition-all" style={{ width: `${completion[0]}%` }} />
                    </div>
                  )}
                </div>
              </div>
            </div>
          </ScrollArea>
        </DialogContent>
      </Dialog>

      <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Task</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete this task? This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDelete}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}
