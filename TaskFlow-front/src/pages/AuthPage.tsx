import React, { useState } from 'react';
import LoginForm from '../components/LoginForm';
import RegisterForm from '../components/RegisterForm';
// 1. A linha "import styles from..." FOI REMOVIDA.

const AuthPage: React.FC = () => {
  const [isLogin, setIsLogin] = useState(true);

  return (
    // 2. Aplicamos as classes Tailwind diretamente
    // bg-neutral-50 é próximo do seu fundo #F5F5F5
    <div className="flex flex-col items-center justify-center min-h-screen p-6 bg-neutral-50">
      {/* Usamos o espaçamento base-8 (p-8 = 32px) */}
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
        {isLogin ? (
          <>
            <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">Login</h2>
            <LoginForm />
            <p className="text-center mt-4 text-sm text-gray-600">
              Não tem uma conta?{' '}
              <button 
                onClick={() => setIsLogin(false)} 
                // Cor Primária #4A90E2
                className="font-semibold text-blue-600 hover:text-blue-700"
              >
                Registe-se
              </button>
            </p>
            <a 
              href="/forgot-password" 
              // Cor Neutra #666666
              className="block text-center mt-4 text-sm text-gray-600 hover:text-gray-800"
            >
              Esqueceu a senha?
            </a>
          </>
        ) : (
          <>
            <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">Registo</h2>
            <RegisterForm />
            <p className="text-center mt-4 text-sm text-gray-600">
              Já tem uma conta?{' '}
              <button 
                onClick={() => setIsLogin(true)} 
                className="font-semibold text-blue-600 hover:text-blue-700"
              >
                Login
              </button>
            </p>
          </>
        )}
      </div>
    </div>
  );
};

export default AuthPage;