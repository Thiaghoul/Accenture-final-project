/**
 * Define a estrutura de um objeto Projeto,
 * conforme os campos do projeto no seu esqueleto.
 *
 */
export interface Project {
  id: string;
  name: string;
  description: string;
  // Adicione outros campos que sua API retornar, ex:
  // createdAt: string;
  // percentCompleted: number;
}