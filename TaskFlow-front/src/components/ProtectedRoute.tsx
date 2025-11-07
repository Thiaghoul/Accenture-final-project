import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';

const ProtectedRoute: React.FC = () => {
  const token = localStorage.getItem('userToken');

  if (token) {
    return <Outlet />;
  }

  return <Navigate to="/auth" replace />;
};

export default ProtectedRoute;