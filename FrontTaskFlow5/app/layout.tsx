import type React from "react"
import type { Metadata } from "next"
import { Roboto, Open_Sans, Fira_Code } from "next/font/google"
import { Analytics } from "@vercel/analytics/next"
import "./globals.css"

const roboto = Roboto({
  subsets: ["latin"],
  weight: ["400", "500", "700"],
  variable: "--font-roboto",
})

const openSans = Open_Sans({
  subsets: ["latin"],
  weight: ["400", "600"],
  variable: "--font-open-sans",
})

const firaCode = Fira_Code({
  subsets: ["latin"],
  variable: "--font-fira-code",
})

export const metadata: Metadata = {
  title: "Task Flow Project",
  description: "Efficient task management with Kanban boards",
  generator: "v0.app",
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="en">
      <body className={`${roboto.className} antialiased`}>
        {children}
        <Analytics />
      </body>
    </html>
  )
}
