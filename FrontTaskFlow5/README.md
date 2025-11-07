# Task Flow Project - React + Vite

Aplicação completa de gerenciamento de tarefas construída com React, Vite e Tailwind CSS, seguindo a especificação UI/UX do Task Flow Project.

## Tecnologias

- **React 18** - Biblioteca UI
- **Vite** - Build tool e dev server
- **TypeScript** - Type safety
- **Tailwind CSS** - Styling
- **React Router** - Navegação
- **Axios** - HTTP client para API
- **Radix UI** - Componentes acessíveis
- **Sonner** - Toast notifications

## Estrutura do Projeto

\`\`\`
src/
├── components/
│   ├── ui/              # Componentes base (Button, Input, Dialog, etc)
│   ├── auth/            # Componentes de autenticação
│   ├── dashboard/       # Componentes do dashboard
│   └── project/         # Componentes do board Kanban
├── contexts/            # React Context (AuthContext)
├── services/            # Serviços de API (auth, projects, tasks)
├── pages/               # Páginas da aplicação
├── lib/                 # Utilitários e dados mock
├── App.tsx              # Configuração de rotas
└── main.tsx             # Entry point
\`\`\`

## Funcionalidades

### Autenticação
- Login com validação inline
- Registro de usuário
- Recuperação de senha
- Estados de loading e feedback visual
- Gerenciamento de sessão com JWT
- Rotas protegidas

### Dashboard
- Grid responsivo de projetos
- Busca em tempo real
- Criação de novos projetos
- Cards com progresso visual
- Integração com API

### Kanban Board
- 4 colunas de status (A Fazer, Em Progresso, Em Revisão, Concluído)
- Drag-and-drop nativo
- Filtros e busca de tarefas
- Criação rápida de tarefas
- Integração com API

### Detalhes da Tarefa
- Visualização completa de informações
- Edição inline de campos
- Sistema de comentários
- Log de atividades
- Badges de prioridade e status

## Design System

### Cores
- **Primary**: #4A90E2 (Azul)
- **Secondary**: #50E3C2 (Verde água)
- **Accent**: #F5A623 (Laranja)
- **Success**: #7ED321 (Verde)
- **Warning**: #F8E71C (Amarelo)
- **Error**: #D0021B (Vermelho)

### Tipografia
- **Primary**: Roboto
- **Secondary**: Open Sans
- **Monospace**: Fira Code

### Ícones
- Material Icons

## Instalação

\`\`\`bash
# Instalar dependências
npm install

# Copiar arquivo de ambiente
cp .env.example .env

# Configurar URL da API no .env
VITE_API_BASE_URL=http://localhost:3333/api

# Iniciar servidor de desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview do build
npm run preview
\`\`\`

## Integração com Backend

Este projeto está **pronto para integração com backend**. Consulte o arquivo `BACKEND_INTEGRATION.md` para:

- Estrutura completa de endpoints esperada
- Modelos de dados (User, Project, Task)
- Como usar os serviços de API
- Guia passo a passo de integração
- Troubleshooting

### Configuração Rápida

1. Configure a variável de ambiente no arquivo `.env`:
\`\`\`env
VITE_API_BASE_URL=http://seu-backend.com/api
\`\`\`

2. Seu backend deve implementar os endpoints documentados em `BACKEND_INTEGRATION.md`

3. O frontend automaticamente incluirá JWT tokens nos headers das requisições

## Serviços de API

O projeto inclui serviços prontos para integração:

- `authService` - Login, registro, logout, recuperação de senha
- `projectService` - CRUD de projetos, gerenciamento de membros
- `taskService` - CRUD de tarefas, movimentação, comentários

Todos os serviços incluem tratamento de erros e feedback visual automático.

## Acessibilidade

- WCAG 2.1 AA compliance
- Navegação por teclado
- Labels e ARIA attributes
- Contraste adequado de cores
- Focus indicators visíveis

## Otimizações para Desktop

- Componentes otimizados para desktop
- Layout fixo com padding de 32px
- Grid de projetos com 3 colunas
- Elementos de UI sempre visíveis
- Sem breakpoints mobile

## Rotas

- `/` - Redireciona para login
- `/login` - Página de login
- `/register` - Página de registro
- `/forgot-password` - Recuperação de senha
- `/dashboard` - Dashboard com projetos (protegida)
- `/project/:id` - Board Kanban do projeto (protegida)

## Próximos Passos

- Conectar com backend Node.js/Express
- Implementar WebSocket para atualizações em tempo real
- Adicionar testes unitários e E2E
- PWA support
- Modo escuro
- Responsividade mobile

## Documentação Adicional

- `BACKEND_INTEGRATION.md` - Guia completo de integração com backend
- `.env.example` - Exemplo de variáveis de ambiente

## Licença

MIT
