import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom'; // Importamos o Link
import { getBoards, type BoardResponse } from '../services/boardService';

const DashboardPage: React.FC = () => {
  const [boards, setBoards] = useState<BoardResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // A lógica de 'fetch' permanece a mesma
    const fetchBoards = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await getBoards();
        setBoards(data);
      } catch (err) {
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError('Ocorreu um erro desconhecido.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchBoards();
  }, []);

  if (loading) {
    return <div className="p-6 text-lg">Carregando projetos...</div>;
  }
  if (error) {
    // Cor de Erro (Spec 6.2)
    return <div className="p-6 text-lg text-red-600">Erro: {error}</div>;
  }

  return (
    // 1. Layout da Página: Fundo Neutro #F5F5F5, padding 24px (base-8)
    <div className="p-6 bg-neutral-50 min-h-screen">
      
      {/* 2. Cabeçalho da Página (Spec 4.2.2) */}
      <div className="flex justify-between items-center mb-6">
        {/* Fonte Roboto, cor Neutra #333 */}
        <h1 className="text-3xl font-bold text-gray-800">Meu Dashboard</h1>
        
        {/* Botão "Criar Novo Projeto" (Spec 4.2.2) */}
        {/* TODO: Ligar isto a um formulário/modal para criar um novo board */}
        <button
          // Cor Primária #4A90E2 (Spec 6.2)
          className="px-4 py-2 font-semibold text-white bg-blue-600 rounded-md shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        >
          + Criar Novo Projeto
        </button>
      </div>

      <h2 className="text-xl font-semibold text-gray-700 mb-4">Meus Projetos</h2>

      {/* 3. Grelha de Projetos (Spec 4.2.2) */}
      {boards.length === 0 ? (
        <p className="text-gray-600">Você ainda não tem projetos.</p>
      ) : (
        // Layout em grelha responsivo (Base-8 gap)
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          
          {boards.map(board => (
            // 4. Cartão do Projeto (é um Link)
            <Link
              key={board.id}
              to={`/board/${board.id}`}
              // Estilo do cartão: branco, padding, cantos, sombra, borda, transição
              className="block p-6 bg-white rounded-lg shadow border border-gray-200 transition-all hover:shadow-md hover:border-blue-400"
            >
              {/* Nome (Fonte Roboto, cor Neutra #333) */}
              <h3 className="text-lg font-semibold text-gray-800 mb-2">{board.name}</h3>
              {/* Descrição (Fonte Open Sans, cor Neutra #666) */}
              <p className="text-sm text-gray-600 font-sans mb-4 line-clamp-3">
                {board.description}
              </p>
              {/* Data (cor Neutra #999) */}
              <small className="text-xs text-gray-500">
                Atualizado em: {new Date(board.updatedAt).toLocaleString()}
              </small>
            </Link>
          ))}
          
        </div>
      )}
    </div>
  );
};

export default DashboardPage;