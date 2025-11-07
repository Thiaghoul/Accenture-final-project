import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from '../pages/Login';
import Dashboard from '../pages/Dashboard';
import ProtectedRoute from './ProtectedRoute';
// 1. Importe a nova página
import ProjectDetailPage from '../pages/ProjectDetailPage'; 

/**
 * Define todas as rotas da aplicação.
 */
const AppRouter: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rota Pública */}
        <Route path="/login" element={<Login/>} />

        {/* Rotas Protegidas */}
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<Dashboard />} />
          
          {/* 2. Adicione a nova rota de detalhes do projeto */}
          <Route path="/projects/:id" element={<ProjectDetailPage />} />
          
          {/* <Route path="/settings" element={<SettingsPage />} /> */}
        </Route>
        
        <Route path="*" element={<div>404 - Página Não Encontrada</div>} />
      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;