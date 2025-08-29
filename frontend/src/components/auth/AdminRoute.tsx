import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { Navigate } from 'react-router-dom';
import LoadingSpinner from '../common/LoadingSpinner';

interface AdminRouteProps {
    children: React.ReactNode;
}

const AdminRoute: React.FC<AdminRouteProps> = ({ children }) => {
    const { isAuthenticated, isAdmin, loading } = useAuth();

    if (loading) {
        return <LoadingSpinner />;
    }

    if (!isAuthenticated()) {
        return <Navigate to="/login" />;
    }

    if (!isAdmin()) {
        return <Navigate to="/home" />;
    }

    return <>{children}</>;
};

export default AdminRoute;