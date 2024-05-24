import { useState, useEffect } from 'react';

export function useAuth() {
  const [token, setToken] = useState(() =>
    localStorage.getItem('jwtToken')
  );

  useEffect(() => {
    const onStorage = (e) => {
      if (e.key === 'jwtToken') {
        setToken(e.newValue);
      }
    };
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, []);

  const login = (newToken) => {
    localStorage.setItem('jwtToken', newToken);
    setToken(newToken);
  };

  const logout = () => {
    localStorage.removeItem('jwtToken');
    setToken(null);
  };

  return { token, login, logout };
}

export default useAuth;
