# Accenture-final-project

nome do projeto: TaskFlow

## Objetivo do projeto

- Desevolver um MVP
- Implementar funcionalidades de criação, atribuição e acompanhamento de tarefas

## Funcionalidades do Sistema

- Autenticação e Perfis
  - Cadastro e login de usuários (email/senha, autenticação jwt)
  - Perfis: administrador, gerente e colaborador
  - recuperação de senha via e-mail
- Gestão de Tarefas
  - criar, editar, excluir e atribuir tarefas
  - campos: título, descrição, responsável, status, prioridade, data de entrega
  - visualização em lista e em quadro (kanban simples)
  - filtro e busca por status, responsavel e prioridade
- Gestão de Projetos
  - criar projetos e associar tarefas a eles
  - definir nome, descrição e equipe envolvida.
  - painel com resumo do progresso (percentual concluido)
- Colaboração
  -  comentários em tarefas
  -  notificações básicas (ex: "tarefa atribuída a você")
  -  Histórico de alterações (log de atividade por tarefa).
- Dashboard e Relatórios
  - painel principal com contagem de tarefas por status (A Fazer / Em Progresso / Concluído)
  - Relatório por usuário e por projeto
  - Exportação simples (CSV ou PDF)
- Configurações e Acesso
  - Gestão de usuário (somente para administrador)
  - permissões básicas por papel.
  - configurações de idioma e tema (opcional).

## Ferramentas

Servidor:
- Java
- JDK 21 SE

Cliente:
- React
