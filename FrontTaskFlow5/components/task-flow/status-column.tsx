import type React from "react"
import { cn } from "@/lib/utils"

interface StatusColumnProps {
  title: string
  count?: number
  children: React.ReactNode
  className?: string
}

export function StatusColumn({ title, count, children, className }: StatusColumnProps) {
  return (
    <div className={cn("flex flex-col gap-4 min-w-[280px] md:min-w-[320px]", className)}>
      <div className="flex items-center justify-between px-2">
        <h3 className="font-semibold text-foreground">{title}</h3>
        {count !== undefined && (
          <span className="flex h-6 w-6 items-center justify-center rounded-full bg-muted text-xs font-medium text-muted-foreground">
            {count}
          </span>
        )}
      </div>
      <div className="flex flex-col gap-3 rounded-lg bg-muted/30 p-3 min-h-[200px]">{children}</div>
    </div>
  )
}
