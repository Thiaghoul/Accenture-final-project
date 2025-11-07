import React from 'react';
import { useParams, Link } from 'react-router-dom';
import TaskKanban from '../features/task/TaskKanban';
// Importe o 'project-board-header.tsx' do V0 aqui

/**
 * Página que exibe os detalhes de um único projeto,
 * incluindo o quadro Kanban.
 */
const ProjectDetailPage: React.FC = () => {
  // Pega o {id} da URL (ex: /projects/123)
  const { id } = useParams<{ id: string }>();

  if (!id) {
    return <div className="p-8">ID do projeto não encontrado.</div>;
  }

  return (
    <div className="p-8">
      {/* TODO: Adicionar o <ProjectBoardHeader /> do V0 aqui */}
      <header className="mb-6">
        <Link to="/" className="text-primary-600 hover:underline">&larr; Voltar para o Dashboard</Link>
        <h1 className="text-3xl font-bold mt-2">Nome do Projeto (ID: {id})</h1>
        {/* Você pode buscar os detalhes do projeto aqui também */}
      </header>
      
      {/* Renderiza o quadro kanban, passando o ID do projeto */}
      <TaskKanban projectId={id} />
    </div>
  );
};

export default ProjectDetailPage;