import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

// Nossas páginas
import AuthPage from './pages/AuthPage';
import DashboardPage from './pages/Dashboard';
import BoardPage from './pages/BoardPage'; 

// 1. Importamos o nosso novo componente
import ProtectedRoute from './components/ProtectedRoute';

const App: React.FC = () => {
  return (
    <Routes>
      {/* Rota pública - qualquer um pode aceder */}
      <Route path="/auth" element={<AuthPage />} />
      
      {/* 2. Configuramos as rotas protegidas */}
      {/* Este 'Route' "embrulha" as rotas filhas. 
          Ele vai renderizar <ProtectedRoute /> */}
      <Route element={<ProtectedRoute />}>
        {/* Estas rotas filhas só serão renderizadas (pelo <Outlet />) 
            se o utilizador estiver autenticado */}
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/board/:boardId" element={<BoardPage />} />
      </Route>
      
      {/* Redireciona a rota raiz "/" para a autenticação por defeito */}
      <Route path="/" element={<Navigate replace to="/auth" />} />
    </Routes>
  );
}

export default App;