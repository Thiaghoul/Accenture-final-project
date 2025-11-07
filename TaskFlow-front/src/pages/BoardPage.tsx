import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import {
  type BoardResponse,
  type ColumnResponse,
  type CardResponse,
  updateCard,
  type CardUpdateRequest,
  getBoard, // <-- adicione esta função no seu services/boardService
} from '../services/boardService';
import {
  DndContext,
  type DragEndEvent,
  useSensors,
  useSensor,
  PointerSensor,
} from '@dnd-kit/core';
import KanbanColumn from '../components/KanbanColumn';

type BoardPageParams = {
  boardId: string;
};

const BoardPage: React.FC = () => {
  const { boardId } = useParams<BoardPageParams>() as BoardPageParams;

  // estados necessários
  const [board, setBoard] = useState<BoardResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // sensores DnD
  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 5,
      },
    }),
  );

  // fetch do quadro (adapte para sua API)
  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    const fetchBoard = async () => {
      try {
        // getBoard é uma função hipotética que você deve ter em services/boardService
        // Exemplo: export async function getBoard(boardId: string) { ... }
        const response = await getBoard(boardId);
        if (!cancelled) {
          setBoard(response);
        }
      } catch (err) {
        console.error('Erro ao buscar quadro:', err);
        if (!cancelled) setError('Falha ao carregar o quadro.');
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    fetchBoard();

    return () => {
      cancelled = true;
    };
  }, [boardId]);

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    if (!over) return;

    const cardId = String(active.id);
    const newColumnId = String(over.id);

    let sourceColumn: ColumnResponse | undefined;
    let movedCard: CardResponse | undefined;

    board?.columns.forEach(column => {
      const card = column.cards.find(c => c.id === cardId);
      if (card) {
        sourceColumn = column;
        movedCard = card;
      }
    });

    if (!sourceColumn || !movedCard) return;

    if (sourceColumn.id === newColumnId) return;

    const originalBoardState = board;

    // atualiza UI localmente (optimistic)
    setBoard(currentBoard => {
      if (!currentBoard) return null;

      const sourceCards = sourceColumn!.cards.filter(c => c.id !== cardId);
      const targetColumn = currentBoard.columns.find(c => c.id === newColumnId);
      if (!targetColumn) return currentBoard;

      // cria novo array de cards para a coluna alvo
      const targetCards = [
        ...targetColumn.cards,
        { ...movedCard!, columnId: newColumnId }, // manter propriedades do card original
      ];

      const newColumns = currentBoard.columns.map(col => {
        if (col.id === sourceColumn!.id) {
          return { ...col, cards: sourceCards };
        }
        if (col.id === newColumnId) {
          return { ...col, cards: targetCards };
        }
        return col;
      });

      return { ...currentBoard, columns: newColumns };
    });

    // prepara payload para salvar no backend
    const cardData: CardUpdateRequest = {
      title: movedCard.title,
      description: movedCard.description,
      priority: movedCard.priority,
      dueDate: movedCard.dueDate,
      completionPercentage: movedCard.completionPercentage,
      assigneeId: movedCard.assigneeId,
      columnId: newColumnId,
    };

    updateCard(cardId, cardData)
      .then(() => {
        console.log(`Cartão ${cardId} movido para coluna ${newColumnId} com sucesso.`);
      })
      .catch(err => {
        console.error('Falha ao salvar a mudança no backend:', err);
        setError('Não foi possível salvar a mudança. Revertendo.');
        // reverte para o estado anterior
        setBoard(originalBoardState);
      });
  };

  const handleCardAdded = () => {
    // implementar comportamento após criação de card (ex: re-fetch ou atualizar estado)
    // exemplo simples: re-carregar o board
    if (!boardId) return;
    setLoading(true);
    setError(null);
    getBoard(boardId)
      .then((response: React.SetStateAction<BoardResponse | null>) => setBoard(response))
      .catch(() => setError('Falha ao recarregar quadro.'))
      .finally(() => setLoading(false));
  };

  // renderização
  if (loading) return <div>Carregando quadro...</div>;
  if (error) return <div style={{ color: 'red' }}>Erro: {error}</div>;
  if (!board) return <div>Projeto não encontrado.</div>;

  return (
    <DndContext sensors={sensors} onDragEnd={handleDragEnd}>
      <div className="h-screen flex flex-col p-6">
        <h1 className="text-3xl font-bold text-gray-800">{board.name}</h1>
        <p className="text-gray-600 mb-6 font-sans">{board.description}</p>

        <div className="flex-1 flex gap-4 overflow-x-auto pb-4">
          {board.columns
            .slice() // garante não mutar o original em caso de sort
            .sort((a, b) => a.order - b.order)
            .map(column => (
              <KanbanColumn key={column.id} column={column} onCardAdded={handleCardAdded} />
            ))}
        </div>
      </div>
    </DndContext>
  );
};

export default BoardPage;
