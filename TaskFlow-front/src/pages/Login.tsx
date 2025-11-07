import React from "react";
import LoginForm from "../components/LoginForm"; // ajuste o caminho se necessário

const LoginPage: React.FC = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-muted/30 px-4">
      <div className="w-full max-w-4xl grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
        {/* Painel visual (opcional) inspirado no projeto 'code' */}
        <div className="hidden md:flex flex-col justify-center items-start gap-4 p-8 bg-gradient-to-br from-[#f7fbff] to-[#f2f7ff] rounded-2xl shadow-md">
          <div className="w-12 h-12 rounded-md flex items-center justify-center bg-[#4A90E2] text-white font-bold">TF</div>
          <h1 className="text-3xl font-bold text-gray-800">Bem-vindo de volta</h1>
          <p className="text-sm text-gray-600 max-w-sm">Gerencie tarefas com quadros Kanban, colabore com sua equipe e acompanhe o progresso do seu projeto.</p>
        </div>

        {/* Card do formulário */}
        <div className="flex items-center justify-center">
          <div className="w-full max-w-md bg-white rounded-2xl shadow-md border border-gray-100 p-8">
            <LoginForm />
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
