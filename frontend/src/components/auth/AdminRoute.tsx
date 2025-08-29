import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import LoadingSpinner from '../common/LoadingSpinner';

const AdminRoute = ({ allowedRoles }) => {
    const { isAuthenticated, userRole, isLoading } = useAuth();

    if (isLoading) {
        return <LoadingSpinner />;
    }

    // Se o usuário está autenticado
    if (isAuthenticated) {
        if (allowedRoles.includes(userRole) == "ADMIN") {
            return <Outlet />;
        } else {
            // Se não tem permissão, redireciona para a home
            return <Navigate to="/home" replace />;
        }
    } else {
        // Se não está autenticado, redireciona para o login
        return <Navigate to="/login" replace />;
    }
};

export default AdminRoute;