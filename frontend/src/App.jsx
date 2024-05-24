import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Home from './pages/Home';
import AddBook from './pages/AddBook';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Navbar from './components/Navbar';
import { useAuth } from './hooks/useAuth';
import ExchangeForm from './pages/ExchangeForm';
import ExchangeHistory from './pages/ExchangeHistory';
import ExchangeRequests from './pages/ExchangeRequests';
import AdminPanel from './pages/AdminPanel';
import ScanQr from './pages/ScanQr';

function App() {
    const { token } = useAuth();

    return (
        <>
            <Navbar />
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/signup" element={<Signup />} />
                <Route path="/" element={token ? <Home /> : <Navigate to="/login" />} />
                <Route path="/add-book" element={token ? <AddBook /> : <Navigate to="/login" />} />
                <Route path="/history" element={<ExchangeHistory />} />
                <Route path="/exchange" element={<ExchangeForm />} />
                <Route path="/requests" element={<ExchangeRequests />} />
                <Route path="/admin" element={<AdminPanel />} />
                <Route path="/scan-qr" element={<ScanQr />} />
            </Routes>
        </>
    );
}

export default App;
