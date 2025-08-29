export interface User {
    id: number;
    cpf: string;
    name: string;
    role: 'ADMIN' | 'USER';
    token: string;
}

export interface AuthContextType {
    user: User | null;
    login: (cpf: string, password: string) => Promise<LoginResult>;
    logout: () => void;
    isAuthenticated: () => boolean;
    isAdmin: () => boolean;
    loading: boolean;
}

export interface LoginResult {
    success: boolean;
    error?: string;
}

export interface AuthProviderProps {
    children: React.ReactNode;
}