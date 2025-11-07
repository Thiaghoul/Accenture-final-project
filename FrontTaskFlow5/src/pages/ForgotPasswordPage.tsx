"use client"

import type React from "react"

import { useState } from "react"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { toast } from "sonner"
import { authService } from "@/services/auth.service"

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [isSubmitted, setIsSubmitted] = useState(false)
  const [error, setError] = useState("")

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!email) {
      setError("Email é obrigatório")
      return
    }

    if (!/\S+@\S+\.\S+/.test(email)) {
      setError("Email inválido")
      return
    }

    setIsLoading(true)
    setError("")

    try {
      await authService.forgotPassword({ email })
      setIsSubmitted(true)
      toast.success("Email de recuperação enviado!")
    } catch (error: any) {
      console.error("[v0] Forgot password error:", error)
      toast.error(error.response?.data?.message || "Erro ao enviar email. Tente novamente.")
    } finally {
      setIsLoading(false)
    }
  }

  if (isSubmitted) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary/10 to-secondary/10 p-8">
        <div className="w-full max-w-md">
          <div className="bg-white rounded-lg shadow-lg p-8 text-center">
            <div className="w-16 h-16 bg-success/10 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="material-icons text-success text-3xl">check_circle</span>
            </div>
            <h2 className="text-2xl font-bold text-neutral-900 mb-2">Email Enviado!</h2>
            <p className="text-neutral-600 mb-6">
              Enviamos instruções para recuperação de senha para <strong>{email}</strong>
            </p>
            <Link to="/login">
              <Button className="w-full">Voltar para Login</Button>
            </Link>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary/10 to-secondary/10 p-8">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-lg shadow-lg p-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold text-neutral-900 mb-2">Recuperar Senha</h1>
            <p className="text-neutral-600">Digite seu email e enviaremos instruções para redefinir sua senha</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="seu@email.com"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value)
                  setError("")
                }}
                aria-invalid={!!error}
              />
              {error && (
                <p className="text-sm text-error" role="alert">
                  {error}
                </p>
              )}
            </div>

            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? "Enviando..." : "Enviar Instruções"}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <Link to="/login" className="text-sm text-primary hover:underline">
              Voltar para Login
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
