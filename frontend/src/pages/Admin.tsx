import React, { useState, useEffect, createContext, useContext, useReducer } from 'react';

// Shadcn/ui icons replaced with lucide-react (loaded from CDN)
import { Shield, Calendar, Users, Coffee, Plus, Edit, Trash2, Check, X, ArrowLeft, Settings } from 'lucide-react';

// --- MOCK API and UTILS (No changes needed, but now in the same file) ---

// Mock API to simulate backend requests
const adminAPI = {
    getEvents: () => new Promise(resolve => setTimeout(() => resolve({
        data: [
            { id: '1', date: '2025-09-05', title: 'Café de Aniversário Sulwork', participants: [{ id: 'p1' }], items: [{ id: 'i1' }] },
            { id: '2', date: '2025-09-12', title: 'Café Semanal', participants: [], items: [] },
        ]
    }), 500)),
    getParticipants: () => new Promise(resolve => setTimeout(() => resolve({
        data: [
            { id: 'p1', name: 'João Silva', email: 'joao.s@email.com', brought: false },
            { id: 'p2', name: 'Maria Souza', email: 'maria.s@email.com', brought: true },
        ]
    }), 500)),
    getItems: () => new Promise(resolve => setTimeout(() => resolve({
        data: [
            { id: 'i1', name: 'Café', category: 'bebidas' },
            { id: 'i2', name: 'Pão de Queijo', category: 'paes' },
            { id: 'i3', name: 'Bolo de Chocolate', category: 'doces' },
        ]
    }), 500)),
    createEvent: (data) => new Promise(resolve => setTimeout(() => resolve({ data: { ...data, id: Date.now().toString() } }), 500)),
    deleteEvent: (id) => new Promise(resolve => setTimeout(() => resolve({ data: { id } }), 500)),
    createItem: (data) => new Promise(resolve => setTimeout(() => resolve({ data: { ...data, id: Date.now().toString() } }), 500)),
    updateItem: (id, data) => new Promise(resolve => setTimeout(() => resolve({ data: { ...data, id } }), 500)),
    deleteItem: (id) => new Promise(resolve => setTimeout(() => resolve({ data: { id } }), 500)),
    markParticipantPresence: (id, brought) => new Promise(resolve => setTimeout(() => resolve({ data: { id, brought } }), 500)),
};

// Mock the useNavigate hook since react-router-dom is not available
const mockUseNavigate = () => (path) => console.log(`Navigating to: ${path}`);

// --- APP CONTEXT ---

// Define the initial state for the context
const initialState = {
    isAuthenticated: true, // We assume the user is authenticated for this demo
    user: {
        name: 'Admin User',
        isAdmin: true, // Crucial for rendering the admin dashboard
        email: 'admin@sulwork.com',
    },
    adminData: {
        events: [],
        participants: [],
        items: [],
    },
    isLoading: false,
    error: null,
};

// Reducer function to manage state changes
const appReducer = (state, action) => {
    switch (action.type) {
        case 'SET_LOADING':
            return { ...state, isLoading: action.payload };
        case 'SET_ERROR':
            return { ...state, error: action.payload };
        case 'SET_ADMIN_EVENTS':
            return { ...state, adminData: { ...state.adminData, events: action.payload } };
        case 'SET_ADMIN_PARTICIPANTS':
            return { ...state, adminData: { ...state.adminData, participants: action.payload } };
        case 'SET_ADMIN_ITEMS':
            return { ...state, adminData: { ...state.adminData, items: action.payload } };
        default:
            return state;
    }
};

const AppContext = createContext();

const AppProvider = ({ children }) => {
    const [state, dispatch] = useReducer(appReducer, initialState);

    const setError = (message) => {
        dispatch({ type: 'SET_ERROR', payload: message });
    };

    const setLoading = (loading) => {
        dispatch({ type: 'SET_LOADING', payload: loading });
    };

    const value = { state, dispatch, setError, setLoading };

    return (
        <AppContext.Provider value={value}>
            {children}
        </AppContext.Provider>
    );
};

const useAppContext = () => useContext(AppContext);

// --- COMPONENT REPLACEMENT (Custom components to replicate shadcn/ui) ---

const Card = ({ children, className = '' }) => (
    <div className={`bg-white rounded-lg shadow-md border border-gray-200 ${className}`}>
        {children}
    </div>
);

const CardHeader = ({ children, className = '' }) => (
    <div className={`p-6 border-b border-gray-200 ${className}`}>
        {children}
    </div>
);

const CardTitle = ({ children, className = '' }) => (
    <h3 className={`text-lg font-bold text-gray-800 ${className}`}>
        {children}
    </h3>
);

const CardDescription = ({ children, className = '' }) => (
    <p className={`text-sm text-gray-500 mt-1 ${className}`}>
        {children}
    </p>
);

const CardContent = ({ children, className = '' }) => (
    <div className={`p-6 ${className}`}>
        {children}
    </div>
);

const Button = ({ children, onClick, className = '', variant = 'default', size = 'default', disabled }) => {
    let baseClasses = 'inline-flex items-center justify-center rounded-md font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none';

    let sizeClasses = '';
    if (size === 'sm') sizeClasses = 'h-8 px-3 text-sm';
    else sizeClasses = 'h-10 px-4 py-2';

    let variantClasses = '';
    if (variant === 'default') variantClasses = 'bg-blue-600 text-white hover:bg-blue-700 focus-visible:ring-blue-500';
    else if (variant === 'destructive') variantClasses = 'bg-red-600 text-white hover:bg-red-700 focus-visible:ring-red-500';
    else if (variant === 'outline') variantClasses = 'border border-gray-300 text-gray-700 hover:bg-gray-100 focus-visible:ring-gray-400';
    else if (variant === 'ghost') variantClasses = 'hover:bg-gray-100';

    return (
        <button
            onClick={onClick}
            className={`${baseClasses} ${sizeClasses} ${variantClasses} ${className}`}
            disabled={disabled}
        >
            {children}
        </button>
    );
};

const Input = ({ id, type = 'text', value, onChange, placeholder, disabled, min }) => (
    <input
        id={id}
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        disabled={disabled}
        min={min}
        className="flex h-10 w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
    />
);

const Label = ({ htmlFor, children }) => (
    <label htmlFor={htmlFor} className="text-sm font-medium leading-none mb-1 block text-gray-700">
        {children}
    </label>
);

const Alert = ({ variant, children, className = '' }) => {
    const variantClasses = variant === 'destructive'
        ? 'bg-red-100 border border-red-400 text-red-700'
        : 'bg-gray-100 border border-gray-300 text-gray-700';
    return (
        <div className={`p-4 rounded-md ${variantClasses} ${className}`}>
            {children}
        </div>
    );
};

const AlertDescription = ({ children }) => <p className="text-sm">{children}</p>;

const Badge = ({ variant, children }) => {
    const variantClasses = variant === 'secondary'
        ? 'bg-gray-200 text-gray-800'
        : 'bg-gray-100 text-gray-600';
    return (
        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${variantClasses}`}>
            {children}
        </span>
    );
};

const Dialog = ({ open, onOpenChange, children }) => {
    if (!open) return null;
    return (
        <div className="fixed inset-0 z-50 overflow-y-auto" role="dialog" aria-modal="true">
            <div className="flex items-center justify-center min-h-screen px-4 py-8">
                <div className="fixed inset-0 bg-gray-900 bg-opacity-75 transition-opacity" onClick={() => onOpenChange(false)}></div>
                <div className="relative bg-white rounded-lg shadow-xl max-w-lg w-full p-6 transform transition-all">
                    {children}
                </div>
            </div>
        </div>
    );
};

const DialogTrigger = ({ children, asChild }) => {
    if (asChild) {
        return React.cloneElement(children, {
            onClick: () => {
                // This is a simplified trigger logic. The Dialog component will be controlled by state.
            }
        });
    }
    return <button>{children}</button>;
};

const DialogContent = ({ children }) => <div className="space-y-4">{children}</div>;
const DialogHeader = ({ children }) => <div className="space-y-1">{children}</div>;
const DialogTitle = ({ children }) => <h2 className="text-xl font-semibold">{children}</h2>;
const DialogDescription = ({ children }) => <p className="text-sm text-gray-500">{children}</p>;
const DialogFooter = ({ children }) => <div className="flex justify-end space-x-2 mt-4">{children}</div>;

const Select = ({ value, onValueChange, children }) => {
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div className="relative">
            <div
                onClick={() => setIsOpen(!isOpen)}
                className="flex h-10 w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 cursor-pointer"
            >
                {React.Children.map(children, child => child.type === SelectValue && React.cloneElement(child, { value, placeholder: child.props.placeholder }))}
            </div>
            {isOpen && (
                <div className="absolute top-full left-0 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg z-10">
                    {React.Children.map(children, child => child.type === SelectContent && React.cloneElement(child, { onValueChange, setIsOpen }))}
                </div>
            )}
        </div>
    );
};

const SelectTrigger = ({ children }) => {
    const selectedText = React.Children.toArray(children)
        .find(child => child.type === SelectValue)?.props.value || 'Selecione uma categoria';
    return (
        <div className="flex items-center justify-between">
            <span>{selectedText}</span>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-chevrons-up-down h-4 w-4 opacity-50">
                <path d="m7 15 5 5 5-5" /><path d="m7 9 5-5 5 5" />
            </svg>
        </div>
    );
};

const SelectValue = ({ value, placeholder }) => {
    return <span className={!value ? 'text-gray-400' : ''}>{value || placeholder}</span>;
};

const SelectContent = ({ onValueChange, setIsOpen, children }) => (
    <>
        {React.Children.map(children, child => React.cloneElement(child, { onValueChange, setIsOpen }))}
    </>
);

const SelectItem = ({ value, children, onValueChange, setIsOpen }) => (
    <div
        className="px-3 py-2 cursor-pointer hover:bg-gray-100"
        onClick={() => {
            onValueChange(value);
            setIsOpen(false);
        }}
    >
        {children}
    </div>
);

const Tabs = ({ value, onValueChange, children }) => (
    <div>
        {React.Children.map(children, child => {
            if (child.type === TabsList) return React.cloneElement(child, { value, onValueChange });
            return React.cloneElement(child, { activeTab: value });
        })}
    </div>
);

const TabsList = ({ value, onValueChange, children, className = '' }) => (
    <div className={`flex bg-gray-100 rounded-lg p-1 ${className}`}>
        {React.Children.map(children, child => React.cloneElement(child, { onValueChange, activeTab: value }))}
    </div>
);

const TabsTrigger = ({ value, children, onValueChange, activeTab }) => (
    <button
        onClick={() => onValueChange(value)}
        className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors ${activeTab === value ? 'bg-white shadow text-gray-900' : 'text-gray-500 hover:text-gray-700'}`}
    >
        {children}
    </button>
);

const TabsContent = ({ value, activeTab, children, className = '' }) => {
    if (value !== activeTab) return null;
    return <div className={`mt-4 ${className}`}>{children}</div>;
};

// --- ADMIN COMPONENT (Refactored) ---

const Admin = () => {
    const navigate = mockUseNavigate();
    const { state, dispatch, setError, setLoading } = useAppContext();

    const [activeTab, setActiveTab] = useState('dashboard');
    const [isCreateEventOpen, setIsCreateEventOpen] = useState(false);
    const [isCreateItemOpen, setIsCreateItemOpen] = useState(false);
    const [editingItem, setEditingItem] = useState(null);

    const [eventForm, setEventForm] = useState({
        date: '',
        title: '',
    });

    const [itemForm, setItemForm] = useState({
        name: '',
        category: '',
    });

    useEffect(() => {
        // This is a simplified check for the demo, assuming the user is always authenticated
        if (!state.isAuthenticated) {
            navigate('/login');
            return;
        }

        if (!state.user?.isAdmin) {
            navigate('/');
            return;
        }

        loadAdminData();
    }, [state.isAuthenticated, state.user]);

    const loadAdminData = async () => {
        setLoading(true);
        try {
            const [eventsResponse, participantsResponse, itemsResponse] = await Promise.all([
                adminAPI.getEvents(),
                adminAPI.getParticipants(),
                adminAPI.getItems(),
            ]);

            dispatch({ type: 'SET_ADMIN_EVENTS', payload: eventsResponse.data });
            dispatch({ type: 'SET_ADMIN_PARTICIPANTS', payload: participantsResponse.data });
            dispatch({ type: 'SET_ADMIN_ITEMS', payload: itemsResponse.data });
        } catch (error) {
            const message = error.response?.data?.message || 'Erro ao carregar dados administrativos.';
            setError(message);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateEvent = async () => {
        try {
            const response = await adminAPI.createEvent({
                date: eventForm.date,
                title: eventForm.title || `Café da Manhã - ${new Date(eventForm.date).toLocaleDateString('pt-BR')}`,
            });

            dispatch({
                type: 'SET_ADMIN_EVENTS',
                payload: [...state.adminData.events, response.data]
            });

            setEventForm({ date: '', title: '' });
            setIsCreateEventOpen(false);
        } catch (error) {
            const message = error.response?.data?.message || 'Erro ao criar evento.';
            setError(message);
        }
    };

    const handleDeleteEvent = async (eventId) => {
        try {
            await adminAPI.deleteEvent(eventId);

            dispatch({
                type: 'SET_ADMIN_EVENTS',
                payload: state.adminData.events.filter(event => event.id !== eventId)
            });
        } catch (error) {
            const message = error.response?.data?.message || 'Erro ao deletar evento.';
            setError(message);
        }
    };

    const handleCreateItem = async () => {
        try {
            const response = await adminAPI.createItem(itemForm);

            dispatch({
                type: 'SET_ADMIN_ITEMS',
                payload: [...state.adminData.items, response.data]
            });

            setItemForm({ name: '', category: '' });
            setIsCreateItemOpen(false);
        } catch (error) {
            const message = error.response?.data?.message || 'Erro ao criar item.';
            setError(message);
        }
    };

    const handleUpdateItem = async () => {
        if (!editingItem) return;

        try {
            const response = await adminAPI.updateItem(editingItem.id, itemForm);

            dispatch({
                type: 'SET_ADMIN_ITEMS',
                payload: state.adminData.items.map(item =>
                    item.id === editingItem.id ? response.data : item
                )
            });

            setItemForm({ name: '', category: '' });
            setEditingItem(null);
        } catch (error) {
            const message = error.response?.data?.message || 'Erro ao atualizar item.';
            setError(message);
        }
    };

    const handleDeleteItem = async (itemId) => {
        try {
            await adminAPI.deleteItem(itemId);

            dispatch({
                type: 'SET_ADMIN_ITEMS',
                payload: state.adminData.items.filter(item => item.id !== itemId)
            });
        } catch (error) {
            const message = error.response?.data?.message || 'Erro ao deletar item.';
            setError(message);
        }
    };

    const handleMarkPresence = async (participantId, brought) => {
        try {
            await adminAPI.markParticipantPresence(participantId, brought);

            dispatch({
                type: 'SET_ADMIN_PARTICIPANTS',
                payload: state.adminData.participants.map(participant =>
                    participant.id === participantId
                        ? { ...participant, brought }
                        : participant
                )
            });
        } catch (error) {
            const message = error.response?.data?.message || 'Erro ao marcar presença.';
            setError(message);
        }
    };

    const startEditItem = (item) => {
        setItemForm({ name: item.name, category: item.category });
        setEditingItem(item);
    };

    const cancelEditItem = () => {
        setItemForm({ name: '', category: '' });
        setEditingItem(null);
    };

    const getTomorrowDate = () => {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        return tomorrow.toISOString().split('T')[0];
    };

    if (state.isLoading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <Settings className="h-12 w-12 text-blue-500 mx-auto mb-4 animate-spin" />
                    <p className="text-gray-600">Carregando painel administrativo...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 font-sans">
            {/* Header */}
            <header className="bg-white shadow-sm border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <div className="flex items-center">
                            <Button
                                variant="ghost"
                                onClick={() => navigate('/')}
                                className="mr-4"
                            >
                                <ArrowLeft className="h-4 w-4 mr-2" />
                                Voltar
                            </Button>
                            <div className="bg-blue-600 p-2 rounded-lg mr-3">
                                <Shield className="h-6 w-6 text-white" />
                            </div>
                            <div>
                                <h1 className="text-xl font-bold text-gray-900">
                                    Painel Administrativo
                                </h1>
                                <p className="text-sm text-gray-600">
                                    Gestão do café da manhã
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </header>

            {/* Main Content */}
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {state.error && (
                    <Alert variant="destructive" className="mb-6">
                        <AlertDescription>{state.error}</AlertDescription>
                    </Alert>
                )}

                <Tabs value={activeTab} onValueChange={setActiveTab}>
                    <TabsList className="grid w-full grid-cols-4">
                        <TabsTrigger value="dashboard">Dashboard</TabsTrigger>
                        <TabsTrigger value="events">Eventos</TabsTrigger>
                        <TabsTrigger value="participants">Participantes</TabsTrigger>
                        <TabsTrigger value="items">Itens</TabsTrigger>
                    </TabsList>

                    {/* Dashboard Tab */}
                    <TabsContent value="dashboard" className="space-y-6">
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                            <Card>
                                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                                    <CardTitle className="text-sm font-medium">Total de Eventos</CardTitle>
                                    <Calendar className="h-4 w-4 text-gray-400" />
                                </CardHeader>
                                <CardContent>
                                    <div className="text-2xl font-bold">{state.adminData.events.length}</div>
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                                    <CardTitle className="text-sm font-medium">Participantes Ativos</CardTitle>
                                    <Users className="h-4 w-4 text-gray-400" />
                                </CardHeader>
                                <CardContent>
                                    <div className="text-2xl font-bold">{state.adminData.participants.length}</div>
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                                    <CardTitle className="text-sm font-medium">Itens Cadastrados</CardTitle>
                                    <Coffee className="h-4 w-4 text-gray-400" />
                                </CardHeader>
                                <CardContent>
                                    <div className="text-2xl font-bold">{state.adminData.items.length}</div>
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                                    <CardTitle className="text-sm font-medium">Próximos Eventos</CardTitle>
                                    <Calendar className="h-4 w-4 text-gray-400" />
                                </CardHeader>
                                <CardContent>
                                    <div className="text-2xl font-bold">
                                        {state.adminData.events.filter(event =>
                                            new Date(event.date) >= new Date()
                                        ).length}
                                    </div>
                                </CardContent>
                            </Card>
                        </div>

                        <Card>
                            <CardHeader>
                                <CardTitle>Próximos Eventos</CardTitle>
                                <CardDescription>
                                    Eventos de café da manhã programados
                                </CardDescription>
                            </CardHeader>
                            <CardContent>
                                {state.adminData.events
                                    .filter(event => new Date(event.date) >= new Date())
                                    .slice(0, 5)
                                    .map((event) => (
                                        <div key={event.id} className="flex items-center justify-between p-3 border rounded-lg mb-2">
                                            <div>
                                                <h4 className="font-medium">{new Date(event.date).toLocaleDateString('pt-BR', {
                                                    weekday: 'long',
                                                    year: 'numeric',
                                                    month: 'long',
                                                    day: 'numeric'
                                                })}</h4>
                                                <p className="text-sm text-gray-600">{event.participants?.length || 0} participantes</p>
                                            </div>
                                            <Badge variant="outline">
                                                {event.items?.length || 0} itens
                                            </Badge>
                                        </div>
                                    ))}
                            </CardContent>
                        </Card>
                    </TabsContent>

                    {/* Events Tab */}
                    <TabsContent value="events" className="space-y-6">
                        <div className="flex justify-between items-center">
                            <h2 className="text-2xl font-bold text-gray-900">Gestão de Eventos</h2>
                            <Dialog open={isCreateEventOpen} onOpenChange={setIsCreateEventOpen}>
                                <DialogTrigger asChild>
                                    <Button className="bg-blue-600 hover:bg-blue-700" children={undefined} onClick={undefined} disabled={undefined}>
                                        <Plus className="h-4 w-4 mr-2" />
                                        Criar Evento
                                    </Button>
                                </DialogTrigger>
                                <DialogContent>
                                    <DialogHeader>
                                        <DialogTitle>Criar Novo Evento</DialogTitle>
                                        <DialogDescription>
                                            Crie um novo evento de café da manhã
                                        </DialogDescription>
                                    </DialogHeader>
                                    <div className="space-y-4">
                                        <div>
                                            <Label htmlFor="event-date">Data</Label>
                                            <Input
                                                id="event-date"
                                                type="date"
                                                value={eventForm.date}
                                                onChange={(e) => setEventForm({ ...eventForm, date: e.target.value })}
                                                min={getTomorrowDate()} placeholder={undefined} disabled={undefined} />
                                        </div>
                                        <div>
                                            <Label htmlFor="event-title">Título (opcional)</Label>
                                            <Input
                                                id="event-title"
                                                placeholder="Café da Manhã Especial"
                                                value={eventForm.title}
                                                onChange={(e) => setEventForm({ ...eventForm, title: e.target.value })}
                                            />
                                        </div>
                                    </div>
                                    <DialogFooter>
                                        <Button variant="outline" onClick={() => setIsCreateEventOpen(false)} children={undefined} disabled={undefined}>
                                            Cancelar
                                        </Button>
                                        <Button
                                            onClick={handleCreateEvent}
                                            disabled={!eventForm.date}
                                            className="bg-blue-600 hover:bg-blue-700"
                                        >
                                            Criar Evento
                                        </Button>
                                    </DialogFooter>
                                </DialogContent>
                            </Dialog>
                        </div>

                        <div className="grid gap-4">
                            {state.adminData.events.map((event) => (
                                <Card key={event.id}>
                                    <CardHeader>
                                        <div className="flex justify-between items-start">
                                            <div>
                                                <CardTitle>
                                                    {new Date(event.date).toLocaleDateString('pt-BR', {
                                                        weekday: 'long',
                                                        year: 'numeric',
                                                        month: 'long',
                                                        day: 'numeric'
                                                    })}
                                                </CardTitle>
                                                <CardDescription>
                                                    {event.participants?.length || 0} participantes • {event.items?.length || 0} itens
                                                </CardDescription>
                                            </div>
                                            <Button
                                                variant="destructive"
                                                size="sm"
                                                onClick={() => handleDeleteEvent(event.id)}
                                            >
                                                <Trash2 className="h-4 w-4" />
                                            </Button>
                                        </div>
                                    </CardHeader>
                                </Card>
                            ))}
                        </div>
                    </TabsContent>

                    {/* Participants Tab */}
                    <TabsContent value="participants" className="space-y-6">
                        <h2 className="text-2xl font-bold text-gray-900">Gestão de Participantes</h2>

                        <Card>
                            <CardHeader>
                                <CardTitle>Participantes do Café de Hoje</CardTitle>
                                <CardDescription>
                                    Marque quem trouxe os itens programados
                                </CardDescription>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-3">
                                    {state.adminData.participants.map((participant) => (
                                        <div key={participant.id} className="flex items-center justify-between p-3 border rounded-lg">
                                            <div className="flex items-center">
                                                <div className="h-10 w-10 bg-blue-600 rounded-full flex items-center justify-center text-white font-medium mr-3">
                                                    {participant.name.charAt(0).toUpperCase()}
                                                </div>
                                                <div>
                                                    <h4 className="font-medium">{participant.name}</h4>
                                                    <p className="text-sm text-gray-600">{participant.email}</p>
                                                </div>
                                            </div>
                                            <div className="flex items-center space-x-2">
                                                <Button
                                                    size="sm"
                                                    variant={participant.brought ? "default" : "outline"}
                                                    onClick={() => handleMarkPresence(participant.id, true)}
                                                    className={participant.brought ? "bg-green-600 hover:bg-green-700" : ""}
                                                >
                                                    <Check className="h-4 w-4" />
                                                </Button>
                                                <Button
                                                    size="sm"
                                                    variant={participant.brought === false ? "default" : "outline"}
                                                    onClick={() => handleMarkPresence(participant.id, false)}
                                                    className={participant.brought === false ? "bg-red-600 hover:bg-red-700" : ""}
                                                >
                                                    <X className="h-4 w-4" />
                                                </Button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </CardContent>
                        </Card>
                    </TabsContent>

                    {/* Items Tab */}
                    <TabsContent value="items" className="space-y-6">
                        <div className="flex justify-between items-center">
                            <h2 className="text-2xl font-bold text-gray-900">Gestão de Itens</h2>
                            <Button
                                className="bg-blue-600 hover:bg-blue-700"
                                onClick={() => {
                                    setEditingItem(null);
                                    setItemForm({ name: '', category: '' });
                                    setIsCreateItemOpen(true);
                                }}
                            >
                                <Plus className="h-4 w-4 mr-2" />
                                Adicionar Item
                            </Button>
                            <Dialog open={isCreateItemOpen} onOpenChange={setIsCreateItemOpen}>
                                <DialogTrigger asChild>
                                </DialogTrigger>
                                <DialogContent>
                                    <DialogHeader>
                                        <DialogTitle>
                                            {editingItem ? 'Editar Item' : 'Adicionar Novo Item'}
                                        </DialogTitle>
                                        <DialogDescription>
                                            {editingItem ? 'Edite as informações do item' : 'Adicione um novo item para o café da manhã'}
                                        </DialogDescription>
                                    </DialogHeader>
                                    <div className="space-y-4">
                                        <div>
                                            <Label htmlFor="item-name">Nome do Item</Label>
                                            <Input
                                                id="item-name"
                                                placeholder="Ex: Pão francês, Queijo, Café..."
                                                value={itemForm.name}
                                                onChange={(e) => setItemForm({ ...itemForm, name: e.target.value })}
                                            />
                                        </div>
                                        <div>
                                            <Label htmlFor="item-category">Categoria</Label>
                                            <Select
                                                value={itemForm.category}
                                                onValueChange={(value) => setItemForm({ ...itemForm, category: value })}
                                            >
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Selecione uma categoria" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    <SelectItem value="paes">Pães</SelectItem>
                                                    <SelectItem value="frios">Frios</SelectItem>
                                                    <SelectItem value="bebidas">Bebidas</SelectItem>
                                                    <SelectItem value="doces">Doces</SelectItem>
                                                    <SelectItem value="frutas">Frutas</SelectItem>
                                                    <SelectItem value="outros">Outros</SelectItem>
                                                </SelectContent>
                                            </Select>
                                        </div>
                                    </div>
                                    <DialogFooter>
                                        <Button
                                            variant="outline"
                                            onClick={() => {
                                                setIsCreateItemOpen(false);
                                                cancelEditItem();
                                            }}
                                        >
                                            Cancelar
                                        </Button>
                                        <Button
                                            onClick={editingItem ? handleUpdateItem : handleCreateItem}
                                            disabled={!itemForm.name || !itemForm.category}
                                            className="bg-blue-600 hover:bg-blue-700"
                                        >
                                            {editingItem ? 'Atualizar' : 'Adicionar'}
                                        </Button>
                                    </DialogFooter>
                                </DialogContent>
                            </Dialog>
                        </div>

                        <div className="grid gap-4">
                            {state.adminData.items.map((item) => (
                                <Card key={item.id}>
                                    <CardContent className="flex items-center justify-between p-4">
                                        <div className="flex items-center">
                                            <div className="mr-4">
                                                <h4 className="font-medium">{item.name}</h4>
                                                <Badge variant="secondary">{item.category}</Badge>
                                            </div>
                                        </div>
                                        <div className="flex items-center space-x-2">
                                            <Button
                                                size="sm"
                                                variant="outline"
                                                onClick={() => {
                                                    startEditItem(item);
                                                    setIsCreateItemOpen(true);
                                                }}
                                            >
                                                <Edit className="h-4 w-4" />
                                            </Button>
                                            <Button
                                                size="sm"
                                                variant="destructive"
                                                onClick={() => handleDeleteItem(item.id)}
                                            >
                                                <Trash2 className="h-4 w-4" />
                                            </Button>
                                        </div>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>
                    </TabsContent>
                </Tabs>
            </main>
        </div>
    );
};

// Main app component to manage the overall view
const App = () => {
    const [view, setView] = useState('admin');

    if (view === 'admin') {
        return <Admin />;
    }

    // Placeholder for other views
    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center">
            <h1 className="text-xl">Outra Página</h1>
        </div>
    );
};

// Root component that includes the provider
const RootApp = () => (
    <AppProvider>
        <App />
    </AppProvider>
);

export default RootApp;