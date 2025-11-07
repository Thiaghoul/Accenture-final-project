import * as React from "react"
import { cn } from "../../lib/utils"

export interface AlertProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: "default" | "destructive"
}

export const Alert = React.forwardRef<HTMLDivElement, AlertProps>(
  ({ className, variant = "default", ...props }, ref) => {
    const variants = {
      default: "bg-gray-50 border border-gray-300 text-gray-900",
      destructive: "bg-red-50 border border-red-500 text-red-800",
    }
    return (
      <div
        ref={ref}
        role="alert"
        className={cn("flex items-center gap-2 rounded-md p-4 text-sm", variants[variant], className)}
        {...props}
      />
    )
  }
)
Alert.displayName = "Alert"

export const AlertDescription = React.forwardRef<
  HTMLParagraphElement,
  React.HTMLAttributes<HTMLParagraphElement>
>(({ className, ...props }, ref) => (
  <p ref={ref} className={cn("text-sm", className)} {...props} />
))
AlertDescription.displayName = "AlertDescription"
