import React, { FormEvent, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Coffee } from 'lucide-react';
import { api } from '../lib/api';
import { useAuth } from '../context/AuthContext';


export default function Login() {
    const [cpf, setCpf] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    const location = useLocation();
    const { login } = useAuth();
    const from = location.state?.from?.pathname || '/';

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!cpf || !password) {
            setError("Preencha todos os campos.")
            return;
        }

        setLoading(true);
        setError('');

        const res = await login(cpf, password);

        if (res.success) {
            navigate(from, { replace: true });
        } else {
            setError(res.error || "CPF ou Senha incorretos!")
        }
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-sulwork-dark-blue to-sulwork-light-blue flex items-center justify-center p-4">
            <Card className="w-full max-w-md">
                <CardHeader className="text-center">
                    <div className="flex justify-center mb-4">
                        <div className="bg-sulwork-light-blue p-3 rounded-full">
                            <Coffee className="h-8 w-8 text-white" />
                        </div>
                    </div>
                    <CardTitle className="text-2xl font-bold text-sulwork-dark-blue">
                        Café da Manhã Sulwork
                    </CardTitle>
                    <CardDescription>
                        Entre na sua conta para organizar o seu café da manhã na empresa
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="cpf">CPF</Label>
                            <Input
                                id="cpf"
                                name="cpf"
                                type="text"
                                placeholder="Digite seu CPF"
                                value={cpf}
                                onChange={(e: any) => setCpf(e.target.value)}
                                disabled={loading}
                                className="focus:ring-sulwork-light-blue focus:border-sulwork-light-blue"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="password">Senha</Label>
                            <Input
                                id="password"
                                name="password"
                                type="password"
                                placeholder="Digite sua senha"
                                value={password}
                                onChange={(e: any) => setPassword(e.target.value)}
                                disabled={loading}
                                className="focus:ring-sulwork-light-blue focus:border-sulwork-light-blue"
                            />
                        </div>

                        {error && <p className='text-red-500 text-sm'>{error}</p>}

                        <Button
                            type="submit"
                            className="w-full bg-sulwork-light-blue hover:bg-sulwork-dark-blue transition-colors"
                            disabled={loading}
                        >
                            {loading ? 'Entrando...' : 'Entrar'}
                        </Button>
                    </form>

                    <div className="mt-6 text-center">
                        <p className="text-sm text-gray-600">
                            Não tem uma conta?{' '}
                            <Link
                                to="/cadastro"
                                className="text-sulwork-light-blue hover:text-sulwork-dark-blue font-medium"
                            >
                                Cadastre-se aqui
                            </Link>
                        </p>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
};
