import React, { createContext, useContext, useEffect, useState } from 'react';
import { login as loginService, logout as logoutService, adminLogout as adminLogoutService } from '/src/common/auth/authService';
import instance from '/src/common/auth/axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loginId, setLoginId] = useState();

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    setIsAuthenticated(!!token);
    // if (token != null) {
    //   fetchUserId(token);
    // }
  }, []);

  // const fetchUserId = async (token) => {
  //   try {
  //     const response = await instance.get('http://localhost:8080/getUserId', {
  //       headers: {
  //         "Content-Type": "application/json",
  //         'Authorization': token,
  //       }
  //     })
  //     setLoginId(response.data);
  //   } catch (error) {
  //     console.error('getUserId error', error);
  //   }
  // }

  const login = async (username, password) => {
    console.log(username, password)
    try {
      const response = await loginService(username, password);
      const userId = response.headers['loginid'];
      setLoginId(userId);
      setIsAuthenticated(true);
    } catch (error) {
      console.error('Login error:', error);
      setIsAuthenticated(false);
    }
  };

  const socialLogin = async (token, loginId) => {
    try {
      localStorage.setItem('accessToken', token);
      setLoginId(loginId);
      setIsAuthenticated(true);
    } catch (error) {
      console.error('Social login error:', error);
      setIsAuthenticated(false);
    }
  };

  const logout = async () => {
    try {
      await logoutService();
      setIsAuthenticated(false);
      setLoginId('');
    } catch (error) {
      console.error('Logout error:', error);
    }
  };

  const adminLogout = async () => {
    try {
      await adminLogoutService();
      setIsAuthenticated(false);
      setLoginId('');
    } catch (error) {
      console.error('Logout error:', error);
    }
  }
  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout, adminLogout, socialLogin, loginId }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};