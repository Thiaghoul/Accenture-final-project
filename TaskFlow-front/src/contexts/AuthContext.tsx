import React, { 
  createContext,  
  useState, 
  useEffect, 
  // 1. Correção: Usamos 'type' para importar o TIPO ReactNode
  type ReactNode 
} from 'react';
// Importamos as nossas funções de API
import { login as apiLogin } from '../services/authService';
// Importamos os tipos dos DTOs do backend
// Importamos os tipos dos DTOs do backend
import type { UserLoginRequest } from '../services/authService'; // Precisamos de adicionar este tipo ao authService
 // Precisamos de adicionar este tipo ao authService

// --- 1. Definir os Tipos ---

// O que o nosso contexto irá fornecer
interface AuthContextType {
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: UserLoginRequest) => Promise<void>;
  logout: () => void;
  // Poderíamos adicionar 'register' aqui também, se quisermos
}

// As 'props' que o nosso 'Provider' irá aceitar
interface AuthProviderProps {
  children: ReactNode;
}

// --- 2. Criar o Contexto ---
// Usamos '!' para dizer ao TypeScript que vamos fornecê-lo
const AuthContext = createContext<AuthContextType>(null!);

// --- 3. Criar o 'Provider' (o componente que gere o estado) ---
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true); // Começa a carregar para verificar o token

  // Efeito para verificar o localStorage ao iniciar
  useEffect(() => {
    try {
      const storedToken = localStorage.getItem('userToken');
      if (storedToken) {
        setToken(storedToken);
      }
    } catch (error) {
      console.error("Falha ao ler o token do localStorage", error);
    } finally {
      setIsLoading(false); // Termina de carregar
    }
  }, []);

  // Função de Login
  const login = async (credentials: UserLoginRequest) => {
    try {
      // Chamamos o serviço de API que já tínhamos
      const data = await apiLogin(credentials.email, credentials.password);
      if (data.token) {
        setToken(data.token);
        localStorage.setItem('userToken', data.token); // O serviço já faz isto, mas é bom garantir
      }
    } catch (error) {
      console.error("Falha no login (Context):", error);
      throw error; // Lança o erro para o formulário (LoginForm) poder apanhá-lo
    }
  };

  // Função de Logout
  const logout = () => {
    setToken(null);
    localStorage.removeItem('userToken');
    // TODO: Redirecionar para /auth
  };

  // O valor que será partilhado com todos os componentes
  const value: AuthContextType = {
    token,
    isAuthenticated: !!token, // Verdadeiro se o token existir
    isLoading,
    login,
    logout,
  };

  // Não renderiza nada até terminarmos de verificar o token
  if (isLoading) {
    return <div>A carregar aplicação...</div>; // Ou um 'spinner'
  }

  // 4. Retorna o 'Provider' com os 'children' (a nossa app)
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};