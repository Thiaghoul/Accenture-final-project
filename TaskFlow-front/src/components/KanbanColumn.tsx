import React, { useState } from 'react';
import { type ColumnResponse, type CardResponse, createCard, type CardCreateRequest,  } from '../services/boardService';
import KanbanCard from './KanbanCard';
import { useDroppable } from '@dnd-kit/core';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';

interface KanbanColumnProps {
  column: ColumnResponse;
  onCardAdded: (newCard: CardResponse) => void;
}

const KanbanColumn: React.FC<KanbanColumnProps> = ({ column, onCardAdded }) => {
  const { isOver, setNodeRef } = useDroppable({ id: column.id });
  const cardIds = column.cards.map(card => card.id);
  
  const [isAdding, setIsAdding] = useState(false);
  const [newCardTitle, setNewCardTitle] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmitNewCard = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!newCardTitle.trim()) return;
    setLoading(true);

    const cardData: CardCreateRequest = {
      title: newCardTitle,
      columnId: column.id,
      priority: 'MEDIUM', // Padrão
      completionPercentage: 0, // Padrão
    };

    try {
      const newCard = await createCard(cardData);
      onCardAdded(newCard);
      setNewCardTitle("");
      setIsAdding(false);
    } catch (error) {
      console.error("Erro ao criar cartão:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    // 1. Classes aplicadas à coluna principal
    <div
      ref={setNodeRef}
      // Cor de fundo Neutra (#F5F5F5), largura fixa, cantos, padding e layout flexível
      // O 'isOver' dá um feedback visual (bg-gray-200) ao arrastar
      className={`w-72 flex-shrink-0 bg-neutral-100 rounded-lg p-4 flex flex-col h-full ${isOver ? 'bg-gray-200' : ''}`}
    >
      {/* 2. Título da Coluna (Fonte 'Roboto', H3, cor Neutra #333333) */}
      <h3 className="text-lg font-semibold text-gray-800 mb-4">{column.name}</h3>
      
      {/* 3. Lista de Cartões (permite scroll vertical) */}
      <SortableContext items={cardIds} strategy={verticalListSortingStrategy}>
        <div className="flex-grow overflow-y-auto pr-1">
          {column.cards.map(card => (
            <KanbanCard key={card.id} card={card} />
          ))}
        </div>
      </SortableContext>

      {/* 4. Formulário "Adicionar Tarefa" estilizado com Tailwind */}
      {isAdding ? (
        <form onSubmit={handleSubmitNewCard} className="mt-2">
          <textarea
            value={newCardTitle}
            onChange={(e) => setNewCardTitle(e.target.value)}
            placeholder="Insira um título para este cartão..."
            autoFocus
            // Input Field (Spec 5.2.2)
            className="w-full p-3 text-sm border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <div className="mt-2 flex items-center gap-2">
            {/* Botão Primário (Spec 5.2.1) - Cor Primária #4A90E2 */}
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:bg-gray-400"
            >
              {loading ? "A guardar..." : "Guardar"}
            </button>
            <button
              type="button"
              onClick={() => setIsAdding(false)}
              className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-md"
            >
              Cancelar
            </button>
          </div>
        </form>
      ) : (
        // Botão "Adicionar Tarefa"
        <button
          onClick={() => setIsAdding(true)}
          className="mt-2 p-2 w-full text-left text-sm text-gray-600 hover:bg-gray-200 rounded-md"
        >
          + Adicionar Tarefa
        </button>
      )}
    </div>
  );
};

export default KanbanColumn;