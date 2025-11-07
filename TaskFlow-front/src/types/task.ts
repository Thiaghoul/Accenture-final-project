/**
 * Define a estrutura de uma Tarefa (Card).
 * Baseado nos campos do seu esqueleto
 */
export interface Task {
  id: string;
  title: string;
  description?: string;
  status: string; // Ex: 'A Fazer', 'Em Progresso', 'Concluído'
  priority: 'Baixa' | 'Média' | 'Alta';
  dueDate?: string;
  assigneeId?: string;
  projectId: string;
  // Adicione outros campos conforme sua API
}

/**
 * Define a estrutura de uma Coluna Kanban.
 */
export interface Column {
  id: string;
  title: string; // Ex: 'A Fazer'
  tasks: Task[];
}