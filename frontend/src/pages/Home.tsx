import React, { useEffect, useState } from 'react';

// Tailwind CSS é importado aqui
// Você precisará de uma configuração Tailwind no seu projeto para que as classes funcionem.

// Função para simular a navegação
const useNavigate = () => {
    return (path) => {
        console.log(`Navegando para: ${path}`);
    };
};

// Funções para simular as chamadas de API com dados mockados
const mockApi = {
    getTodayBreakfast: () => {
        return new Promise(resolve => {
            setTimeout(() => {
                const today = new Date();
                resolve({
                    data: {
                        date: today.toISOString(),
                        items: [
                            { id: '1', name: 'Pão de Queijo', category: 'Salgado', brought: true, reservedBy: 'user2', reservedByName: 'João Silva' },
                            { id: '2', name: 'Bolo de Fubá', category: 'Doce', brought: false, reservedBy: null, reservedByName: null },
                            { id: '3', name: 'Café', category: 'Bebida', brought: true, reservedBy: 'user1', reservedByName: 'Usuário de Teste' },
                        ],
                        participants: [
                            { id: 'user1', name: 'Usuário de Teste' },
                            { id: 'user2', name: 'João Silva' },
                        ]
                    }
                });
            }, 1000); // Simula o tempo de carregamento
        });
    },
    getTomorrowBreakfast: () => {
        return new Promise(resolve => {
            setTimeout(() => {
                const tomorrow = new Date();
                tomorrow.setDate(tomorrow.getDate() + 1);
                resolve({
                    data: {
                        date: tomorrow.toISOString(),
                        items: [
                            { id: '4', name: 'Frutas', category: 'Saudável', reservedBy: null, reservedByName: null },
                            { id: '5', name: 'Ovos Mexidos', category: 'Salgado', reservedBy: 'user3', reservedByName: 'Maria Souza' },
                            { id: '6', name: 'Suco de Laranja', category: 'Bebida', reservedBy: null, reservedByName: null },
                            { id: '7', name: 'Waffles', category: 'Doce', reservedBy: null, reservedByName: null },
                            { id: '8', name: 'Iogurte', category: 'Laticínio', reservedBy: 'user1', reservedByName: 'Usuário de Teste' },
                        ],
                        participants: [
                            { id: 'user1', name: 'Usuário de Teste' },
                            { id: 'user3', name: 'Maria Souza' },
                        ]
                    }
                });
            }, 1500); // Simula o tempo de carregamento
        });
    },
    reserveItem: (itemId) => {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                const isReserved = Math.random() > 0.1; // 90% de chance de sucesso
                if (isReserved) {
                    resolve({
                        data: { message: `Item ${itemId} reservado com sucesso.` }
                    });
                } else {
                    reject({
                        response: { data: { message: 'Erro ao reservar item: item já reservado ou indisponível.' } }
                    });
                }
            }, 500);
        });
    }
};

export default function Home() {
    const navigate = useNavigate();
    const [user] = useState({ id: 'user1', name: 'Usuário de Teste', isAdmin: true });
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [todayBreakfast, setTodayBreakfast] = useState(null);
    const [tomorrowBreakfast, setTomorrowBreakfast] = useState(null);
    const [searchQuery, setSearchQuery] = useState('');
    const [filteredItems, setFilteredItems] = useState([]);

    // Função para carregar os dados
    const loadBreakfastData = async () => {
        setIsLoading(true);
        setError(null);
        try {
            const [todayResponse, tomorrowResponse] = await Promise.all([
                mockApi.getTodayBreakfast(),
                mockApi.getTomorrowBreakfast(),
            ]);
            setTodayBreakfast(todayResponse.data);
            setTomorrowBreakfast(tomorrowResponse.data);
        } catch (err) {
            console.error('Erro ao carregar dados:', err);
            const message = err.response?.data?.message || 'Erro ao carregar dados do café da manhã.';
            setError(message);
        } finally {
            setIsLoading(false);
        }
    };

    // Efeito para carregar os dados na primeira renderização
    useEffect(() => {
        loadBreakfastData();
    }, []);

    // Efeito para filtrar os itens quando a pesquisa ou os dados mudam
    useEffect(() => {
        if (tomorrowBreakfast?.items) {
            const filtered = tomorrowBreakfast.items.filter(item =>
                item.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                item.category.toLowerCase().includes(searchQuery.toLowerCase())
            );
            setFilteredItems(filtered);
        }
    }, [searchQuery, tomorrowBreakfast]);

    // Função para simular a reserva de um item
    const handleReserveItem = async (itemId) => {
        setError(null);
        try {
            await mockApi.reserveItem(itemId);
            // Atualiza o estado localmente para refletir a reserva
            setTomorrowBreakfast(prev => ({
                ...prev,
                items: prev.items.map(item =>
                    item.id === itemId
                        ? { ...item, reservedBy: user.id, reservedByName: user.name }
                        : item
                )
            }));
        } catch (err) {
            const message = err.response?.data?.message || 'Erro ao reservar item.';
            setError(message);
        }
    };

    // Função para simular o logout
    const handleLogout = () => {
        console.log('Usuário desconectado.');
        // navigate('/login'); // Você pode descomentar isso se usar o react-router-dom
        setError('Desconectado com sucesso.');
    };

    // Funções de formatação de data
    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('pt-BR', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    };

    const getTodayDate = () => new Date().toLocaleDateString('pt-BR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
    const getTomorrowDate = () => {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        return tomorrow.toLocaleDateString('pt-BR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
    };

    if (isLoading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center font-sans">
                <div className="text-center p-8 bg-white rounded-xl shadow-lg">
                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-12 w-12 text-indigo-500 mx-auto mb-4 animate-pulse">
                        <path d="M10 2c-.2 0-.4.1-.6.2C5.9 3.1 3 6.6 3 10.5 3 14.5 5.9 18 9.4 18.8c.2.1.4.2.6.2h4c.2 0 .4-.1.6-.2.1-.1.2-.2.4-.4.8-1 1.6-1.9 2.5-2.8.2-.2.5-.4.7-.6.5-.5.9-1.2 1.3-1.8.3-.5.5-1.1.5-1.8 0-3.9-2.9-7.4-6.4-8.3-.2-.1-.4-.2-.6-.2z" />
                        <path d="M10 2v16h4V2" />
                    </svg>
                    <p className="text-gray-600 font-medium text-lg">Carregando café da manhã...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 font-sans text-gray-800">
            {/* Header */}
            <header className="bg-white shadow-sm border-b border-gray-100">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-20">
                        <div className="flex items-center">
                            <div className="bg-indigo-600 p-3 rounded-xl mr-3 shadow-md">
                                <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-6 w-6 text-white">
                                    <path d="M10 2c-.2 0-.4.1-.6.2C5.9 3.1 3 6.6 3 10.5 3 14.5 5.9 18 9.4 18.8c.2.1.4.2.6.2h4c.2 0 .4-.1.6-.2.1-.1.2-.2.4-.4.8-1 1.6-1.9 2.5-2.8.2-.2.5-.4.7-.6.5-.5.9-1.2 1.3-1.8.3-.5.5-1.1.5-1.8 0-3.9-2.9-7.4-6.4-8.3-.2-.1-.4-.2-.6-.2z" />
                                    <path d="M10 2v16h4V2" />
                                </svg>
                            </div>
                            <div>
                                <h1 className="text-2xl font-bold text-indigo-900">
                                    Café da Manhã
                                </h1>
                                <p className="text-sm text-gray-500 mt-1">
                                    Bem-vindo, {user?.name}
                                </p>
                            </div>
                        </div>
                        <div className="flex items-center space-x-4">
                            {user?.isAdmin && (
                                <button
                                    onClick={() => navigate('/admin')}
                                    className="px-4 py-2 border-2 border-indigo-600 text-indigo-600 font-medium rounded-full shadow-sm hover:bg-indigo-50 transition-colors flex items-center"
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-4 w-4 mr-2">
                                        <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.23.46a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.23.46a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.23-.46a2 2 0 0 0-.73-2.73l-.15-.1a2 2 0 0 1-1-1.72v-.51a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.23-.46a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z" />
                                        <circle cx="12" cy="12" r="3" />
                                    </svg>
                                    Admin
                                </button>
                            )}
                            <button
                                onClick={handleLogout}
                                className="px-4 py-2 border-2 border-gray-300 text-gray-700 font-medium rounded-full shadow-sm hover:bg-gray-100 transition-colors flex items-center"
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-4 w-4 mr-2">
                                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                                    <polyline points="16 17 21 12 16 7" />
                                    <line x1="21" x2="9" y1="12" y2="12" />
                                </svg>
                                Sair
                            </button>
                        </div>
                    </div>
                </div>
            </header>

            {/* Main Content */}
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {error && (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-xl relative mb-6" role="alert">
                        <span className="block sm:inline">{error}</span>
                    </div>
                )}

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                    {/* Today's Breakfast */}
                    <div className="bg-white rounded-2xl shadow-lg p-6">
                        <div className="flex items-center mb-2">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-5 w-5 mr-2 text-indigo-900">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                                <line x1="16" y1="2" x2="16" y2="6" />
                                <line x1="8" y1="2" x2="8" y2="6" />
                                <line x1="3" y1="10" x2="21" y2="10" />
                            </svg>
                            <h2 className="text-xl font-bold text-indigo-900">
                                Café de Hoje
                            </h2>
                        </div>
                        <p className="text-gray-500 mb-6">
                            {getTodayDate()}
                        </p>
                        {todayBreakfast?.items?.length ? (
                            <div className="space-y-4">
                                {todayBreakfast.items.map((item) => (
                                    <div key={item.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg shadow-sm">
                                        <div className="flex-1">
                                            <h4 className="font-semibold text-gray-900">{item.name}</h4>
                                            <div className="flex items-center mt-1 text-sm">
                                                <span className="bg-gray-200 text-gray-700 px-2 py-1 rounded-full text-xs font-medium mr-2">
                                                    {item.category}
                                                </span>
                                                {item.reservedByName && (
                                                    <span className="text-gray-600">
                                                        Por: {item.reservedByName}
                                                    </span>
                                                )}
                                            </div>
                                        </div>
                                        <div className="flex items-center">
                                            {item.brought ? (
                                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-6 w-6 text-green-500">
                                                    <path d="M22 11.08V12a10 10 0 1 1-5.93-8.08" />
                                                    <polyline points="22 4 12 14.01 9 11.01" />
                                                </svg>
                                            ) : (
                                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-6 w-6 text-yellow-500">
                                                    <circle cx="12" cy="12" r="10" />
                                                    <polyline points="12 6 12 12 16 14" />
                                                </svg>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="text-center py-8 bg-gray-50 rounded-lg">
                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-12 w-12 text-gray-400 mx-auto mb-4">
                                    <path d="M10 2c-.2 0-.4.1-.6.2C5.9 3.1 3 6.6 3 10.5 3 14.5 5.9 18 9.4 18.8c.2.1.4.2.6.2h4c.2 0 .4-.1.6-.2.1-.1.2-.2.4-.4.8-1 1.6-1.9 2.5-2.8.2-.2.5-.4.7-.6.5-.5.9-1.2 1.3-1.8.3-.5.5-1.1.5-1.8 0-3.9-2.9-7.4-6.4-8.3-.2-.1-.4-.2-.6-.2z" />
                                    <path d="M10 2v16h4V2" />
                                </svg>
                                <p className="text-gray-600">Nenhum item programado para hoje.</p>
                            </div>
                        )}
                    </div>

                    {/* Tomorrow's Breakfast */}
                    <div className="bg-white rounded-2xl shadow-lg p-6">
                        <div className="flex items-center mb-2">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-5 w-5 mr-2 text-indigo-900">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                                <line x1="16" y1="2" x2="16" y2="6" />
                                <line x1="8" y1="2" x2="8" y2="6" />
                                <line x1="3" y1="10" x2="21" y2="10" />
                            </svg>
                            <h2 className="text-xl font-bold text-indigo-900">
                                Café de Amanhã
                            </h2>
                        </div>
                        <p className="text-gray-500 mb-4">
                            {getTomorrowDate()}
                        </p>
                        <div className="relative mb-6">
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-4 w-4 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                                <circle cx="11" cy="11" r="8" />
                                <path d="m21 21-4.3-4.3" />
                            </svg>
                            <input
                                type="text"
                                placeholder="Buscar itens..."
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                className="w-full px-4 py-2 pl-10 border border-gray-300 rounded-full focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors"
                            />
                        </div>

                        {filteredItems?.length ? (
                            <div className="space-y-4">
                                {filteredItems.map((item) => (
                                    <div key={item.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg shadow-sm">
                                        <div className="flex-1">
                                            <h4 className="font-semibold text-gray-900">{item.name}</h4>
                                            <div className="flex items-center mt-1 text-sm">
                                                <span className="bg-gray-200 text-gray-700 px-2 py-1 rounded-full text-xs font-medium mr-2">
                                                    {item.category}
                                                </span>
                                                {item.reservedByName && (
                                                    <span className="text-gray-600">
                                                        Reservado por: {item.reservedByName}
                                                    </span>
                                                )}
                                            </div>
                                        </div>
                                        <div>
                                            {item.reservedBy ? (
                                                item.reservedBy === user?.id ? (
                                                    <span className="bg-green-100 text-green-800 px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap">
                                                        Reservado por você
                                                    </span>
                                                ) : (
                                                    <span className="bg-gray-200 text-gray-700 px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap">
                                                        Reservado
                                                    </span>
                                                )
                                            ) : (
                                                <button
                                                    onClick={() => handleReserveItem(item.id)}
                                                    className="px-4 py-2 bg-indigo-600 text-white rounded-full shadow-md hover:bg-indigo-700 transition-colors flex items-center"
                                                >
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-4 w-4 mr-1">
                                                        <line x1="12" y1="5" x2="12" y2="19" />
                                                        <line x1="5" y1="12" x2="19" y2="12" />
                                                    </svg>
                                                    Reservar
                                                </button>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : searchQuery ? (
                            <div className="text-center py-8 bg-gray-50 rounded-lg">
                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-12 w-12 text-gray-400 mx-auto mb-4">
                                    <circle cx="11" cy="11" r="8" />
                                    <path d="m21 21-4.3-4.3" />
                                </svg>
                                <p className="text-gray-600">Nenhum item encontrado para "{searchQuery}"</p>
                            </div>
                        ) : (
                            <div className="text-center py-8 bg-gray-50 rounded-lg">
                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-12 w-12 text-gray-400 mx-auto mb-4">
                                    <path d="M10 2c-.2 0-.4.1-.6.2C5.9 3.1 3 6.6 3 10.5 3 14.5 5.9 18 9.4 18.8c.2.1.4.2.6.2h4c.2 0 .4-.1.6-.2.1-.1.2-.2.4-.4.8-1 1.6-1.9 2.5-2.8.2-.2.5-.4.7-.6.5-.5.9-1.2 1.3-1.8.3-.5.5-1.1.5-1.8 0-3.9-2.9-7.4-6.4-8.3-.2-.1-.4-.2-.6-.2z" />
                                    <path d="M10 2v16h4V2" />
                                </svg>
                                <p className="text-gray-600">Nenhum item programado para amanhã.</p>
                            </div>
                        )}
                    </div>
                </div>

                {/* Participants Summary */}
                {(todayBreakfast?.participants?.length || tomorrowBreakfast?.participants?.length) && (
                    <div className="bg-white rounded-2xl shadow-lg p-6 mt-8">
                        <div className="flex items-center mb-6">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-5 w-5 mr-2 text-indigo-900">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
                                <circle cx="9" cy="7" r="4" />
                                <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
                                <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                            </svg>
                            <h2 className="text-xl font-bold text-indigo-900">
                                Participantes
                            </h2>
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            {todayBreakfast?.participants?.length && (
                                <div>
                                    <h4 className="font-semibold text-gray-900 mb-3 text-lg">Hoje</h4>
                                    <div className="space-y-2">
                                        {todayBreakfast.participants.map((participant) => (
                                            <div key={participant.id} className="flex items-center p-3 bg-gray-50 rounded-lg">
                                                <div className="h-9 w-9 bg-indigo-600 rounded-full flex items-center justify-center text-white text-md font-bold mr-3">
                                                    {participant.name.charAt(0).toUpperCase()}
                                                </div>
                                                <span className="text-sm text-gray-700 font-medium">{participant.name}</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {tomorrowBreakfast?.participants?.length && (
                                <div>
                                    <h4 className="font-semibold text-gray-900 mb-3 text-lg">Amanhã</h4>
                                    <div className="space-y-2">
                                        {tomorrowBreakfast.participants.map((participant) => (
                                            <div key={participant.id} className="flex items-center p-3 bg-gray-50 rounded-lg">
                                                <div className="h-9 w-9 bg-indigo-600 rounded-full flex items-center justify-center text-white text-md font-bold mr-3">
                                                    {participant.name.charAt(0).toUpperCase()}
                                                </div>
                                                <span className="text-sm text-gray-700 font-medium">{participant.name}</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
}