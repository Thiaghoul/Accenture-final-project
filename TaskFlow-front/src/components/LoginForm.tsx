import React, { useState, type JSX } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { Alert, AlertDescription } from "./ui/alert";
import { AlertCircle, Loader2 } from "lucide-react";
import { login } from "../services/authService"; // ajuste o caminho se necessário

export default function LoginForm(): JSX.Element {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    // validação simples
    if (!email || !password) {
      setError("Preencha todos os campos.");
      return;
    }
    if (!/\S+@\S+\.\S+/.test(email)) {
      setError("Insira um e-mail válido.");
      return;
    }

    setIsLoading(true);
    try {
      // chame seu serviço real de login (ajuste assinatura se necessário)
      await login(email, password);
      navigate("/dashboard");
    } catch (err) {
      console.error(err);
      setError("Email ou senha inválidos. Tente novamente.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6 w-full max-w-md">
      {error && (
        <Alert variant="destructive" className="items-start">
          <AlertCircle className="h-4 w-4 mt-0.5" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="space-y-2">
        <Label htmlFor="email">Email</Label>
        <Input
          id="email"
          type="email"
          placeholder="you@example.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          disabled={isLoading}
          required
          aria-label="Email address"
        />
      </div>

      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <Label htmlFor="password">Senha</Label>
          <Link
            to="/forgot-password"
            className={`text-sm text-primary hover:underline ${isLoading ? "pointer-events-none opacity-60" : ""}`}
            tabIndex={isLoading ? -1 : 0}
          >
            Esqueceu a senha?
          </Link>
        </div>

        <Input
          id="password"
          type="password"
          placeholder="Digite sua senha"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          disabled={isLoading}
          required
          aria-label="Password"
        />
      </div>

      <Button type="submit" className="w-full" disabled={isLoading} aria-disabled={isLoading}>
        {isLoading ? (
          <>
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            Entrando...
          </>
        ) : (
          "Entrar"
        )}
      </Button>

      <div className="text-center text-sm text-muted-foreground">
        Não tem uma conta?{" "}
        <Link to="/register" className="text-primary font-medium hover:underline" tabIndex={isLoading ? -1 : 0}>
          Cadastre-se
        </Link>
      </div>
    </form>
  );
}
