import { cn } from "@/lib/utils"

export type Priority = "low" | "medium" | "high" | "urgent"

interface PriorityBadgeProps {
  priority: Priority
  className?: string
}

const priorityConfig = {
  low: {
    label: "Low",
    className: "bg-muted text-muted-foreground",
  },
  medium: {
    label: "Medium",
    className: "bg-secondary text-secondary-foreground",
  },
  high: {
    label: "High",
    className: "bg-accent text-accent-foreground",
  },
  urgent: {
    label: "Urgent",
    className: "bg-destructive text-destructive-foreground",
  },
}

export function PriorityBadge({ priority, className }: PriorityBadgeProps) {
  const config = priorityConfig[priority]

  return (
    <span
      className={cn("inline-flex items-center rounded-md px-2 py-1 text-xs font-medium", config.className, className)}
    >
      {config.label}
    </span>
  )
}
