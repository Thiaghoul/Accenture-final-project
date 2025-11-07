// Define a URL base da nossa API
const API_URL = 'http://localhost:8080';

// Podemos definir os tipos de resposta esperados
interface AuthResponse {
  token: string;
  email: string;
  // Adicione outros campos do UserRegisterResponse se necessário
}

interface UserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export interface UserLoginRequest {
  email: string;
  password: string;
}

export interface UserRegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}
/**
 * Registra um novo usuário.
 * Os dados correspondem ao DTO UserRequest.java.
 */
export const register = async (firstName: string, lastName: string, email: string, password: string): Promise<UserResponse> => {
  const response = await fetch(`${API_URL}/users/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      firstName,
      lastName,
      email,
      password,
      roles: ['USER'], // Define um 'role' padrão
    }),
  });

  if (!response.ok) {
    throw new Error('Falha no registro');
  }

  return response.json() as Promise<UserResponse>;
};


/**
 * Autentica um usuário.
 * Os dados correspondem ao DTO UserLoginRequest.java.
 */
export const login = async (email: string, password: string): Promise<AuthResponse> => {
  const response = await fetch(`${API_URL}/users/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      email,
      password,
    }),
  });

  if (!response.ok) {
    throw new Error('Email ou senha inválidos');
  }

  const data = await response.json() as AuthResponse;
  
  if (data.token) {
    localStorage.setItem('userToken', data.token); 
  }

  return data;
};