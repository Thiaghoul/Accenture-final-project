import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

/**
 * Este componente verifica se o usuário está autenticado.
 * - Se estiver carregando, mostra "Carregando...".
 * - Se não estiver logado, redireciona para /login.
 * - Se estiver logado, mostra o conteúdo da rota (Outlet).
 */
const ProtectedRoute: React.FC = () => {
  // Pega o estado de autenticação e carregamento do nosso "cérebro"
  const { isAuthenticated, isLoading } = useAuth();

  // 1. Ainda estamos verificando o token?
  if (isLoading) {
    // Você pode substituir isso por um belo <Spinner />
    return <div className="flex items-center justify-center h-screen">Carregando...</div>;
  }

  // 2. Não está logado? Redireciona para o login.
  if (!isAuthenticated) {
    // 'replace' impede que o usuário volte para a página protegida
    return <Navigate to="/login" replace />;
  }

  // 3. Está logado? Permite o acesso.
  // <Outlet /> é o placeholder para o componente que esta rota protege
  // (neste caso, a DashboardPage).
  return <Outlet />;
};

export default ProtectedRoute;