// boardService.ts
// Serviço simples usando fetch; adapte para axios se preferir

const API_URL = 'http://localhost:8080';

// --- Tipos de Dados (Baseados nos DTOs Java) ---

export interface CardResponse {
  id: string;
  title: string;
  description?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  dueDate?: string;
  completionPercentage: number;
  columnId: string;
  assigneeId?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ColumnResponse {
  id: string;
  name: string;
  order: number;
  boardId: string;
  cards: CardResponse[];
}

export interface BoardResponse {
  id: string;
  name: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
  columns: ColumnResponse[];
}

// Request para criação de cartão
export interface CardCreateRequest {
  title: string;
  description?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  dueDate?: string;
  completionPercentage: number;
  columnId: string;
  assigneeId?: string;
}

// Request para atualização: todos opcionais (update parcial)
export interface CardUpdateRequest {
  title?: string;
  description?: string;
  priority?: 'LOW' | 'MEDIUM' | 'HIGH';
  dueDate?: string;
  completionPercentage?: number;
  columnId?: string;
  assigneeId?: string;
}

// --- Funções de acesso à API ---

const getAuthHeaders = () => {
  const token = localStorage.getItem('userToken');
  if (!token) throw new Error('Usuário não autenticado.');
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };
};

export const getBoards = async (): Promise<BoardResponse[]> => {
  const response = await fetch(`${API_URL}/boards`, {
    method: 'GET',
    headers: getAuthHeaders(),
  });
  if (!response.ok) throw new Error('Falha ao buscar os projetos.');
  return response.json() as Promise<BoardResponse[]>;
};

export const getBoardById = async (boardId: string): Promise<BoardResponse> => {
  const response = await fetch(`${API_URL}/boards/${boardId}`, {
    method: 'GET',
    headers: getAuthHeaders(),
  });
  if (!response.ok) throw new Error('Falha ao buscar o projeto.');
  return response.json() as Promise<BoardResponse>;
};

// alias para compatibilidade com o BoardPage que chama getBoard(...)
export const getBoard = getBoardById;

export const updateCard = async (cardId: string, cardData: CardUpdateRequest): Promise<CardResponse> => {
  const response = await fetch(`${API_URL}/cards/${cardId}`, {
    method: 'PUT',
    headers: getAuthHeaders(),
    body: JSON.stringify(cardData),
  });

  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      throw new Error('Sessão expirada ou sem permissão.');
    }
    throw new Error('Falha ao atualizar o cartão.');
  }

  return response.json() as Promise<CardResponse>;
};

export const createCard = async (cardData: CardCreateRequest): Promise<CardResponse> => {
  const response = await fetch(`${API_URL}/cards`, {
    method: 'POST',
    headers: getAuthHeaders(),
    body: JSON.stringify(cardData),
  });

  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      throw new Error('Sessão expirada ou sem permissão.');
    }
    throw new Error('Falha ao criar o cartão.');
  }

  return response.json() as Promise<CardResponse>;
};
