import React from 'react';
import type { Task } from '../types/task';

interface TaskCardProps {
  task: Task;
  onClick: (task: Task) => void; // 1. Adiciona a prop onClick
}

/**
 * Card de tarefa atualizado para lidar com o clique
 * e informar o componente pai.
 */
const TaskCard: React.FC<TaskCardProps> = ({ task, onClick }) => {
  return (
    <div
      className="p-4 mb-3 bg-white border border-gray-200 rounded-lg shadow-sm cursor-pointer hover:bg-gray-50"
      onClick={() => onClick(task)} // 2. Dispara o evento de clique
    >
      <h5 className="mb-1 text-md font-bold text-gray-900">{task.title}</h5>
      {/* ... (resto do conteúdo do card) ... */}
    </div>
  );
};

export default TaskCard;