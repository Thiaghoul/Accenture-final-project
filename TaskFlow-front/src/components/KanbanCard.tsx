import React from 'react';
import type { CardResponse } from '../services/boardService';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

interface KanbanCardProps {
  card: CardResponse;
}

const KanbanCard: React.FC<KanbanCardProps> = ({ card }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
  } = useSortable({ id: card.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      // 1. Aqui estão as classes do Tailwind:
      className="bg-white p-4 rounded-md shadow border border-gray-200 mb-2 cursor-grab transition-colors hover:border-blue-500"
      // NOTA: Para usar a sua cor primária exata (#4A90E2)
      // teríamos de a adicionar ao tailwind.config.js. 
      // 'hover:border-blue-500' é um ótimo substituto.
    >
      {/* 2. Também podemos estilizar o texto interno: */}
      {/* Usando text-gray-800 para a cor Neutra #333333 */}
      <h4 className="font-semibold text-sm text-gray-800 mb-1">{card.title}</h4>
      
      {/* Usando text-gray-600 para a cor Neutra #666666 */}
      <p className="text-xs text-gray-600">Prioridade: {card.priority}</p>
      
      {/* TODO: Exibir mais detalhes (assignee, etc.) */}
    </div>
  );
};

export default KanbanCard;