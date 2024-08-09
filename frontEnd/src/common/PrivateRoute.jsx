import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '/src/common/AuthContext';

const PrivateRoute = ({ element }) => {
  const { isAuthenticated } = useAuth();

  return !isAuthenticated ? element : <Navigate to="/admin/" />;
};

export default PrivateRoute;