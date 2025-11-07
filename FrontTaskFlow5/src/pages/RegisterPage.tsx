"use client"

import type React from "react"

import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { toast } from "sonner"
import { useAuth } from "@/context/AuthContext"

export default function RegisterPage() {
  const navigate = useNavigate()
  const { register } = useAuth()
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  })
  const [isLoading, setIsLoading] = useState(false)
  const [errors, setErrors] = useState<Record<string, string>>({})

  const validateForm = () => {
    const newErrors: Record<string, string> = {}

    if (!formData.firstName) {
      newErrors.firstName = "Nome é obrigatório"
    }

    if (!formData.lastName) {
      newErrors.lastName = "Sobrenome é obrigatório"
    }

    if (!formData.email) {
      newErrors.email = "Email é obrigatório"
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Email inválido"
    }

    if (!formData.password) {
      newErrors.password = "Senha é obrigatória"
    } else if (formData.password.length < 8) {
      newErrors.password = "Senha deve ter no mínimo 8 caracteres"
    }

    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "As senhas não coincidem"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validateForm()) return

    setIsLoading(true)

    try {
      await register({
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        password: formData.password,
      })
      toast.success("Conta criada com sucesso!")
      navigate("/dashboard")
    } catch (error: any) {
      console.error("[v0] Register error:", error)
      toast.error(error.response?.data?.message || "Erro ao criar conta. Tente novamente.")
    } finally {
      setIsLoading(false)
    }
  }

  const handleChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }))
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: "" }))
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary/10 to-secondary/10 p-8">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-lg shadow-lg p-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold text-neutral-900 mb-2">Criar Conta</h1>
            <p className="text-neutral-600">Comece a organizar suas tarefas</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="firstName">Nome</Label>
                <Input
                  id="firstName"
                  type="text"
                  placeholder="João"
                  value={formData.firstName}
                  onChange={(e) => handleChange("firstName", e.target.value)}
                  aria-invalid={!!errors.firstName}
                />
                {errors.firstName && (
                  <p className="text-sm text-error" role="alert">
                    {errors.firstName}
                  </p>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="lastName">Sobrenome</Label>
                <Input
                  id="lastName"
                  type="text"
                  placeholder="Silva"
                  value={formData.lastName}
                  onChange={(e) => handleChange("lastName", e.target.value)}
                  aria-invalid={!!errors.lastName}
                />
                {errors.lastName && (
                  <p className="text-sm text-error" role="alert">
                    {errors.lastName}
                  </p>
                )}
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="seu@email.com"
                value={formData.email}
                onChange={(e) => handleChange("email", e.target.value)}
                aria-invalid={!!errors.email}
              />
              {errors.email && (
                <p className="text-sm text-error" role="alert">
                  {errors.email}
                </p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">Senha</Label>
              <Input
                id="password"
                type="password"
                placeholder="••••••••"
                value={formData.password}
                onChange={(e) => handleChange("password", e.target.value)}
                aria-invalid={!!errors.password}
              />
              {errors.password && (
                <p className="text-sm text-error" role="alert">
                  {errors.password}
                </p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirmar Senha</Label>
              <Input
                id="confirmPassword"
                type="password"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={(e) => handleChange("confirmPassword", e.target.value)}
                aria-invalid={!!errors.confirmPassword}
              />
              {errors.confirmPassword && (
                <p className="text-sm text-error" role="alert">
                  {errors.confirmPassword}
                </p>
              )}
            </div>

            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? "Criando conta..." : "Criar Conta"}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-neutral-600">
              Já tem uma conta?{" "}
              <Link to="/login" className="text-primary hover:underline font-medium">
                Entrar
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

