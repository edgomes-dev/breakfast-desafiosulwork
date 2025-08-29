import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, AuthContextType, AuthProviderProps, LoginResult } from '../types/auth';
import { api } from '../lib/api';

// Interface para a resposta do login da API
interface LoginResponse {
    user: User;
    token: string;
}
/// Exportei aqui
export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth deve ser usado dentro de um AuthProvider');
    }
    return context;
};

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        // Verificar se há usuário logado no localStorage ao inicializar
        const initializeAuth = async () => {
            const storedUser = localStorage.getItem('user');

            if (storedUser) {
                const userData: User = JSON.parse(storedUser);

                // Verificar se o token ainda é válido
                try {
                    // Você pode fazer uma requisição para validar o token
                    // ou simplesmente definir o usuário (mais comum)
                    setUser(userData);
                } catch (error) {
                    // Token inválido, limpar storage
                    localStorage.removeItem('user');
                }
            }

            setLoading(false);
        };

        initializeAuth();
    }, []);

    const login = async (cpf: string, password: string): Promise<LoginResult> => {
        try {
            // Chamada real para a API
            const response = await api.post<LoginResponse>('/auth/login', {
                cpf,
                password
            });

            /********************************************* */
            /****************Olhar aqio***************************** */
            /********************************************* */
            const { user: userData, token } = response.data;

            // Adicionar token ao objeto do usuário
            const userWithToken = {
                ...userData,
                token
            };

            setUser(userWithToken);
            localStorage.setItem('user', JSON.stringify(userWithToken));

            return { success: true };
        } catch (error: any) {
            let errorMessage = 'Erro ao fazer login';

            if (error.response) {
                errorMessage = error.response.data.message || errorMessage;
            } else if (error.request) {
                errorMessage = 'Erro de conexão. Verifique sua internet.';
            }

            return {
                success: false,
                error: errorMessage
            };
        }
    };

    const logout = async (): Promise<void> => {
        try {
            await api.post('/auth/logout');
        } catch (error) {
            console.error('Erro no logout:', error);
        } finally {
            setUser(null);
            localStorage.removeItem('user');
        }
    };

    const isAuthenticated = (): boolean => {
        return !!user;
    };

    const isAdmin = (): boolean => {
        return user?.role === 'ADMIN';
    };

    const value: AuthContextType = {
        user,
        login,
        logout,
        isAuthenticated,
        isAdmin,
        loading
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};