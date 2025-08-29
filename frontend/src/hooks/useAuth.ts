import { useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

function useAuth() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [userRole, setUserRole] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');

        if (token) {
            try {
                const decodedToken = jwtDecode(token);
                const currentTime = Date.now() / 1000; // Converte para segundos

                // Verifica se o token expirou
                if (decodedToken.exp < currentTime) {
                    console.log("Token expirado. Removendo do localStorage.");
                    localStorage.removeItem('token');
                    setIsAuthenticated(false);
                    setUserRole(null);
                } else {
                    // Token válido, define o estado
                    setIsAuthenticated(true);
                    // Supondo que o seu token tenha a chave 'role'
                    setUserRole(decodedToken.role);
                }
            } catch (error) {
                // Trata erro de token inválido (mal formatado)
                console.error("Erro ao decodificar token:", error);
                localStorage.removeItem('token');
                setIsAuthenticated(false);
                setUserRole(null);
            }
        } else {
            // Nenhum token encontrado
            setIsAuthenticated(false);
            setUserRole(null);
        }

        setIsLoading(false);

    }, []); // Executa apenas uma vez ao montar o componente

    // Retorna o estado de autenticação e o papel do usuário
    return { isAuthenticated, userRole, isLoading };
}

export default useAuth;