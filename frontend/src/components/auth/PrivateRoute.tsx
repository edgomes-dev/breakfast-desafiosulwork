import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import LoadingSpinner from '../common/LoadingSpinner';

const PrivateRoute = ({ allowedRoles }) => {
    const { isAuthenticated, userRole, isLoading } = useAuth();

    if (isLoading) {
        return <LoadingSpinner />;
    }

    if (isAuthenticated) {
        return <Outlet />;
    } else {
        return <Navigate to="/login" replace />;
    }
};

export default PrivateRoute;