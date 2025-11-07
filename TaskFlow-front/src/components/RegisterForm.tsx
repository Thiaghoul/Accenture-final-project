import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// 1. Importamos o 'register' e o TIPO 'UserRegisterRequest'
import { register } from '../services/authService';
// 2. REMOVEMOS a importação do ficheiro .module.css

const RegisterForm: React.FC = () => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError(null);
    try {
      // 3. Passamos os quatro argumentos esperados pela função register
      await register(firstName, lastName, email, password); 
      navigate('/dashboard');
    } catch (err) {
      // 4. Corrigimos o aviso do linter
      console.error('Erro no registro:', err); 
      setError('Falha no registro. O email pode já estar em uso.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* 5. Usamos classes Tailwind para o erro (Cor de Erro Spec 6.2) */}
      {error && <p className="text-red-600 text-sm mb-4">{error}</p>}
      
      {/* 6. Aplicamos Tailwind aos nossos grupos de formulário (Espaçamento Base-8) */}
      <div className="mb-4">
        <label htmlFor="register-firstname" className="block text-sm font-medium text-gray-700 mb-1 font-sans">
          Nome
        </label>
        {/* Estilo do Input Field (Spec 5.2.2) */}
        <input
          type="text"
          id="register-firstname"
          className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={firstName}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => setFirstName(e.target.value)}
          required
          disabled={loading}
        />
      </div>
      <div className="mb-4">
        <label htmlFor="register-lastname" className="block text-sm font-medium text-gray-700 mb-1 font-sans">
          Sobrenome
        </label>
        <input
          type="text"
          id="register-lastname"
          className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={lastName}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => setLastName(e.target.value)}
          required
          disabled={loading}
        />
      </div>
      <div className="mb-4">
        <label htmlFor="register-email" className="block text-sm font-medium text-gray-700 mb-1 font-sans">
          Email
        </label>
        <input
          type="email"
          id="register-email"
          className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={email}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
          required
          disabled={loading}
        />
      </div>
      <div className="mb-4">
        <label htmlFor="register-password" className="block text-sm font-medium text-gray-700 mb-1 font-sans">
          Senha
        </label>
        <input
          type="password"
          id="register-password"
          className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={password}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
          required
          disabled={loading}
        />
      </div>
      
      {/* 7. Botão Primário (Cor Primária #4A90E2) */}
      <button 
        type="submit" 
        className="w-full py-3 px-4 font-semibold text-white bg-blue-600 rounded-md shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:bg-gray-400"
        disabled={loading}
      >
        {loading ? 'A registar...' : 'Registar'}
      </button>
    </form>
  );
};

export default RegisterForm;