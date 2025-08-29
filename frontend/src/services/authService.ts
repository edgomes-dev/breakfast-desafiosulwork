import { User, LoginResult } from '../types/auth';

const API_URL = 'http://localhost:8080/api/v1';

// Exemplo de serviço para integração com API real
export const authService = {
    async login(cpf: string, password: string): Promise<LoginResult> {
        try {
            const response = await fetch(`${API_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ cpf, password }),
            });

            if (!response.ok) {
                throw new Error('Credenciais inválidas');
            }

            const userData: User = await response.json();
            return { success: true, user: userData };
        } catch (error) {
            return {
                success: false,
                error: error instanceof Error ? error.message : 'Erro desconhecido'
            };
        }
    },

    async logout(token: string): Promise<void> {
        await fetch(`${API_URL}/auth/logout`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });
    },

    async validateToken(token: string): Promise<boolean> {
        const response = await fetch(`${API_URL}/auth/validate`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });

        return response.ok;
    }
};