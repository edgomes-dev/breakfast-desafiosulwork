import React, { useState, createContext, useContext, useEffect } from 'react';
import { Coffee, UserPlus } from 'lucide-react';

// --- CPF Validation and Formatting Utilities ---
// These functions are now self-contained within this file.
const cleanCPF = (cpf) => cpf.replace(/[^\d]/g, '');

const formatCPF = (cpf) => {
    const cleaned = cleanCPF(cpf);
    if (cleaned.length <= 3) return cleaned;
    if (cleaned.length <= 6) return cleaned.replace(/(\d{3})/, '$1.');
    if (cleaned.length <= 9) return cleaned.replace(/(\d{3})(\d{3})/, '$1.$2.');
    return cleaned.replace(/(\d{3})(\d{3})(\d{3})/, '$1.$2.$3-');
};

const validateCPF = (cpf) => {
    const cleaned = cleanCPF(cpf);
    if (cleaned.length !== 11 || /^(\d)\1+$/.test(cleaned)) return false;

    let sum = 0;
    let remainder;
    for (let i = 1; i <= 9; i++) {
        sum += parseInt(cleaned.substring(i - 1, i)) * (11 - i);
    }
    remainder = (sum * 10) % 11;
    if ((remainder === 10) || (remainder === 11)) remainder = 0;
    if (remainder !== parseInt(cleaned.substring(9, 10))) return false;

    sum = 0;
    for (let i = 1; i <= 10; i++) {
        sum += parseInt(cleaned.substring(i - 1, i)) * (12 - i);
    }
    remainder = (sum * 10) % 11;
    if ((remainder === 10) || (remainder === 11)) remainder = 0;
    if (remainder !== parseInt(cleaned.substring(10, 11))) return false;

    return true;
};

// --- Mock API for Authentication ---
// This simulates the backend API call to register a user.
const authAPI = {
    register: (data) => {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                // Simulate a CPF conflict error
                if (data.cpf === '11111111111') {
                    reject({ response: { status: 409, data: { message: 'CPF já cadastrado no sistema.' } } });
                } else {
                    // Simulate a successful registration
                    const mockUser = { name: data.name, cpf: data.cpf };
                    const mockToken = 'mock-auth-token-123';
                    resolve({ data: { user: mockUser, token: mockToken } });
                }
            }, 1500); // Simulate network latency
        });
    },
};

// --- App Context for State Management ---
// We create a simple context and provider to handle global state like loading and errors.
const AppContext = createContext();

const AppProvider = ({ children }) => {
    const [state, setState] = useState({
        user: null,
        token: null,
        isLoading: false,
        error: null,
    });

    const login = (user, token) => {
        setState(prev => ({ ...prev, user, token }));
    };

    const setError = (error) => {
        setState(prev => ({ ...prev, error }));
    };

    const setLoading = (isLoading) => {
        setState(prev => ({ ...prev, isLoading }));
    };

    const value = { state, login, setError, setLoading };

    return (
        <AppContext.Provider value={value}>
            {children}
        </AppContext.Provider>
    );
};

const useAppContext = () => useContext(AppContext);

// --- Cadastro Component (from the original code) ---
// This is the core component logic, adapted to our single-file app.
const Cadastro = ({ onNavigate }) => {
    const { login, setError, setLoading, state } = useAppContext();

    const [formData, setFormData] = useState({
        name: '',
        cpf: '',
        password: '',
        confirmPassword: '',
    });
    const [localErrors, setLocalErrors] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;

        let processedValue = value;

        if (name === 'cpf') {
            processedValue = formatCPF(value);
        }

        setFormData(prev => ({
            ...prev,
            [name]: processedValue,
        }));

        if (localErrors[name]) {
            setLocalErrors(prev => ({ ...prev, [name]: '' }));
        }
        if (state.error) setError(null);
    };

    const validateForm = () => {
        const errors = {};

        if (!formData.name.trim()) {
            errors.name = 'Nome é obrigatório.';
        } else if (formData.name.trim().length < 2) {
            errors.name = 'Nome deve ter pelo menos 2 caracteres.';
        }

        if (!formData.cpf) {
            errors.cpf = 'CPF é obrigatório.';
        } else if (!validateCPF(formData.cpf)) {
            errors.cpf = 'CPF inválido. Verifique os números digitados.';
        }

        if (!formData.password) {
            errors.password = 'Senha é obrigatória.';
        } else if (formData.password.length < 6) {
            errors.password = 'Senha deve ter pelo menos 6 caracteres.';
        }

        if (!formData.confirmPassword) {
            errors.confirmPassword = 'Confirmação de senha é obrigatória.';
        } else if (formData.password !== formData.confirmPassword) {
            errors.confirmPassword = 'Senhas não coincidem.';
        }

        setLocalErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        setLoading(true);
        setLocalErrors({});

        try {
            const response = await authAPI.register({
                name: formData.name.trim(),
                cpf: cleanCPF(formData.cpf),
                password: formData.password,
            });

            const { user, token } = response.data;
            login(user, token);
            onNavigate('login'); // Simulate navigation to login page
        } catch (error) {
            if (error.response?.status === 409) {
                setLocalErrors({ cpf: 'CPF já cadastrado no sistema.' });
            } else {
                const message = error.response?.data?.message || 'Erro ao criar conta. Tente novamente.';
                setError(message);
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-700 to-blue-500 flex items-center justify-center p-4 font-sans">
            <div className="w-full max-w-md bg-white rounded-lg shadow-xl overflow-hidden">
                <div className="p-8 text-center">
                    <div className="flex justify-center mb-4">
                        <div className="bg-blue-500 p-3 rounded-full">
                            <UserPlus className="h-8 w-8 text-white" />
                        </div>
                    </div>
                    <h1 className="text-2xl font-bold text-blue-800">
                        Criar Conta
                    </h1>
                    <p className="text-sm text-gray-600 mt-2">
                        Cadastre-se para participar do café da manhã da Sulwork
                    </p>
                </div>
                <div className="p-8 pt-0">
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="space-y-2">
                            <label htmlFor="name" className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
                                Nome Completo
                            </label>
                            <input
                                id="name"
                                name="name"
                                type="text"
                                placeholder="Digite seu nome completo"
                                value={formData.name}
                                onChange={handleChange}
                                disabled={state.isLoading}
                                className="flex h-10 w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
                            />
                            {localErrors.name && (
                                <p className="text-sm text-red-600">{localErrors.name}</p>
                            )}
                        </div>

                        <div className="space-y-2">
                            <label htmlFor="cpf" className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
                                CPF
                            </label>
                            <input
                                id="cpf"
                                name="cpf"
                                type="text"
                                placeholder="000.000.000-00"
                                value={formData.cpf}
                                onChange={handleChange}
                                disabled={state.isLoading}
                                maxLength={14}
                                className="flex h-10 w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
                            />
                            {localErrors.cpf && (
                                <p className="text-sm text-red-600">{localErrors.cpf}</p>
                            )}
                        </div>

                        <div className="space-y-2">
                            <label htmlFor="password" className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
                                Senha
                            </label>
                            <input
                                id="password"
                                name="password"
                                type="password"
                                placeholder="Mínimo 6 caracteres"
                                value={formData.password}
                                onChange={handleChange}
                                disabled={state.isLoading}
                                className="flex h-10 w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
                            />
                            {localErrors.password && (
                                <p className="text-sm text-red-600">{localErrors.password}</p>
                            )}
                        </div>

                        <div className="space-y-2">
                            <label htmlFor="confirmPassword" className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
                                Confirmar Senha
                            </label>
                            <input
                                id="confirmPassword"
                                name="confirmPassword"
                                type="password"
                                placeholder="Digite a senha novamente"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                disabled={state.isLoading}
                                className="flex h-10 w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
                            />
                            {localErrors.confirmPassword && (
                                <p className="text-sm text-red-600">{localErrors.confirmPassword}</p>
                            )}
                        </div>

                        {state.error && (
                            <div className="p-4 rounded-md border border-red-500 bg-red-50">
                                <p className="text-sm text-red-600">{state.error}</p>
                            </div>
                        )}

                        <button
                            type="submit"
                            className="w-full h-10 bg-blue-500 text-white font-medium rounded-md hover:bg-blue-700 transition-colors disabled:cursor-not-allowed disabled:opacity-50"
                            disabled={state.isLoading}
                        >
                            {state.isLoading ? 'Criando conta...' : 'Criar Conta'}
                        </button>
                    </form>

                    <div className="mt-6 text-center">
                        <p className="text-sm text-gray-600">
                            Já tem uma conta?{' '}
                            <button
                                onClick={() => onNavigate('login')}
                                className="text-blue-500 hover:text-blue-700 font-medium"
                            >
                                Entre aqui
                            </button>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

// --- Login Component (for simulation) ---
// This is a placeholder to demonstrate the navigation.
const Login = ({ onNavigate }) => {
    const { state } = useAppContext();
    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-700 to-blue-500 flex flex-col items-center justify-center p-4 font-sans text-center">
            <div className="flex justify-center mb-4">
                <div className="bg-white p-3 rounded-full">
                    <Coffee className="h-8 w-8 text-blue-500" />
                </div>
            </div>
            <h1 className="text-3xl font-bold text-white mb-2">Login</h1>
            {state.user && (
                <p className="text-lg text-white mb-4">
                    Bem-vindo, {state.user.name}!
                </p>
            )}
            <p className="text-white text-lg mb-8">
                Simulação de página de login.
            </p>
            <button
                onClick={() => onNavigate('cadastro')}
                className="px-6 py-3 bg-white text-blue-500 font-medium rounded-md hover:bg-gray-200 transition-colors"
            >
                Voltar para Cadastro
            </button>
        </div>
    );
};

// --- Main App Component ---
// This component manages the view state (Cadastro or Login).
const App = () => {
    const [view, setView] = useState('cadastro');
    const { state } = useAppContext();

    // If a user is logged in, automatically navigate to the 'login' view
    useEffect(() => {
        if (state.user) {
            setView('login');
        }
    }, [state.user]);

    const handleNavigate = (page) => {
        setView(page);
    };

    return (
        <div className="min-h-screen">
            {view === 'cadastro' ? (
                <Cadastro onNavigate={handleNavigate} />
            ) : (
                <Login onNavigate={handleNavigate} />
            )}
        </div>
    );
};

// New root component that includes the provider
const RootApp = () => (
    <AppProvider>
        <App />
    </AppProvider>
);

export default RootApp;